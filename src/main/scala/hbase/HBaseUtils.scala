package com.aihuishou.hbase

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{ConnectionFactory, HTable}

/**
  * Created by admin on 2017/12/15.
  */
object HBaseUtils {

  def getData() ={
    val config = HBaseConfiguration.create()
   /* config.set("hbase.zookeeper.quorum", "hadoop.master,hadoop.master2,hadoop.data1");
    config.set("hbase.zookeeper.property.clientPort", "2181");
    config.set("hbase.client.retries.number", "3");*/

    /*config.addResource(new Path(System.getenv("HBASE_CONF_DIR"), "hbase-site.xml"));
    config.addResource(new Path(System.getenv("HADOOP_CONF_DIR"), "core-site.xml"));*/
    val connection = ConnectionFactory.createConnection(config)




  }

}
