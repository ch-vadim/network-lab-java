package org.example.ping;

import java.io.IOException;
import java.net.InetAddress;

public class Ping {
    public static void main(String[] args) {


        String hostname = args[0];
        try {
            InetAddress host = InetAddress.getByName(hostname);
            while (true) {
                long startTime = System.currentTimeMillis();
                if (host.isReachable(5000)) {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;
                    System.out.println(hostname + " is reachable (" + elapsedTime + " ms)");
                } else {
                    System.out.println(hostname + " is not reachable");
                }
                Thread.sleep(1000); // Задержка в 1 секунду перед следующим ping-запросом
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("InterruptedException: " + e.getMessage());
        }
    }
}