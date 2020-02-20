package com.company;

// A Java program for a Server
import javax.crypto.SecretKey;
import java.net.*;
import java.io.*;
import java.util.Arrays;

// constructor with port
public class Server
{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    DataOutputStream out = null;
    private DataInputStream input       =  null;

    // constructor with port
    public Server(int port)
    {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");
            input  = new DataInputStream(System.in);


            // takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            out    = new DataOutputStream(socket.getOutputStream());


            String line = "";
            byte[] encryptOut;
            byte[] encryptIn;

            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    DES handler = new DES();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    baos.write(buffer, 0 , in.read(buffer));

                    encryptIn = baos.toByteArray();
                    System.out.println("debug received message" + Arrays.toString(encryptIn));
                    handler.decrypt(encryptIn);

                    line = input.readLine();
                    encryptOut = handler.encrypt(line);
                    out.write(encryptOut);
                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            // close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
}
