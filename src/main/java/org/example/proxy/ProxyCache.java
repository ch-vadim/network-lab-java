package org.example.proxy;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ProxyCache {
    private final ConcurrentHashMap<String, byte[]> cache = new ConcurrentHashMap<>();
    private final int port;

    public ProxyCache(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Proxy Server is running on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClientRequest(clientSocket)).start();
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream outputStream = clientSocket.getOutputStream()) {

            String requestLine = reader.readLine();
            if (requestLine == null) return;

            System.out.println("Request: " + requestLine);
            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) return;

            String method = tokens[0];
            if (!"GET".equalsIgnoreCase(method)) {
                String response = "HTTP/1.0 405 Method Not Allowed\r\n\r\n";
                outputStream.write(response.getBytes());
                return;
            }

            URL url = new URL("http://"+tokens[1].substring(1)); // Remove the leading '/'
            byte[] responseBytes;

            if (cache.containsKey(url.toString())) {
                responseBytes = cache.get(url.toString());
            } else {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (InputStream is = con.getInputStream()) {
                    byte[] byteChunk = new byte[4096];
                    int n;

                    while ((n = is.read(byteChunk)) > 0) {
                        baos.write(byteChunk, 0, n);
                    }
                }
                responseBytes = baos.toByteArray();
                cache.put(url.toString(), responseBytes);
            }

            outputStream.write(("HTTP/1.0 200 OK\r\n").getBytes());
            outputStream.write(("Content-Length: " + responseBytes.length + "\r\n").getBytes());
            outputStream.write("\r\n".getBytes());
            outputStream.write(responseBytes);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ProxyCache proxyServer = new ProxyCache(port);
        proxyServer.start();
    }
}