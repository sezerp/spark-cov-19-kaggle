import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat


( new SimpleDateFormat("mm/dd/yyyy")).format(new Date(0L)).toString
( new SimpleDateFormat("mm/dd/yyyy HH:mm")).format(new Timestamp(0L)).toString

new SimpleDateFormat("mm/dd/yyyy HH:mm").toPattern

val xs: collection.mutable.ListBuffer[String] = collection.mutable.ListBuffer()
(xs += "dt.name").toList

xs

val ys = collection.mutable.Map[String, String]()

ys += ("a" -> "b")

ys

val zs = List(Some("x"), None)

zs.flatMap(e => e).foreach(println)