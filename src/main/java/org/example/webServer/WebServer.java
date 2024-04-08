package org.example.webServer;

import java.io.* ;
import java.net.* ;
import java.net.http.*;
import java.util.* ;


public final class WebServer{
    public static void main(String argv[]) throws Exception {

        int port = Integer.parseInt(argv[0]);

        ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connection = socket.accept();
            HttpRequest request = new HttpRequest(connection);

            Thread thread = new Thread(request);

            thread.start();
        }
    }

}
