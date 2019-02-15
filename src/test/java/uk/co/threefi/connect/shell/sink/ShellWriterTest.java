/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.threefi.connect.shell.sink;

import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShellWriterTest {

  private ShellWriter writer = null;

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {

    if (writer != null)
      writer.closeQuietly();
  }

  @Test
  public void echoWithTopicKeyValue() throws Exception {
    Map<String,String> properties = new HashMap<>();
    properties.put(ShellSinkConfig.SHELL_COMMAND, "echo ${topic} ${key} ${value}");
    ShellSinkConfig config = new ShellSinkConfig(properties);

    ShellWriter writer = new ShellWriter(config);
    final Logger mockLogger = Mockito.mock(Logger.class);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    Mockito.doNothing().when(mockLogger).debug(captor.capture());
    writer.setLogger(mockLogger);


    List<SinkRecord> sinkRecords = new ArrayList<>();
    String payload = "someValue";
    sinkRecords.add(new SinkRecord("someTopic",0,null,"someKey",null, payload,0));

    writer.write(sinkRecords);

    List<String> captured=captor.getAllValues();

    Assert.assertEquals("someTopic someKey someValue",captured.get(0));

  }


}