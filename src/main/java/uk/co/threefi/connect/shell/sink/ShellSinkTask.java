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

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class ShellSinkTask extends SinkTask {
  private static final Logger log = LoggerFactory.getLogger(ShellSinkTask.class);

  ShellSinkConfig config;
  ShellWriter writer;
  int remainingRetries;

  @Override
  public void start(final Map<String, String> props) {
    log.info("Starting task");
    config = new ShellSinkConfig(props);
    initWriter();
    remainingRetries = config.maxRetries;
  }

  void initWriter() {
    writer = new ShellWriter(config);
  }

  @Override
  public void put(Collection<SinkRecord> records) {
    if (records.isEmpty()) {
      return;
    }
    final SinkRecord first = records.iterator().next();
    final int recordsCount = records.size();
    log.trace(
        "Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
        + "shell...",
        recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset()
    );
    try {
      writer.write(records);
    } catch (Exception e) {
      log.warn(
          "Write of {} records failed, remainingRetries={}",
          records.size(),
          remainingRetries,
          e
      );
      if (remainingRetries == 0) {
        throw new ConnectException(e);
      } else {
        writer.closeQuietly();
        initWriter();
        remainingRetries--;
        context.timeout(config.retryBackoffMs);
        throw new RetriableException(e);
      }
    }
    remainingRetries = config.maxRetries;
  }

  @Override
  public void flush(Map<TopicPartition, OffsetAndMetadata> map) {
    // Not necessary
  }

  public void stop() {
    log.info("Stopping task");
    try {
      writer.closeQuietly();
    } catch (Exception e) {
      log.error("Couldn't close writer: " + e);
    }

  }

  @Override
  public String version() {
    return getClass().getPackage().getImplementationVersion();
  }

}
