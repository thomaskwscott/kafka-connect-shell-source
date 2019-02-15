package uk.co.threefi.connect.shell.source;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.threefi.connect.shell.util.CommandRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellSourceTask  extends SourceTask {

    private static final Logger log = LoggerFactory.getLogger(ShellSourceTask.class);

    ShellSourceConfig config;

    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting task");
        config = new ShellSourceConfig(props);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        String command = config.shellCommand;
        log.info("Running command: " + command);
        List<String> outputLines = new ArrayList<>();
        try {
            outputLines = CommandRunner.runCommand(command);
        } catch (IOException e) {
            log.warn("Command: " + command + " failed");
        }

        log.info("Retrieved: " + outputLines.size() + " records");

        // make lines source records
        List<SourceRecord> sourceRecords = new ArrayList<>();
        for(String line : outputLines)
        {
            SourceRecord record = new SourceRecord(
                    new HashMap<String,String>() {{
                        put("command", command);
                    }},
                    new HashMap<String,Long>() {{
                        put("systemTimeMillis", System.currentTimeMillis());
                    }},
                    config.topic,
                    null,
                    null,
                    null,
                    null,
                    line,
                    System.currentTimeMillis()
            );
            sourceRecords.add(record);
        }
        Thread.sleep(config.blockMs);
        return sourceRecords;
    }

    @Override
    public void stop() {

    }
}
