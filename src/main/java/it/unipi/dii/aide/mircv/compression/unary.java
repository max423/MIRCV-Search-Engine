package it.unipi.dii.aide.mircv.compression;

import java.util.ArrayList;

public class unary {

    // compress an array of integer
    public static byte[] compress(ArrayList<Integer> uncompressed) {

        // count the number of bits
        int bits = 0;
        for (int i = 0; i < uncompressed.size(); i++) {
            // check if 0
            if (uncompressed.get(i) <= 0) {
                System.out.println("skipped element because <= 0");
                continue;
            }
            bits += uncompressed.get(i);
        }

        // allocate the byte array
        byte[] compressed = new byte[(int) Math.ceil(bits / 8.0)];

        // compress each number n with n-1 0s and a 1 at the end, except for 1 that is compressed with a single 1
        int index = 0;
        int count = 0;
        for (int i = 0; i < uncompressed.size(); i++) {
            // check if 1
            if (uncompressed.get(i) == 1) {
                // set the bit at position count
                compressed[index] |= 1 << (7 - count);
                count++;
                // check if the byte is full
                if (count == 8) {
                    // reset count and move to the next byte
                    index++;
                    count = 0;
                }
                continue;
            }
            // compress the number
            for (int j = 0; j < uncompressed.get(i) - 1; j++) {
                // set to 0 the bit at position count
                compressed[index] |= 0 << (7 - count);
                count++;
                // check if the byte is full
                if (count == 8) {
                    // reset count and move to the next byte
                    index++;
                    count = 0;
                }
            }
            // add a 1 at the end of the sequence
            compressed[index] |= 1 << (7 - count);
            count++;
            // check if the byte is full
            if (count == 8) {
                // reset count and move to the next byte
                index++;
                count = 0;
            }
        }
        return compressed;
    }

    // decompress an array of bytes
    public static ArrayList<Integer> decompress(byte[] compressed) {

        // allocate the array
        ArrayList<Integer> decompressed = new ArrayList<>();

        // decompress
        int count = 0;
        for (int i = 0; i < compressed.length; i++) {
            for (int j = 0; j < 8; j++) {
                // check if the bit at position j is 0
                if ((compressed[i] & (1 << (7 - j))) == 0) {
                    count++;
                } else {
                    // add the number of 0s + 1
                    decompressed.add(count + 1);
                    count = 0;
                }
            }
        }

        return decompressed;
    }

}
