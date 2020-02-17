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



//            System.out.println("Text [Byte Format] : " + Arrays.toString(text));


            // Encrypt the text
            this.encryptedText = desCipher.doFinal(text);
            byte[] hmac = HMAC.calcHmacSha256(encryptedText);
            FileOutputStream hmacfos = new FileOutputStream("HMAC.txt");
            ObjectOutputStream hmacoos = new ObjectOutputStream(hmacfos);
            hmacoos.writeObject(hmac);
            System.out.print("Key is: " + key + "\n");
            System.out.println("Text to send: " + input);
            //write code to display HMAC
            System.out.println("Cipher : " + desCipher);
            System.out.print("Encrypted text : " + Arrays.toString(encryptedText) + "\n");



        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return this.encryptedText;
    }

    public String decrypt(byte[] input) {
        String res = "";
        try {
            // Create the cipher
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            FileInputStream fis = new FileInputStream("key.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.key = (SecretKey) ois.readObject();
            desCipher.init(Cipher.DECRYPT_MODE, key);

            // Decrypt the text
             this.decryptedText = desCipher.doFinal(input);


            //write code to display HMAC
//            FileInputStream hmacfis = new FileInputStream("HMAC.txt");
//            ObjectInputStream hmacois = new ObjectInputStream(hmacfis);
//            byte[] fileHmac = (byte[]) hmacois.readObject();
//            byte[] generatedHmac = HMAC.calcHmacSha256(decryptedText);
//            if(fileHmac == generatedHmac)

                res = new String(this.decryptedText);
                System.out.print("Received text: " + Arrays.toString(input) + "\n");
                System.out.println("Cipher : " + desCipher);
                System.out.println("Text Decrypted : " + (res));


        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;

    }
}
