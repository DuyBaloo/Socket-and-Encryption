package com.company;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DES {

    KeyGenerator keygenerator;
    SecretKey key;

    Cipher desCipher;
    byte[] encryptedText;
    byte[] decryptedText;
    int msgLength = 0;
    int hmacLength = 0;
    byte[] joinHmac;

    public DES() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey key = keyGenerator.generateKey();
            this.keygenerator = keyGenerator;
            this.key = key;


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String input) {
        try {
            // Create the cipher
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            FileOutputStream fos = new FileOutputStream("key.txt");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(key);
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, key);
            //sensitive information
            byte[] text = input.getBytes();
            System.out.println("debug: " + Arrays.toString(text));
            this.msgLength = text.length;
            FileOutputStream numos = new FileOutputStream("msgLength.txt");

            DataOutputStream dos = new DataOutputStream(numos);
            dos.writeInt(msgLength);
            System.out.println("debug: " + msgLength);
            byte[] hmac = HMAC.calcHmacSha256(text);
            this.hmacLength = hmac.length;
            FileOutputStream numos1 = new FileOutputStream("hmacLength.txt");

            DataOutputStream dos1 = new DataOutputStream(numos1);
            dos1.writeInt(hmacLength);
            byte[] newHmac = new byte[msgLength+hmacLength];
            int count = 0;

            for(int i = 0; i < text.length; i++) {
                newHmac[i] = text[i];
                count++;
            }
            for(int j = 0; j < hmac.length;j++) {
                newHmac[count++] = hmac[j];
            }
            this.joinHmac = newHmac;


                    // Encrypt the text
            this.encryptedText = desCipher.doFinal(joinHmac);
//            FileOutputStream hmacfos = new FileOutputStream("HMAC.txt");
//            ObjectOutputStream hmacoos = new ObjectOutputStream(hmacfos);
//            hmacoos.writeObject(hmac);
            System.out.print("Key is: " + key + "\n");
            System.out.println("Text to send: " + input);
            System.out.println("Sent HMAC : " + Arrays.toString(joinHmac));
            System.out.println("Text used to generate HMAC : " + Arrays.toString(text));
            //write code to display HMAC
            System.out.println("Cipher : " + desCipher);
            System.out.print("Encrypted text : " + Arrays.toString(encryptedText) + "\n");
            System.out.println("***********************************************************************************");


        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return this.encryptedText;
    }

    public void decrypt(byte[] input) {
        String res = "";
        try {
            // Create the cipher
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            FileInputStream fis = new FileInputStream("key.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.key = (SecretKey) ois.readObject();
            desCipher.init(Cipher.DECRYPT_MODE, key);

            int count = 0;
            FileInputStream numis = new FileInputStream("msgLength.txt");
            DataInputStream dis = new DataInputStream(numis);
            this.msgLength = dis.readInt();

            FileInputStream numis1 = new FileInputStream("hmacLength.txt");
            DataInputStream dis1 = new DataInputStream(numis1);
            this.hmacLength = dis1.readInt();
            byte[] receivedMsg = new byte[msgLength];
            byte[] receivedHmac = new byte[hmacLength];
            // Decrypt the text
            System.out.println("debug msg length1: " + msgLength);
            this.decryptedText = desCipher.doFinal(input);
            byte[] passedByte = this.decryptedText;
            System.out.println("debug msg length2: " + msgLength);
            for (int i = 0; i < msgLength; i++)
             {
                 receivedMsg[i] = passedByte[i];
                 count++;
             }
            for(int j = 0; j < hmacLength; j++) {
                receivedHmac[j] = passedByte[count++];
            }

            byte[] finalHMAC = HMAC.calcHmacSha256(receivedMsg);
            if(Arrays.equals(receivedHmac, finalHMAC))
            {
                res = new String(receivedMsg);
                System.out.print("Received text: " + Arrays.toString(input) + "\n");
                System.out.println("Cipher : " + desCipher);
                System.out.println("HMAC generated with received text: " + Arrays.toString(finalHMAC));
                System.out.println("HMAC received: " + Arrays.toString(receivedHmac));

                System.out.println("Text Decrypted : " + (res));
                System.out.println("***********************************************************************************");
            }
            else
            {
                System.out.println("HMAC does not match.");
                System.out.println("msg received: " + Arrays.toString(receivedMsg));
                System.out.println("decrypted whole: " + Arrays.toString(decryptedText));
                System.out.println("decrypted passed: " + Arrays.toString(passedByte));
                System.out.println("msg length: " + msgLength);
                System.out.println("hmac length: " + hmacLength);


                System.out.println("HMAC received: " + Arrays.toString(receivedHmac));
                System.out.println("HMAC passed: " + Arrays.toString(finalHMAC));

            }



        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
