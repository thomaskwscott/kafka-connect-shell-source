.. _connector:

Shell Source Connector
===================

The Shell source connector runs shell commands periodically and produces the results of these to Kafka.
The output of the command is read line by line with each line being ingested into Kafka as a separate message

Quick Start - Reading a file.
===================

.. include:: includes/prerequisites.rst

In this demo we'll use the Shell Source connector to read lines from a file into Kafka.

---------------------------------
Create the Kafka topic
---------------------------------

Before we can read the file we need to create a topic to hold the file data.
From inside a cp-demo broker container (``docker-compose exec kafka1 bash``):

#. Create topics:

    .. sourcecode:: bash

        kafka-topics --zookeeper zookeeper:2181 --topic file.data --create --replication-factor 1 --partitions 1

---------------------------------
Create a test file and a script to read it
---------------------------------


From inside a cp-demo Connect container (``docker-compose exec connect bash``):

#. Create a test file:

    .. sourcecode:: bash

        cat /proc/cpuinfo > /etc/kafka/secrets/cpuinfo

#. Create a test script:

    .. sourcecode:: bash

        echo "cd /etc/kafka/secrets" > /etc/kafka/secrets/readFile.sh
        echo "if [ -f cpuinfo ]; then" >> /etc/kafka/secrets/readFile.sh
        echo "cat cpuinfo" >> /etc/kafka/secrets/readFile.sh
        echo "mv cpuinfo cpuinfo.processed" >> /etc/kafka/secrets/readFile.sh
        echo "fi" >> /etc/kafka/secrets/readFile.sh
        chmod +x /etc/kafka/secrets/readfile.sh

----------------------------
Load the Shell Source Connector
----------------------------

Now we submit the Shell Source connector to the cp-demo connect instance this will load our test file into Kafka:

#.  From outside the container in the cp-demo root directory:

    .. sourcecode:: bash

        curl -X POST -H "Content-Type: application/json" \
        --cert scripts/security/connect.certificate.pem \
        --key scripts/security/connect.key \
        --tlsv1.2 \
        --cacert scripts/security/snakeoil-ca-1.crt \
        --data '{ \
        "name": "shell-source", \
        "config": {"connector.class":"uk.co.threefi.connect.shell.ShellSourceConnector", \
                "tasks.max":"1", \
                "shell.command":"sh /etc/kafka/secrets/readFile.sh", \
                "block.ms": "3000", \
                "topic":"file.data" \
                }}' \
        https://localhost:8083/connectors

-------------------
Confirm the results
-------------------

Confirm messages on the topic from inside a cp-demo Broker container (``docker-compose exec kafka1 bash``):

    .. sourcecode:: bash

        kafka-console-consumer --bootstrap-server localhost:10091 --topic file.data --from-beginning


Features
===================

Key/Topic substitutions
^^^^^^^^^^^^^^^^^^^^^^^

The special strings ``${key}``, ``${topic}`` and ``${value}`` can be used in the shell.commandproperty to
inject message data into the command run.


