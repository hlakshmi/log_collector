package com.log_collector.java;

import java.io.*;

public class BufferedReverseLineStream extends InputStream {

    private static final int MAX_LINE_SIZE_BYTES = 1024;
    private static final int DEFAULT_BUFFER_SIZE_BYTES = 1024*1024;

    private RandomAccessFile in;
    private byte[] buffer;
    private byte[] currentLine;

    private int bufferSize;
    private int maxLineSizeBytes;

    private long currFilePos;
    private int currBufferPos;
    private int currLineWritePos;
    private int currLineReadPos;
    private boolean lineBuffered;

    public BufferedReverseLineStream(File file) throws IOException {
        new BufferedReverseLineStream(file, DEFAULT_BUFFER_SIZE_BYTES, MAX_LINE_SIZE_BYTES);
    }

    public BufferedReverseLineStream(File file, int bufferSize, int maxLineSizeBytes) throws IOException {
        this.bufferSize = bufferSize;
        this.maxLineSizeBytes = maxLineSizeBytes;
        init(file);
    }

    @Override
    public int read() throws IOException {
        if(currFilePos < 0 && currBufferPos < 0 && currLineReadPos < 0) {
            return -1;
        }

        if(!lineBuffered) {
            fillCurrentLine();
        }

        if(lineBuffered) {
            return currentLine[currLineReadPos--];
        }
        return 0;
    }

    private void init(File file) throws IOException {
        in = new RandomAccessFile(file, "r");
        currFilePos = file.length() - 1;
        in.seek(currFilePos);
        if(in.readByte() == 0xA) {
            // If the current byte is new line, decrement the move to the previous byte.
            currFilePos--;
        }
        currentLine = new byte[maxLineSizeBytes];
        buffer = new byte[bufferSize];
        currLineWritePos = 0;
        currLineReadPos = 0;
        lineBuffered = false;
        fillBuffer();
        fillCurrentLine();
    }

    private void fillBuffer() throws IOException {
        if(currFilePos < 0) {
            return;
        }

        if(currFilePos < bufferSize) {
            // We don't have bufferSize worth of data left, so we just read everything else into the buffer.
            // buffer position would be set to whatever is the file position is because that's the amount of data that's read into buffer.
            // currFilePos needs to be updated to -1 so that next time when a read is done, we don't do anything as file is done reading.
            in.seek(0);
            in.read(buffer);
            currBufferPos = (int) currFilePos;
            currFilePos = -1;
        }
        else {
            /*
                Just read the bufferSize worth of data from the current file position.
                Update the currFilePos so that we read the previous block when the next read happens
                currBufferPos would be set to end of buffer byte array as it's full.
             */
            in.seek(currFilePos);
            in.read(buffer);
            currBufferPos = bufferSize - 1;
            currFilePos = currFilePos - bufferSize;
        }
    }

    private void fillCurrentLine() throws IOException {
        currentLine[0] = 0xA; // Add new line as the first character of the line.
        currLineWritePos = 1;
        // Start reading from the buffer until we find a new line character.
        // The line is stored in the reverse order (from end to start) as we are reading from the end.
        // Hence the first byte of the currentLine is set CR
        while (true) {
            if(currBufferPos < 0) {
                // We have read all the bytes from buffer, so fill up the buffer again.
                fillBuffer();
                if(currBufferPos < 0) {
                    // if we still don't have anything in the buffer, then we have read the entire file.
                    currLineReadPos = currLineWritePos - 1;
                    lineBuffered = true;
                    return;
                }
            }

            byte b = buffer[currBufferPos--];
            if(b == 0xA) {
                // newline (LF) found, line is fully constructed.
                lineBuffered = true;
                break;
            }
            else if(b == 0xD) {
                // ignore \r (CR) character.
                continue;
            }
            else {
                if(currLineWritePos == maxLineSizeBytes) {
                    throw new IOException("Line Size exceeds the configured max-line size bytes: " + maxLineSizeBytes + " Please configure a larger line size");
                }
                currentLine[currLineWritePos++] = b;
            }
        }
    }

}