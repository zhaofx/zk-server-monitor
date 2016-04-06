package com.le.bigdata.monitor

import java.text.SimpleDateFormat
import java.util.{Date, List}

import com.le.bigdata.config.MonitorItem
import com.le.bigdata.utils.SendMsgHelper
import org.I0Itec.zkclient.IZkChildListener

import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Administrator on 2016/2/23.
 */
class ZkChildChangeListener(item: MonitorItem, childNodes: List[String]) extends IZkChildListener {
  //  val previousChilds = scala.collection.mutable.Map.empty[String,String]

  def handleChildChange(parentPath: String, currentChilds: List[String]): Unit = {
    val dateStr = getNowDate
    println("----------------------"+dateStr+"--------------------------")
    println("the zk node child node has change:" + parentPath)
    if (null == currentChilds) {
      val msgBuffer :StringBuilder = new StringBuilder
      msgBuffer.append("#").append(item.title).append("#\r")
        .append("errorTime:").append(dateStr).append("  ")
        .append("serviceName:").append(item.service).append("  ")
        .append(parentPath).append(" all node is deleted!!!!!!")
      println(msgBuffer.toString())
      val args = Array(item.contacts, msgBuffer.toString())
      SendMsgHelper.main(args)
      return
    }
    childNodes.asScala.foreach(c => print(c + " "))
    println()
    currentChilds.asScala.foreach(c => print(c + " "))
    println()
    try {
      val msg = compareList(parentPath, currentChilds,item.msgTemplate)
      val msgBuffer :StringBuilder = new StringBuilder
      msgBuffer.append("#").append(item.title).append("#\r")
        .append("errorTime:").append(dateStr).append("  ")
        .append("serviceName:").append(item.service).append(" warn message:")
        .append(msg)

      println(item.contacts)
      println(msgBuffer.toString())
      val args = Array(item.contacts, msgBuffer.toString())
      SendMsgHelper.main(args)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def getNowDate(): String = {
    val now: Date = new Date()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateStr = dateFormat.format(now)
    dateStr
  }

  def compareList(parentPath: String, currentChilds: List[String], msgTemplate: String): String = {
    //查找删除的节点
    var deleteNodes = ""
    for (child <- childNodes.asScala) {
      if (!currentChilds.contains(child)) {
        deleteNodes = deleteNodes + parentPath + "/" + child + ","
      }
    }

    //查找新增的的节点
    var newNodes = ""
    for (child <- currentChilds.asScala) {
      if (!childNodes.contains(child)) {
        newNodes = newNodes + parentPath + "/" + child + ","
        childNodes.add(child)
      }
    }
    var msg = msgTemplate
    println("deleteNodes:"+deleteNodes)

    if (deleteNodes != "") {
      msg = msgTemplate.replace("#DEL_NODES#",deleteNodes.substring(0, deleteNodes.length - 1))
    }else{
      msg = msgTemplate.replace("#DEL_NODES#","0个")
    }
    if (newNodes != "") {
      msg = msg.replace("#NEW_NODES#", newNodes.substring(0, newNodes.length - 1))
    }else{
      msg = msg.replace("#NEW_NODES#", "0个")
    }
    msg
  }

  def test = {
    val command = ArrayBuffer("ls", "pwd", "cd")
    val pb = new ProcessBuilder(command)
    handleChildChange("", pb.command())
  }
}
