package com.le.bigdata.monitor

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

import com.le.bigdata.utils.{Logging, XmlParser}

/**
 * Created by Administrator on 2016/2/23.
 */
class AppMonitorServer(val configName: String) extends Logging {
  private var isShuttingDown = new AtomicBoolean(false)
  private var shutdownLatch = new CountDownLatch(1)
  private var startupComplete = new AtomicBoolean(false)

  /*所有的监控服务对象*/
  var monitorServicesSeq: Seq[AppMonitorService] = null
  def startup() {
    try {
      info("starting")
      isShuttingDown = new AtomicBoolean(false)
      shutdownLatch = new CountDownLatch(1)
      /* 读取所有的监控项*/
      val xmlParser = new XmlParser()
      val items = xmlParser.readMonitorItems(configName)
      monitorServicesSeq =
        for {
          item <- items}
        yield {
          val service  = new AppMonitorService(item)
          service.startMonitor()
          service
        }
      startupComplete.set(true)
      info("started")
    }
    catch {
      case e: Throwable =>
        fatal("Fatal error during App Monitor Server startup. Prepare to shutdown", e)
        shutdown()
        throw e
    }
  }

  def shutdown() {
    try {
      info("shutting down")
      val canShutdown = isShuttingDown.compareAndSet(false, true)
      if (canShutdown) {
        //do shutdown operate
        monitorServicesSeq.foreach(service =>{
          service.stopMonitor
        })
        shutdownLatch.countDown()
        startupComplete.set(false)
        info("shut down completed")
      }
    }
    catch {
      case e: Throwable =>
        fatal("Fatal error during KafkaServer shutdown.", e)
        throw e
    }
  }

  def awaitShutdown(): Unit = shutdownLatch.await()

}
