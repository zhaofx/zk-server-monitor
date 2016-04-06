package com.le.bigdata.monitor

import com.le.bigdata.utils.Logging
import org.apache.log4j.Logger

/**
 * Created by Administrator on 2016/2/23.
 */
class AppMonitorServerStartable(val configFileName: String) extends Logging{
  private val server = new AppMonitorServer(configFileName)
  def startup() {
    try {
      server.startup()
    }
    catch {
      case e: Throwable =>
        fatal("Fatal error during KafkaServerStartable startup. Prepare to shutdown", e)
        System.exit(1)
    }
  }

  def shutdown() {
    try {
      server.shutdown()
    }
    catch {
      case e: Throwable =>
        fatal("Fatal error during KafkaServerStable shutdown. Prepare to halt", e)
        System.exit(1)
    }
  }

  def awaitShutdown() =
    server.awaitShutdown
}
