package com.le.bigdata

import com.le.bigdata.monitor.AppMonitorServerStartable
import org.I0Itec.zkclient.ZkClient

/**
 * Created by Administrator on 2016/2/22.
 */


object MonitorMain {
  def main(args: Array[String]):Unit = {
    if (args.length != 1) {
      println("USAGE: java [options] %s monitor.xml".format(classOf[AppMonitorServerStartable].getSimpleName()))
      System.exit(1)
    }
    val configFileName = args(0)
    val serverStartable: AppMonitorServerStartable = new AppMonitorServerStartable(configFileName)

    // attach shutdown handler to catch control-c
    Runtime.getRuntime().addShutdownHook(new Thread() {
      override def run() = {
        println("/////////////////shutdown/////////////")
        serverStartable.shutdown
        println("/////////////////shutdown completed/////////////")
      }
    })
    try{
      serverStartable.startup
      serverStartable.awaitShutdown
    }catch{
      case e: Exception => e.printStackTrace()
    }

  }

}
