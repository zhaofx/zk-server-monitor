package com.le.bigdata.utils

import com.le.bigdata.config.MonitorItem
import scala.util.control.Breaks._
import scala.xml.XML

/**
 * Created by Administrator on 2016/2/22.
 */
class XmlParser {
  /**
   * read monitor items from monitor.xml
   * @return
   */
  def readMonitorItems(configFileName: String) : Seq[MonitorItem] = {
    val xmlFile = XML.load(configFileName)
    val monitorItems = xmlFile \\ "monitor-item"
    val monitorList = for {
      item <- monitorItems
    } yield {
      val title = (item \ "title").text.toString
      val serviceName = (item \ "service").text.toString
      val zkhost = (item \ "zkhost").text.toString
      val znode = (item \ "znode").text.toString
      val opt = (item \ "type").text.toString
      val contacts =(item \ "contacts").text.toString
      val msgTemplate =(item \ "msg").text.toString
      new MonitorItem(title,
        serviceName,
        zkhost,
        znode,
        opt,
        contacts,
        msgTemplate)
    }
    monitorList
  }
  def testBreak() = {
    breakable{
        for( i <- 1 to 10){
          println(i)
          if(i==8) {
            break
          }
        }
    }
  }
}

object XmlParser {
  def main(args: Array[String]): Unit = {
    val parser = new XmlParser()
    val configFileName = "D:\\git\\zk-service-monitor\\zk-service-monitor\\src\\resources\\monitor.xml"
    val items = parser.readMonitorItems(configFileName)
    items.foreach(println)
    items.foreach(x => println(x.zkHost + "  " + x.contacts))
//    parser.testBreak()
  }

}