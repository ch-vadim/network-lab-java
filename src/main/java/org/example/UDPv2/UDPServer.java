package org.example.UDPv2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class UDPServer implements Runnable {
    @Override
    public void run() {
        main(null);
    }
    public static long lastTime = -1;

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(1234); // Порт для прослушивания
            System.out.println("Сервер запущен. Ожидание пакетов...");

            byte[] buffer = new byte[1024]; // Буфер для хранения данных
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            Thread checker = new Thread(new Checker());
            checker.start();

            while (true) {
                serverSocket.receive(packet); // Ожидание пакета
                String request = new String(packet.getData(), 0, packet.getLength());
                lastTime = Long.parseLong(request.split(" ")[2]);
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                System.out.println("Получен пакет в момент времени = " + lastTime);

                if (request.split(" ")[0].trim().equalsIgnoreCase("ping")) {
                    byte[] response = packet.getData(); // Ответ "pong"
                    serverSocket.send(packet); // Отправка ответа клиенту
                } else {
                    byte[] errorResponse = "Некорректный запрос".getBytes();
                    DatagramPacket errorPacket = new DatagramPacket(errorResponse, errorResponse.length, clientAddress, clientPort);
                    serverSocket.send(errorPacket); // Отправка сообщения об ошибке клиенту
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class Checker implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (lastTime != -1 && lastTime +  1100< new Date().getTime()) {
                    System.out.println("Loss packet");
                }
                if (lastTime != -1 && lastTime +  10000< new Date().getTime()) {
                    System.out.println("Client unavailable");
                    return;
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}