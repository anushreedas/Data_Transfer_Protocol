/*
 * Server.java
 *
 * Version:
 * 1.0
 *
 *Revision:
 */

/**
 *
 * This program creates thread of Server which recieves a stream of bytes to Server
 *
 * @author  Anushree Das
 *
 */
import java.io.ByteArrayInputStream;
import java.net.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
// class Client extends Thread class

public class Server extends Thread
{
    public static byte[] receive = new byte[65535]; // recieved data
    public static int c=0;  // index for recieved data


    /**
     * converts integer to array of bytes of 2 length
     * @param integer   integer to be converted
     * @return          array of bytes
     */
    byte[] convertToBytes(int integer){
        // initialize array of bytes
        byte data[] =new byte[2];
        // value of first byte
        data[0] = (byte) (integer & 0xFF);
        // value of second byte
        data[1] = (byte) ((integer >> 8) & 0xFF);

        return data;
    }

    /**
     * adds relevent data from packet to received data array
     * @param buff
     */
    public static void addToImageData(byte buff[]){
        int length = buff.length;

        for(int i=0;i<length;i++){
            receive[c] = buff[i];
            c++;
        }
    }

    /**
     * extract image bytes from packet
     * @param buff
     * @param length
     * @return
     */
    public static byte[] getImageData(byte buff[],int length){
        int len = length - 8;
        byte[] d = new byte[(len)];

        for(int i=0;i<len;i++){
            d[i] = buff[i+8];
        }
        return d;
    }

    // override run method form Thread
    public void run() {
        System.out.println("Server running..");
        try{
            //Create new socket
            DatagramSocket ds = new DatagramSocket(8080);

            DatagramPacket DpReceive = null;

            // loop till transmission ends
            while (true) {
                byte[] buff = new byte[2048];
                // a Datgram Packet to receive the data.
                DpReceive = new DatagramPacket(buff, buff.length);
                // receive packet
                ds.receive(DpReceive);

                // if client sends "end"
                // end transmission
                if (data(buff).toString().equals("end")) {
                    System.out.println("Client sent end.....EXITING");
                    byte[] img = new byte[c];
                    for (int i = 0; i < c; i++) {
                        img[i] = receive[i];
                    }
                    // create image from bytes
                    ByteArrayToImage(img);
                    break;
                }

                // get length field from packet
                int length = ((int) buff[5] << 8) | ((int) buff[4] & 0xFF);

                // get image bytes from packet
                byte imageData[] = getImageData(buff, length);

                UDPPacket p = new UDPPacket();
                // calculate checksum from image bytes
                int checksumOfData = p.getChecksum(imageData);

                // get checksum from packet checksum field
                byte chksumPacket[] = new byte[2];
                chksumPacket = convertToBytes(checksumOfData);
                boolean eq = true;
                for (int i = 0; i < 2; i++) {

                    if (chksumPacket[i] != buff[i + 6])
                        eq = false;
                }
                // if both checksums are equal send ACK
                if (eq == true) {
                    System.out.println("Sending ACK to Client");
                    addToImageData(imageData);
                }
                    // send ACK or NACK
                InetAddress address = DpReceive.getAddress();
                int port = DpReceive.getPort();
                DatagramPacket DpSend = new DatagramPacket(p.data(8080, 1234, 2, 0, chksumPacket), p.data(8080, 1234, 2, 0, chksumPacket).length, address, port);
                ds.send(DpSend);

            }

        }catch(Exception e){
        }
    }

    /**
     * create image from bytes
     *
     * @param data
     * @throws Exception
     */
    public static void ByteArrayToImage(byte data[]) throws Exception {

        System.out.println("Creating image..");
        if(data.length>0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            BufferedImage bImage2 = ImageIO.read(bis);
            File f = new File("output.jpg");
            ImageIO.write(bImage2, "jpg", f);
            String absolute = f.getAbsolutePath();
            System.out.println("Image located at: "+absolute);
      }
    }


    /**
     * convert the byte array
     * data into a string representation.
     * @param a
     * @return
     */
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
    /**
     * Creates thread for class Server and starts it
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String args[]) throws UnknownHostException {

        Server server = new Server();
        server.start();
    }
} 