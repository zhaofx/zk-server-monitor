package com.le.bigdata.monitor

import java.util.List

import com.le.bigdata.config.MonitorItem
import com.le.bigdata.utils.{Logging, ZKStringSerializer}
import org.I0Itec.zkclient.{ZkClient}

/**
 * 用于监控
 * Created by Administrator on 2016/2/22.
 */
class AppMonitorService(val item: MonitorItem) extends Logging{

  var zkClient: ZkClient = null

  def initZk(zkConnect: String): ZkClient = {
    println("Connecting to zookeeper on " + zkConnect)
    val zkSessionTimeoutMs = 60000
    val zkConnectionTimeoutMs = 6000;
    val chroot = {
      if (zkConnect.indexOf("/") > 0)
        zkConnect.substring(zkConnect.indexOf("/"))
      else
        ""
    }
    if (chroot.length > 1) {
      val zkConnForChrootCreation = zkConnect.substring(0, zkConnect.indexOf("/"))
      val zkClientForChrootCreation = {
        new ZkClient(zkConnForChrootCreation, zkSessionTimeoutMs, zkConnectionTimeoutMs, ZKStringSerializer)
      }
      println("Created zookeeper path " + chroot)
      zkClientForChrootCreation.close()
    }
    val zkClient = new ZkClient(zkConnect, zkSessionTimeoutMs, zkConnectionTimeoutMs, ZKStringSerializer)
    zkClient
  }

  def startMonitor() = {
    try{
      val zkClient: ZkClient = initZk(item.zkHost)
      val childNodes: List[String] =  zkClient.getChildren(item.zkNode)
      zkClient.subscribeChildChanges(item.zkNode, new ZkChildChangeListener(item,childNodes))
    }catch {
      case e:Exception => {
        fatal("Fatal error during App Monitor Server startup. Prepare to shutdown", e)
      }
    }
  }

  def stopMonitor = {
    zkClient.unsubscribeAll()
  }
}

