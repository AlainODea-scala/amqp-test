package com.alainodea

import com.rabbitmq.client.ConnectionFactory
import java.util.Properties
import collection.JavaConverters._
import com.github.nscala_time.time.Imports._
import java.io.FileReader

object AMQPTestMain {
  def main(args: Array[String]) {
    val properties = new Properties
    val configFile = if (args.length > 0) args(0) else "connection.properties"
    properties.load(new FileReader(configFile))
    val config = new Config(properties.asScala, configFile)
    val hostName = config("hostName")
    val port = config("port").toInt
    val commandQueue = config("commandQueue")
    val responseQueue = config("responseQueue")
    val username = config("username")
    val password = config("password")
    val virtualHost = config("virtualHost")
    val factory = new ConnectionFactory
    factory.useSslProtocol()
    factory.setHost(hostName)
    factory.setVirtualHost(virtualHost)
    factory.setPort(port)
    factory.setUsername(username)
    factory.setPassword(password)
    factory.setConnectionTimeout(5.seconds.millis.toInt)
    val connection = factory.newConnection()
    val channel = connection.createChannel()
    channel.queueDeclare(commandQueue, true, false, false, null)
    channel.queueDeclare(responseQueue, true, false, false, null)
    channel.close()
    connection.close()

    println("All good")
  }

  class Config(config: collection.mutable.Map[String,String], configResource: String) {
    def apply(key: String): String = config.getOrElse(key, sys.error("missing '" + key + "' from " + configResource))
  }
}
