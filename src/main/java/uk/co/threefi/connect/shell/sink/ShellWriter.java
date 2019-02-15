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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.threefi.connect.shell.util.CommandRunner;


import java.io.*;
import java.util.Collection;
import java.util.List;


public class ShellWriter {

  private final ShellSinkConfig config;
  private static Logger log = LoggerFactory.getLogger(ShellWriter.class);

  // for testing
  protected void setLogger(Logger logger) {
    ShellWriter.log=logger;
  }

  ShellWriter(final ShellSinkConfig config) {
    this.config = config;

  }

  void write(final Collection<SinkRecord> records) throws IOException {

    for (SinkRecord record : records) {

      // build command - ${key}, ${topic} and ${value} can be replaced with message values
      String command = config.shellCommand
              .replace("${key}",record.key() == null ? "" : record.key().toString())
              .replace("${topic}",record.topic())
              .replace("${value}",record.value()== null ? "" : record.value().toString());

      // run command
      log.info("Running command: " + command);
      List<String> lines = CommandRunner.runCommand(command);
      for(String line : lines) {
        log.debug(line);
      }
      log.info("command completed");

    }

  }

  void closeQuietly() {

  }


}

