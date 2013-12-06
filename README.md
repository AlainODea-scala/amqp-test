AMQP Test
=========
A tool for testing AMQP connections.

Usage:
------
* Run sbt assembly
* Run java -jar amqp-test-assembly-1.0.jar
* Exit code 0 means success
* Exit code 1 means failure (details will be printed to STDERR)

Connection.properties
---------------------
* hostName: String - host name of RabbitMQ server
* tls: Boolean - whether or not to use TLS encrypted communications (default true)
* port: Int - port number to connect to (default when tls 5671, otherwise 5672)
* commandQueue: String - name of the command queue to declare
* responseQueue: String - name of the response queue to declare
* username: String - user name to authenticate with
* password: String - password for that user
* virtualHost: String - vHost to connect to (default /)

**Example basic connection.properties (TLS defaults to true)**:

    hostName=localhost
    tls=false
    queue=queueName
    username=username
    password=somesensiblepassword
    virtualHost=vhostname

**Example: more involved connection.properties**:

    hostName=localhost
    port=443
    commandQueue=command
    responseQueue=response
    username=username
    password=somesensiblepassword
    virtualHost=vhostname
