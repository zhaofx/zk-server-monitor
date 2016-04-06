package com.le.bigdata.config

/**
 * Created by Administrator on 2016/2/22.
 */
class MonitorItem(val title: String,
                  val service: String,
                  val zkHost: String,
                  val zkNode: String,
                  val opt: String,
                  val contacts: String,
                  val msgTemplate: String) {

  override def toString(): String = {
    title + " " + service + " " + zkHost + "  " +zkNode + "  " + opt + " " + contacts +"  " + msgTemplate
  }

}
