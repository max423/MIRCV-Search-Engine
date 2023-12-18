package it.unipi.dii.aide.mircv.compression;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class unaryTest {

    @Test
    void compress() {
        // Test input array: [1, 2, 3, 1, 4, 1]
        // Expected compressed binary: 10100110 00110000
        ArrayList<Integer> input = new ArrayList<>();
        input.add(1);
        input.add(2);
        input.add(3);
        input.add(1);
        input.add(4);
        input.add(1);

        // Expected compressed bytes
        byte[] expectedCompressed = {(byte) 0b10100110, (byte) 0b00110000};

        // Compress the input
        byte[] compressed = unary.compress(input);

        // Assert that the compressed array is equal to the expected compressed array
        assertArrayEquals(expectedCompressed, compressed);
    }

    @Test
    void decompress() {
        // Test input array: [1, 2, 3, 1, 4, 1]
        // Expected compressed binary: 10100110 00110000
        ArrayList<Integer> expectedDecompressed = new ArrayList<>();
        expectedDecompressed.add(1);
        expectedDecompressed.add(2);
        expectedDecompressed.add(3);
        expectedDecompressed.add(1);
        expectedDecompressed.add(4);
        expectedDecompressed.add(1);

        // Compress the input
        byte[] compressed = {(byte) 0b10100110, (byte) 0b00110000};

        // Decompress the compressed array
        ArrayList<Integer> decompressed = unary.decompress(compressed);

        // Assert that the decompressed array is equal to the expected decompressed array
        assertEquals(expectedDecompressed, decompressed);
    }
}