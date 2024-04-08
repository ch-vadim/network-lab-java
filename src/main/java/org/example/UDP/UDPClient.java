package org.example.UDP;

import java.net.*;
import java.io.*;
import java.util.*;

public class UDPClient implements Runnable {
    String remoteHost;
    int remotePort;
    DatagramSocket socket;
    static final int MAX_PING_LEN = 1024;
    static final int NUM_PINGS = 10;
    int numReplies = 0;
    static boolean[] replies = new boolean[NUM_PINGS];
    static long[] rtt = new long[NUM_PINGS];

    static final int TIMEOUT = 1000;
    static final int REPLY_TIMEOUT = 5000;
    public UDPClient(String host, int port) {
        remoteHost = host;
        remotePort = port;
    }

    public void run() {
        createSocket();
        try {
            socket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < NUM_PINGS; i++) {
            Date now = new Date();
            String message = "PING " + i + " " + now.getTime() + " ";
            replies[i] = false;
            rtt[i] = 1000000;
            PingMessage ping = null;

            try {
                ping = new PingMessage(InetAddress.getByName(remoteHost),
                        remotePort, message);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            sendPing(ping);

            try {
                PingMessage reply = receivePing();
                handleReply(reply);
            } catch (SocketTimeoutException ignored) {

            }
        }

        try {
            socket.setSoTimeout(REPLY_TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (numReplies < NUM_PINGS) {
            try {
                PingMessage reply = receivePing();
                handleReply(reply);
            } catch (SocketTimeoutException e) {
                numReplies = NUM_PINGS;
            }
        }


        long min  = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long average = Arrays.stream(rtt).sum()/rtt.length;

        for (int i = 0; i < NUM_PINGS; i++) {
            min = Math.min(min, rtt[i]);
            max = Math.max(max, rtt[i]);

        }
        System.out.println("Отправлено = " + NUM_PINGS + ", получено = " + numReplies + ", потеряно  = " + (NUM_PINGS-numReplies));
        System.out.println("Процент потерь = " + (((double)NUM_PINGS-numReplies)/NUM_PINGS)*100.0 + "%");
        System.out.println("Минимальное = " + min + "мсек, Максимальное = " + max + "мсек, Среднее = " + average + "мсек");

    }

    private void handleReply(PingMessage pm) {
        String reply = pm.getContents();
        String[] tmp = reply.split(" ");
        int pingNumber = Integer.parseInt(tmp[1]);
        long then = Long.parseLong(tmp[2]);
        replies[pingNumber] = true;
        Date now = new Date();
        rtt[pingNumber] = now.getTime() - then;
        numReplies++;
        System.out.println("Ответ от "+ pm.inetAddress + ": время =" + rtt[pingNumber]);
    }
    public void createSocket() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void sendPing(PingMessage ping) {
        InetAddress host = ping.getHost();
        int port = ping.getPort();
        String message = ping.getContents();

        try {
            DatagramPacket packet =
                    new DatagramPacket(message.getBytes(), message.length(),
                            host, port);

            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PingMessage receivePing() throws SocketTimeoutException {
        byte[] recvBuf = new byte[MAX_PING_LEN];
        DatagramPacket recvPacket =
                new DatagramPacket(recvBuf, MAX_PING_LEN);
        PingMessage reply = null;

        try {
            socket.receive(recvPacket);

            String recvMsg = new String(recvPacket.getData());
            reply = new PingMessage(recvPacket.getAddress(),
                    recvPacket.getPort(),
                    recvMsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return reply;
    }


    public static void main(String args[]) {
        String host = null;
        int port = 0;
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Необходимы два аргумента: remoteHost remotePort");
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.out.println("Пожалуйста, укажите номер порта (целое число).");
            System.exit(-1);
        }

        UDPClient Client = new UDPClient(host, port);
        Thread server = new Thread(new UDPServer(1234));
        server.start();
        Client.run();
    }
}

class PingMessage {
    InetAddress inetAddress;
    int port;
    String contents;

    public PingMessage(InetAddress inetAddress, int port, String message) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.contents = message;
    }

    public InetAddress getHost() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    public String getContents() {
        return contents;
    }
}
