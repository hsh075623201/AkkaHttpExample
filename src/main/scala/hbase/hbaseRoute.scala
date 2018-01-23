package com.aihuishou.hbase
import akka.http.scaladsl.server.Directives._
import com.aihuishou.common.ExtendedJsonSupport

/**
  * Created by admin on 2017/12/15.
  */
trait hbaseRoute extends ExtendedJsonSupport {

  val HBaseRouteReport = pathPrefix("hbase")(
      complete {

        ""
      }
    )
}
