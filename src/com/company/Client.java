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
        String line2;
        byte[] encryptOut;
        byte[] encryptIn;

        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {
                DES handler = new DES();

                line = input.readLine();
                encryptOut = handler.encrypt(line);
                HMAC.calcHmacSha256(encryptOut);
                out.write(encryptOut);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                baos.write(buffer, 0 , in.read(buffer));

                encryptIn = baos.toByteArray();
//                System.out.println(line2);
                FileInputStream fis = new FileInputStream("HMAC.txt");
                ObjectInputStream ois = new ObjectInputStream(fis);
                byte[] fileHmac = (byte[]) ois.readObject();
                System.out.print("HMAC from Client: " + Arrays.toString(fileHmac) + "\n");
                byte[] passHMAC = HMAC.calcHmacSha256(encryptIn);
                System.out.print("HMAC from Server: " + Arrays.toString(passHMAC) + "\n");

                if(Arrays.equals(fileHmac, passHMAC))
                {
                    handler.decrypt(encryptIn);
                }
                else
                {
                    System.out.println("HMAC does not match.");
                }

//                line2 = handler.decrypt(encryptIn);
//                System.out.println(line2);

            }
            catch(IOException | ClassNotFoundException i)
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input your ip: ");
        String ip = scanner.nextLine();
        Client client = new Client(ip, 5000);


    }
}
