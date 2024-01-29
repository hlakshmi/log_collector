package com.log_collector.java;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.powermock.core.classloader.annotations.PrepareForTest;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest(BufferedReverseLineStreamReader.class)
public class BufferedReverseLineStreamReaderTest {

    @Test
    public void testReadLinesWithRandomChars() throws IOException, InvalidPathException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("random_chars.txt").getFile());

        BufferedReverseLineStreamReader reader = spy(new BufferedReverseLineStreamReader());
        when(reader.isValidPath(file.getCanonicalPath())).thenReturn(true);
        List<String> lines = reader.readLines(file.getPath(), 10, new ArrayList<>());
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("â¥ÇÁÇ©ÇÁ3ÉÅÅ[É^Å[ÇÃÇ∆Ç±ÇÎÇ≈äCÇ™ÉAÉâÉCÉîÇ…Ç»Ç¡ÇΩÇÊÅI", lines.get(0));
        Assert.assertEquals("01:17:12;18 01:17:20;26 00:00:08;09 Ç±ÇÍÇÁÇÃÉCÉìÉåÉbÉgÇ\uF8FFÇøÇÂÇ¡Ç∆âÒÇ¡ÇƒÇ≥ÅAÇ¢ÇÎÇÒÇ»É]Å[ÉìÇ÷Ç¢Ç¡ÇƒÇ›ÇƒÇsÇÖÇíÇíÇÅÇâÇéÇ ÷Ç‡Ç¡Ç∆ãﬂÇ≠çsÇ¡ÇƒÇ≥ÅAÉwÉäÇÃñ≥ë éûä‘Ç\uF8FFè≠Ç»Ç≠Ç∑ÇÈÅB", lines.get(1));
    }

    @Test
    public void testReadLines() throws IOException, InvalidPathException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("hello.txt").getFile());

        BufferedReverseLineStreamReader reader = spy(new BufferedReverseLineStreamReader());
        when(reader.isValidPath(file.getCanonicalPath())).thenReturn(true);
        List<String> lines = reader.readLines(file.getPath(), 10, new ArrayList<>());
        Assert.assertEquals(2, lines.size());
        Assert.assertEquals("This is a new line", lines.get(0));
        Assert.assertEquals("Hello World", lines.get(1));
    }
}
