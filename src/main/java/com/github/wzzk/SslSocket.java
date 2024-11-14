package com.github.wzzk;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class SslSocket {

    public static void main(String[] args) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream keyStoreFile = new FileInputStream("src/main/resources/server.keystore")) {
            keyStore.load(keyStoreFile, "p@ssw0rd".toCharArray());
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "p@ssw0rd".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(8443);

        System.out.println("SSL Server started and listening on port 8443...");
        while (true) {
            try (SSLSocket clientSocket = (SSLSocket) serverSocket.accept()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String clientMessage = reader.readLine();
                System.out.println("Received: " + clientMessage);

                writer.println("Hello from SSL Server!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
}
