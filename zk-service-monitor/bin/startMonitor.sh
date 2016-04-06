#!/bin/bash
#-----------------------
#启动zk节点监控报警服务
#
#-----------------------
#cd `dirname $0`
source /etc/profile
#base_dir=$(dirname $0)/..  #当前目前的上一级目录
base_dir=$(dirname $0)/
cd $base_dir
pwd
COUNT=`jps | grep MonitorMain| wc -l`
if [ $COUNT -eq 1 ]; then
    echo "zk monitor process has already exist!!!"
    exit 1
fi

LOG4J_OPTS="-Dlog4j.configuration=file:$base_dir/config/log4j.properties"
nohup java  $LOG4J_OPTS -cp zk-service-monitor-1.0-jar-with-dependencies.jar com.le.bigdata.MonitorMain ./config/monitor.xml >monitor.log 2>&1 &
echo "======start successed.==="
