package com.log_collector.java;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BufferedReverseLineStreamTest {

    @Test
    public void testReadFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("hello.txt").getFile());
        BufferedReverseLineStream stream = new BufferedReverseLineStream(file);
        char ch1 = (char) stream.read();
        char ch2 =  (char) stream.read();
        Assert.assertEquals(ch1, 'T');
        Assert.assertEquals(ch2, 'h');
    }

    @Test
    public void testReadEmptyFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("empty.txt").getFile());
        BufferedReverseLineStream stream = new BufferedReverseLineStream(file);
        int b = stream.read();
        Assert.assertEquals(-1, b);
        b = stream.read();
        Assert.assertEquals(-1, b);
    }

    @Test
    public void testWithRandomChars() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("random_chars.txt").getFile());
        BufferedReverseLineStream stream = new BufferedReverseLineStream(file);
        int b = stream.read();
        System.out.println(b);
    }
}