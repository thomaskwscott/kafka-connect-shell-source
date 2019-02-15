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

package uk.co.threefi.connect.shell.source;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.*;

public class ShellSourceConfig extends AbstractConfig {

  public static final String SHELL_COMMAND = "shell.command";
  private static final String SHELL_COMMAND_DOC = "The shell command to run";
  private static final String SHELL_COMMAND_DISPLAY = "Shell command";
  private static final String SHELL_COMMAND_DEFAULT = "";

  public static final String TOPIC = "topic";
  private static final String TOPIC_DOC = "The to load the data into";
  private static final String TOPIC_DISPLAY = "Kafka topic";
  private static final String TOPIC_DEFAULT = "";

  public static final String BLOCK_MS = "block.ms";
  private static final String BLOCK_MS_DOC = "minimum time to block after running the shell command. " +
          "This does not guarantee that the command will be executed at the specified interval only " +
          "that a minimum time will pass before the next execution. This must not be greater than max.poll.interval.ms";
  private static final String BLOCK_MS_DISPLAY = "Block MS";
  private static final int BLOCK_MS_DEFAULT = 100;

  private static final String SHELL_GROUP = "Shell";
  private static final String KAFKA_GROUP = "Kafka";

  public static final ConfigDef CONFIG_DEF = new ConfigDef()
          // Shell Command
          .define(
                  SHELL_COMMAND,
                  ConfigDef.Type.STRING,
                  SHELL_COMMAND_DEFAULT,
                  ConfigDef.Importance.HIGH,
                  SHELL_COMMAND_DOC,
                  SHELL_GROUP,
                  1,
                  ConfigDef.Width.MEDIUM,
                  SHELL_COMMAND_DISPLAY
          )
          .define(
                  TOPIC,
                  ConfigDef.Type.STRING,
                  TOPIC_DEFAULT,
                  ConfigDef.Importance.HIGH,
                  TOPIC_DOC,
                  KAFKA_GROUP,
                   1,
                  ConfigDef.Width.MEDIUM,
                  TOPIC_DISPLAY
          )
          .define(
                  BLOCK_MS,
                  ConfigDef.Type.INT,
                  BLOCK_MS_DEFAULT,
                  ConfigDef.Importance.LOW,
                  BLOCK_MS_DOC,
                  SHELL_GROUP,
                  1,
                  ConfigDef.Width.MEDIUM,
                  BLOCK_MS_DISPLAY
          );

  public String shellCommand;
  public String topic;
  public int blockMs;

  public ShellSourceConfig(Map<?, ?> props) {
    super(CONFIG_DEF, props);
    shellCommand = getString(SHELL_COMMAND);
    blockMs = getInt(BLOCK_MS);
    topic = getString(TOPIC);

  }


  private static class EnumValidator implements ConfigDef.Validator {
    private final List<String> canonicalValues;
    private final Set<String> validValues;

    private EnumValidator(List<String> canonicalValues, Set<String> validValues) {
      this.canonicalValues = canonicalValues;
      this.validValues = validValues;
    }

    public static <E> EnumValidator in(E[] enumerators) {
      final List<String> canonicalValues = new ArrayList<>(enumerators.length);
      final Set<String> validValues = new HashSet<>(enumerators.length * 2);
      for (E e : enumerators) {
        canonicalValues.add(e.toString().toLowerCase());
        validValues.add(e.toString().toUpperCase());
        validValues.add(e.toString().toLowerCase());
      }
      return new EnumValidator(canonicalValues, validValues);
    }

    @Override
    public void ensureValid(String key, Object value) {
      if (!validValues.contains(value)) {
        throw new ConfigException(key, value, "Invalid enumerator");
      }
    }

    @Override
    public String toString() {
      return canonicalValues.toString();
    }
  }

  public static void main(String... args) {
    System.out.println(CONFIG_DEF.toEnrichedRst());
  }
}
