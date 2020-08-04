/*
 * UDPPacket.java
 *
 * Version:
 * 1.0
 *
 *Revision:
 */

/**
 *
 * This program creates data for UDP packet
 *
 * @author  Anushree Das
 *
 */
public class UDPPacket {

    public  byte srcPort[];     // source port field in UDP packet
    public  byte destPort[];    // destination port field in UDP packet
    public  byte len[];         // length field in UDP packet
    public  byte checks[];      // checksum field in UDP packet

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
     * creates UDP packet and converts it to bytes array to be sent in packet
     *
     * @param srcport    source port field in UDP packet
     * @param destport   destination port field in UDP packet
     * @param length     length field in UDP packet
     * @param checksum   checksum field in UDP packet
     * @param buff       payload in UDP packet
     * @return          UDP packet as array off bytes
     */
    public  byte[] data(int srcport, int destport, int length, int checksum, byte buff[]){
        //length of whole packet
        int lengthInt =8 + length;

        // new array to store UDP packet
        // length of array is the length of payload + 8(header length)
        byte[] d = new byte[(lengthInt)];

        // index for d array
        int c=0;

        // source port field in UDP packet
        srcPort= convertToBytes(srcport);
        // add source port to d
        for(int i=0;i<2;i++){
            d[c] = srcPort[i];
            c++;
        }

        // destination port field in UDP packet
        destPort= convertToBytes(destport);
        // add destination port to d
        for(int i=0;i<2;i++){
            d[c] = destPort[i];
            c++;
        }

        // length field in UDP packet
        len= convertToBytes(lengthInt);
        // add length to d
        for(int i=0;i<2;i++){
            d[c] = len[i];
            c++;
        }


        // checksum field in UDP packet
        checks= convertToBytes(getChecksum(buff));
        // add checksum to d
        for(int i=0;i<2;i++){
            d[c] = checks[i];
            c++;
        }
        // add data to d
        for(int i=0;c<length+8;i++){
            d[c] = buff[i];
            c++;
        }

        // return UDP packet as array off bytes
        return d;
    }


    /**
     * calculate checksum of data
     *
     * @param data  data whose checkesum needs to be calculated
     * @return  checksum
     */
    public int getChecksum(byte data[]){
        // initialize checksum
        int checksum = 0;
        // initialize sum
        int sum=0;

        // sum all the bytes in data
        for(byte b : data)
            sum += b;
        // find number of bits
        int number_of_bits =
                (int)(Math.floor(Math.log(sum) /
                        Math.log(2))) + 1;

        // find 1's compliment of the sum
        checksum =  ((1 << number_of_bits) - 1) ^ sum;

        // return checksum
        return checksum;

    }

}
