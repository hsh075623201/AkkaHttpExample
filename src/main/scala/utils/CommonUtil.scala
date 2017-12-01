package utils

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by admin on 2017/12/1.
  */
object CommonUtil {

  def getCurrentDateTime() ={
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
  }
}
