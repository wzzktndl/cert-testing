package com.github.wzzk;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;

public class SslClient {

    public static void main(String[] args) throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreFile = new FileInputStream("src/main/resources/client-truststore.jks")) {
            trustStore.load(trustStoreFile, "p@ssw0rd".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket("localhost", 8443)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println("Hello from SSL Client!");
            String serverResponse = reader.readLine();
            System.out.println("Received from server: " + serverResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
