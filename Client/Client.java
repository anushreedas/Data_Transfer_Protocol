/*
 * Client.java
 *
 * Version:
 * 1.0
 *
 *Revision:
 */

/**
 *
 * This program creates thread of Client which sends a stream of bytes to Server
 *
 * @author  Anushree Das
 *
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.time.Instant;

// class Client extends Thread class
public class Client extends Thread
{
    String path;    // file path of image to send
    String destIP;  // destination IP address

    // constructor for Client class with two parameters
    public Client(String s,String dip){
        path = System.getProperty("user.dir")+"/"+s;
        destIP = dip;
    }

    // override run method from Thread class
    public void run() {
        //Create new socket
        DatagramSocket ds = null;

        // set port number for socket
        try {
            ds = new DatagramSocket(1235);
        } catch (SocketException e) {
            System.out.println("Socket error");
        }

        // set destnination IP address
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(destIP);
        } catch (UnknownHostException e) {
            System.out.println("Destination IP address error");
        }


        // set path to image
        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Image File error");
        }

        // read image as array of bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "jpg", bos );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // store image bytes in buffer
        byte [] imagebytes = bos.toByteArray();

        // new bytes array for UDP packet of length 1024
        byte[] buff = new byte[1024];
        // index for buff
        int c=0;

        // loops through all bytes in image bytes
        for(int i=0;i<imagebytes.length;i++){
            // store byte from imagebytes to buff
            buff[c] = imagebytes[i];
            //increment c
            c++;
            // if buff array is filled completely or the imagebytes are over
            // send UDP packet to server
            if(i!=0 && ((i+1)%1024==0 || i==imagebytes.length-1)){

                // create UDP packet data array
                UDPPacket p = new UDPPacket();
                // create packet
                DatagramPacket packet = new DatagramPacket(p.data(8080,1234,c,0,buff), p.data(8080,1234,c,0,buff).length, ip, 8080);
                // clear buff
                buff = new byte[1024];
                // set buff index as 0
                c=0;
                //send packet to server
                try {
                    ds.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // start timer
                Instant first = Instant.now();
                System.out.println("Sent a mini-packet");

                // initialize empty packet
                DatagramPacket UDPReceive = null;

                // loop until positive acknowledgement is received
                while (true) {
                    // stop timer
                    Instant second = Instant.now();
                    // calculate duration
                    Duration duration = Duration.between(first, second);
                    // if duration is greater than timeout then resend packet
                    // timeout is 10 seconds here
                    if (duration.getSeconds() > 10) {
                        // retransmit packet
                        try {
                            System.out.println("Retransmitting..");
                            ds.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // restart timer
                        first = Instant.now();
                    }


                    // receive packet
                    byte[] buff2 = new byte[2048];
                    UDPReceive = new DatagramPacket(buff2, buff2.length);
                    try {
                        ds.receive(UDPReceive);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Client ACK received");
                    // flag to check if checksum recieved is equal to checksum calculated
                    boolean eq = true;
                    //check if checksum recieved is equal to checksum calculated
                    for(int j =0; j<2;j++){
                        if(p.checks[j]!=buff2[j+8])
                            eq =false;
                    }
                    //if checksums are equal then break from loop
                    if(eq==true){
                        System.out.println("Client ACK correct");
                        break;
                    }
                    //if checksums are not equal then retransmit packet
                    else{
                        try {
                            System.out.println("Client ACK incorrect");
                            System.out.println("Retransmitting..");
                            // retransmit packet
                            ds.send(packet);
                            // restart timer
                            first = Instant.now();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

        // to end transmission
        System.out.println("End transmission");
        byte buf[] = null;

        String inp = "end";
        buf = inp.getBytes();
        // send data packet with data as "end"
        DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 8080);
        // send packet to server
        try {
            ds.send(DpSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates thread for class CLient and starts it
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String args[]) throws UnknownHostException {
        if(args.length>0){
            // create Client thread and start
            Client client = new Client(args[1],args[0]);
            client.start();

        }
    }

}
