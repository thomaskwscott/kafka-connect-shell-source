package uk.co.threefi.connect.shell.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CommandRunner {


    public static List<String> runCommand(String command) throws IOException {

        Process process = Runtime.getRuntime()
                .exec(new String[] { "sh", "-c", command });

        InputStream stderr = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);

        String line = null;
        List<String> lines= new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("Shell command " + command, e);
        }
        if (exitCode != 0) {
            throw new IOException("Non zero exitcode: " + exitCode + " from shell command " + command + " failed");
        }
        return lines;
    }
}
