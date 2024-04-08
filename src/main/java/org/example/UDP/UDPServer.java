package org.example.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPServer implements Runnable {
    @Override
    public void run() {
        main(null);
    }
    int port;

    public UDPServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(1234);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                serverSocket.receive(packet);
                String request = new String(packet.getData(), 0, packet.getLength());
                if (request.split(" ")[0].trim().equalsIgnoreCase("ping")) {
                    serverSocket.send(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}