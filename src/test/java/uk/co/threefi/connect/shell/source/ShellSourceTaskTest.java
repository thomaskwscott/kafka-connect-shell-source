package uk.co.threefi.connect.shell.source;

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class ShellSourceTaskTest {

    @Test
    public void mapsToSourceRecord() throws Exception {

        ShellSourceTask sourceTask = new ShellSourceTask();
        sourceTask.start(new HashMap<String,String>() {{
            put("shell.command", "echo test line");
        }});
        List<SourceRecord> records = sourceTask.poll();

        Assert.assertEquals(records.size(),1);
        Assert.assertEquals(records.get(0).value().toString(),"test line");
    }

    @Test
    public void blocksForInterval() throws Exception {

        // this is not a good test, it just runs 2 "date" commands and verifies the time between
        // the results are at least block.ms apart
        ShellSourceTask sourceTask = new ShellSourceTask();
        sourceTask.start(new HashMap<String,String>() {{
            put("shell.command", "date +%s");
            put("block.ms","3000");
        }});
        List<SourceRecord> records = sourceTask.poll();
        List<SourceRecord> records2 = sourceTask.poll();

        double dateFirst = Double.parseDouble(records.get(0).value().toString());
        double dateSecond = Double.parseDouble(records2.get(0).value().toString());

        Assert.assertTrue(dateSecond >= dateFirst+3);
    }
}
