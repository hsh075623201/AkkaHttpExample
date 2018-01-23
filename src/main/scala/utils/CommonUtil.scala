package com.aihuishou.utils

import java.text.SimpleDateFormat
import java.util.Date
import spray.json._
import akka.http.scaladsl.model.StatusCodes

/**
  * Created by admin on 2017/12/1.
  */
object CommonUtil {

  def getCurrentDateTime() ={
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
  }

  def warpResponse(value:Any, code:Int=StatusCodes.OK.intValue, msg:String=StatusCodes.OK.defaultMessage) ={
    Map("code"->code,
      "message"->msg,
      "data"->value).toJson.toString()
  }
}
