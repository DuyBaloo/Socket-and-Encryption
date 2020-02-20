package com.company;

// A Java program for a Client
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Client
{
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;
    private DataInputStream  in  = null;


    // constructor to put ip address and port
    public Client(String address, int port)
    {
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input  = new DataInputStream(System.in);

            // takes input from the server socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            // sends output to the socket
            out    = new DataOutputStream(socket.getOutputStream());
        } catch(IOException u)
        {
            System.out.println(u);
        }

        // string to read message from input
        String line = "";
        byte[] encryptOut;
        byte[] encryptIn;

        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {
                DES handler = new DES();

                assert input != null;
                line = input.readLine();
                encryptOut = handler.encrypt(line);
                assert out != null;
                out.write(encryptOut);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                baos.write(buffer, 0 , in.read(buffer));

                encryptIn = baos.toByteArray();

                handler.decrypt(encryptIn);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }
    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please input your ip: ");
//        String ip = scanner.nextLine();
        Client client = new Client("10.110.35.205" , 5000);


    }
}
