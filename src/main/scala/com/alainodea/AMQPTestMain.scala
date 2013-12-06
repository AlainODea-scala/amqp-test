package com.alainodea

import com.rabbitmq.client.{AlreadyClosedException, ConnectionFactory}
import java.util.Properties
import collection.JavaConverters._
import com.github.nscala_time.time.Imports._
import java.io.{EOFException, FileReader}

object AMQPTestMain {
  def main(args: Array[String]) {
    val properties = new Properties
    val configFile = if (args.length > 0) args(0) else "connection.properties"
    properties.load(new FileReader(configFile))
    val config = new Config(properties.asScala, configFile)

    val hostName =      config("hostName"                                  )
    val tls =           config("tls"          , "true"                     ).toBoolean
    val port =          config("port"         , if (tls) "5671" else "5672").toInt
    val commandQueue  = config("commandQueue"                              )
    val responseQueue = config("responseQueue"                             )
    val username =      config("username"                                  )
    val password =      config("password"                                  )
    val virtualHost =   config("virtualHost"  , "/"                        )

    val factory = new ConnectionFactory
    if (tls) factory.useSslProtocol()
    factory.setHost(hostName)
    factory.setVirtualHost(virtualHost)
    factory.setPort(port)
    factory.setUsername(username)
    factory.setPassword(password)
    factory.setConnectionTimeout(5.seconds.millis.toInt)

    intercept {
      bracket(factory.newConnection())(_.close()) { connection =>
        bracket(connection.createChannel())(_.close()) { channel =>
          channel.queueDeclare(commandQueue, true, false, false, null)
          channel.queueDeclare(responseQueue, true, false, false, null)
          channel.close()
        }
      }
    } match {
      case None =>
        sys.exit(0)
      case Some(cause) =>
        cause match {
          case eof: EOFException =>
            Console.err.println(eof + " - this is a protocol error. Sometimes caused by non-existent virtualHost.")
          case e =>
            Console.err.println(e)
        }
        sys.exit(1)
    }
  }

  class Config(config: collection.mutable.Map[String,String], configResource: String) {
    def apply(key: String): String = apply(key, sys.error("missing '" + key + "' from " + configResource))
    def apply(key: String, default: => String): String = config.getOrElse(key, default)
  }

  def bracket[T](alloc: => T)(release: T => Unit)(block: T => Unit) {
    val it = alloc
    try {
      block(it)
    } finally {
      try {
        release(it)
      } catch {
        case e: AlreadyClosedException =>
      }
    }
  }

  def intercept(block: => Unit): Option[Throwable] = {
    try {
      block
      None
    } catch {
      case e: Throwable =>
        var cause = e.getCause
        if (cause == null) cause = e
        else {
          while (cause.getCause != null) cause = cause.getCause
        }
        Some(cause)
    }
  }
}
