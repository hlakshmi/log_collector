package com.log_collector.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BufferedReverseLineStreamReader {

    private static final String LOG_DIR_PATH = "/var/log";

    public List<String> readLines(String filePath, int numLines, String[] keyWords) throws IOException, InvalidPathException {
        List<String> result = new ArrayList<>();
        File file = new File(filePath);
        String canonicalPath = file.getCanonicalPath();
        // Validate that the path is always under /var/log for security reasons
        if(!isValidPath(canonicalPath)) {
            throw new InvalidPathException("File Path: " + filePath + " is outside the log directory: " + LOG_DIR_PATH);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedReverseLineStream(file)));
        int count = 0;

        while(true) {
            String line = reader.readLine();
            if(line == null || count == numLines) {
                break;
            }
            if(lineContainsKeyWords(line, keyWords)) {
                result.add(line);
                count++;
            }
        }
        return result;
    }

    private boolean lineContainsKeyWords(String line, String[] keyWords) {
        for(String word: keyWords) {
            if(!line.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPath(String canonicalPath) {
        Path path = Paths.get(canonicalPath);
        Path parent = path.getParent();
        String dirName = parent.toString();
        return dirName.startsWith(LOG_DIR_PATH);
    }
}
