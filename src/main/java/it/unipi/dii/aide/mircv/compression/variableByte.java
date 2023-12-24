package it.unipi.dii.aide.mircv.compression;
import it.unipi.dii.aide.mircv.models.Posting;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


public class variableByte { // -> docid

    // compress an array of integer
    public static byte[] compress(ArrayList<Integer> uncompressed) {

        ArrayList<Integer> compressed = new ArrayList<>();

        for(int uncompressedNumber : uncompressed) {

            // convert the number to binary
            String binary = Integer.toBinaryString(uncompressedNumber);

            // add 0s at the beginning to complete the byte
            while(binary.length() % 7 != 0) {
                binary = "0" + binary;
            }

            // split the binary string in 7-bit chunks removing the first bit
            for(int i = 0; i < binary.length(); i += 7) {
                // check if the chunk is the first one
                if(i == 0) {
                    // add a 0 at the beginning
                    compressed.add(Integer.parseInt("0" + binary.substring(i, i + 7), 2));
                } else {
                    // add a 1 at the beginning
                    compressed.add(Integer.parseInt("1" + binary.substring(i, i + 7), 2));
                }
            }
        }

        // convert the arraylist to a byte array
        byte[] compressedBytes = new byte[compressed.size()];
        for(int i = 0; i < compressed.size(); i++) {
            compressedBytes[i] = compressed.get(i).byteValue();
        }

        return compressedBytes;

    }

    // decompress an array of bytes
    public static ArrayList<Integer> decompress(byte[] compressed) {

        ArrayList<Integer> decompressed = new ArrayList<>();
        StringBuilder binaryBuilder = new StringBuilder();
        StringBuilder supportBuilder = new StringBuilder();

        int decodedNumber;

        // convert the bytes to a binary string
        for (byte b : compressed) {
            binaryBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        // decoding the binary string
        while (binaryBuilder.length() > 0) {
            int firstBit = Character.getNumericValue(binaryBuilder.charAt(0));

            // if the first bit is 0, it is the first chunk of the number
            if (firstBit == 0) {
                int groupLength = Math.min(8, binaryBuilder.length());
                String group = binaryBuilder.substring(0, groupLength);
                binaryBuilder.delete(0, groupLength);

                // check if the support builder is empty
                if (supportBuilder.length() == 0) {
                    // add the binary group to the support builder
                    supportBuilder.append(group);
                }
                else {
                    // convert support builder to an integer
                    decodedNumber = Integer.parseInt(supportBuilder.toString(), 2);
                    decompressed.add(decodedNumber);
                    // reset the support builder
                    supportBuilder.setLength(0);
                    // add the binary group to the support builder
                    supportBuilder.append(group);
                }

            } else {
                // if the first bit is 1, it is not the first chunk of the number
                int groupLength = Math.min(7, binaryBuilder.length() - 1);
                String group = binaryBuilder.substring(1, 1 + groupLength);
                binaryBuilder.delete(0, 1 + groupLength);

                // add a 1 at the beginning of the group
                //group = "1" + group;

                // add the binary group to the support builder at the end
                supportBuilder.append(group);
            }
        }

        // convert support builder to an integer
        decodedNumber = Integer.parseInt(supportBuilder.toString(), 2);
        decompressed.add(decodedNumber);
        // reset the support builder
        supportBuilder.setLength(0);

        return decompressed;
    }

    public static int writeDocIdCompressed(ArrayList<Posting> postingList, FileChannel channelDocId) throws IOException {
        ArrayList<Integer> docIdsUncompressed = new ArrayList<>();

        // get the docIds
        for (Posting posting : postingList) {
            docIdsUncompressed.add(posting.getDocID());
        }

        // compress the docIds
        byte[] compressed = compress(docIdsUncompressed);

        channelDocId.position(channelDocId.size());
        ByteBuffer DocIdByteBuffer = ByteBuffer.wrap(compressed);

        // write buffers to the channels
        while (DocIdByteBuffer.hasRemaining())
            channelDocId.write(DocIdByteBuffer);

        return compressed.length;
    }

    public static ArrayList<Integer> readDocIdsCompressed(FileChannel channelDocId, long offsetDocId, int DocIdLen) throws IOException {
        try {
            // create a buffer
            ByteBuffer docsByteBuffer = ByteBuffer.allocate(DocIdLen);

            // set position
            channelDocId.position(offsetDocId);

            while (docsByteBuffer.hasRemaining())
                channelDocId.read(docsByteBuffer);

            docsByteBuffer.rewind(); // reset the buffer position to 0

            ArrayList<Integer> docIdsDecompressed = decompress(docsByteBuffer.array());

            return docIdsDecompressed;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
