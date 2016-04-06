#!/bin/bash
#-----------------------
#启动zk节点监控报警服务
#
#-----------------------
cd `dirname $0`
source /etc/profile
ps ax | grep -i '.MonitorMain' | grep java | grep -v grep | awk '{print $1}' | xargs kill -9
#ps ax | grep -i 'MonitorMain\.MonitorMain' | grep java | grep -v grep | awk '{print $1}' | xargs kill -SIGTERM
#ps ax | grep -i 'kafka\.Kafka' | grep java | grep -v grep | awk '{print $1}' | xargs kill -SIGTERM
