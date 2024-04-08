package org.example.UDPv2;

import java.io.IOException;
import java.net.*;

public class UDPPinger {
    /** Сокет, который мы используем. */
    DatagramSocket socket;

    /** Максимальная длина PING-сообщения. */
    static final int MAX_PING_LEN = 1024;

    /** Создание сокета для отправки UDP-сообщений */
    public void createSocket() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Ошибка создания сокета: " + e);
        }
    }

    /** Создание сокета для получения UDP-сообщений. Этот сокет должен быть пограничным с заданным портом. */
    public void createSocket(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Ошибка создания сокета: " + e);
        }
    }

    /** Отправить UDP ping-сообщение, которое задано в качестве аргумента. */
    public void sendPing(PingMessage ping) {
        InetAddress host = ping.getHost();
        int port = ping.getPort();
        String message = ping.getContents();

        try {
            /* Создание дейтаграммы для пакета адресованного получателю */
            DatagramPacket packet =
                    new DatagramPacket(message.getBytes(), message.length(),
                            host, port);

            /*Отправить пакет */
            socket.send(packet);
            //System.out.println("Сообщение отправлено хосту  " + host + ":" + port);
        } catch (IOException e) {
            System.out.println("Ошибка отправки пакета: " + e);
        }
    }

    /** Получить UDP ping-сообщение и вернуть обратно полученное сообщение. Мы используем исключение, чтобы показать, что таймаут истек. Это может произойти, когда сообщение потеряно в сети.*/
    public PingMessage receivePing() throws SocketTimeoutException {
        /* Создание пакета для получения пакета */
        byte recvBuf[] = new byte[MAX_PING_LEN];
        DatagramPacket recvPacket =
                new DatagramPacket(recvBuf, MAX_PING_LEN);
        PingMessage reply = null;

        /* Чтение сообщение из сокета. */
        try {
            socket.receive(recvPacket);


            String recvMsg = new String(recvPacket.getData());
            reply = new PingMessage(recvPacket.getAddress(),
                    recvPacket.getPort(),
                    recvMsg);

        } catch (SocketTimeoutException e) {
            /* Примечание: Поскольку сокет для сокета интервал ожидания истек, мы можем использовать исключение SocketTimeoutException. Вызов функции необходим, чтобы знать об этом таймауте и когда это прекратить. Но, SocketTimeoutException - это подкласс класса IOException, который также считывает ошибки сокетов так, что нам необходимо добавить SocketTimeoutException здесь и пройти его.*/
            throw e;
        } catch (IOException e) {
            System.out.println("Ошибка чтения из сокета: " + e);
        }
        return reply;
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
