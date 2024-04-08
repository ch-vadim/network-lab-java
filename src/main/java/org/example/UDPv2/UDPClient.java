package org.example.UDPv2;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UDPClient extends UDPPinger implements Runnable {
    /** Пинг хоста */
    String remoteHost;
    /** Номер порта удаленного хоста */
    int remotePort;

    /** Сколько пинг-команд отправлено */
    static final int NUM_PINGS = 10;

    /** 1 секунда таймаута до ожидания ответа*/
    static final int TIMEOUT = 1000;


    /** Создание объекта UDPClient. */
    public UDPClient(String host, int port) {
        remoteHost = host;
        remotePort = port;
    }

    /** Главный код процесса пингующего клиента. */
    public void run() {
        /* Создание сокета. Мы не беспокоимся о локальном порте, который используем. */
        createSocket();
        try {
            socket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            System.out.println("Ошибка установки времени ожидания TIMEOUT: " + e);
        }


        int index = 0;
        while (index<5) {

            Date now = new Date();
            String message = "PING " + index + " " + now.getTime() + " ";

            PingMessage ping = null;
            try {
                ping = new PingMessage(InetAddress.getByName(remoteHost),
                        remotePort, message);
            } catch (UnknownHostException e) {
                System.out.println("Cannot find host: " + e);
            }
            if (index!=2) {
                sendPing(ping);
            }
            index++;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }


    /** Главная функция. Считать аргументы командной строки и начать процесс клиента.*/
    public static void main(String args[]) {
        String host = null;
        int port = 0;

        /* Получить хост и номер порта из командной строки*/
        try {
            host = "localhost";
                    //args[0];
            port = 1234;
                    //Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Необходимы два аргумента: remoteHost remotePort");
            System.exit(-1);
        } catch (NumberFormatException e) {
            System.out.println("Пожалуйста, укажите номер порта (целое число).");
            System.exit(-1);
        }
        System.out.println("Соединение с хостом " + host + " на порт " + port);

        UDPClient Client = new UDPClient(host, port);
        Thread server = new Thread(new UDPServer());
        server.start();
        Client.run();
    }
}