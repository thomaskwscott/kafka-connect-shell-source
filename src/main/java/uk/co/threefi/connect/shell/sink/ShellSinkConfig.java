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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class ShellSinkConfig extends AbstractConfig {

  public static final String SHELL_COMMAND = "shell.command";
  private static final String SHELL_COMMAND_DOC = "The shell command to run";
  private static final String SHELL_COMMAND_DISPLAY = "Shell command";
  private static final String SHELL_COMMAND_DEFAULT = "";

  public static final String MAX_RETRIES = "max.retries";
  private static final int MAX_RETRIES_DEFAULT = 0;
  private static final String MAX_RETRIES_DOC =
          "The maximum number of times to retry on errors before failing the task.";
  private static final String MAX_RETRIES_DISPLAY = "Maximum Retries";

  public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
  private static final int RETRY_BACKOFF_MS_DEFAULT = 3000;
  private static final String RETRY_BACKOFF_MS_DOC =
          "The time in milliseconds to wait following an error before a retry attempt is made.";
  private static final String RETRY_BACKOFF_MS_DISPLAY = "Retry Backoff (millis)";

  private static final String RETRIES_GROUP = "Retries";
  private static final String SHELL_GROUP = "Shell";

  private static final ConfigDef.Range NON_NEGATIVE_INT_VALIDATOR = ConfigDef.Range.atLeast(0);


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
                  SHELL_COMMAND_DISPLAY)
          // Retries
          .define(
                  MAX_RETRIES,
                  ConfigDef.Type.INT,
                  MAX_RETRIES_DEFAULT,
                  NON_NEGATIVE_INT_VALIDATOR,
                  ConfigDef.Importance.MEDIUM,
                  MAX_RETRIES_DOC,
                  RETRIES_GROUP,
                  1,
                  ConfigDef.Width.SHORT,
                  MAX_RETRIES_DISPLAY
          )
          .define(
                  RETRY_BACKOFF_MS,
                  ConfigDef.Type.INT,
                  RETRY_BACKOFF_MS_DEFAULT,
                  NON_NEGATIVE_INT_VALIDATOR,
                  ConfigDef.Importance.MEDIUM,
                  RETRY_BACKOFF_MS_DOC,
                  RETRIES_GROUP,
                  2,
                  ConfigDef.Width.SHORT,
                  RETRY_BACKOFF_MS_DISPLAY
          );

  public final int maxRetries;
  public final int retryBackoffMs;
  public String shellCommand;

  public ShellSinkConfig(Map<?, ?> props) {
    super(CONFIG_DEF, props);
    maxRetries = getInt(MAX_RETRIES);
    retryBackoffMs = getInt(RETRY_BACKOFF_MS);
    shellCommand = getString(SHELL_COMMAND);

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
