package com.example

//#json-formats-top
import spray.json.DefaultJsonProtocol
import com.example.StudentRegistry._
import spray.json.DefaultJsonProtocol._
//#json-formats-top

//#json-formats
object JsonFormats {

  import DefaultJsonProtocol._

  implicit val studentJsonFormat = jsonFormat4(Student)
  implicit val teacherJsonFormat = jsonFormat2(Teacher)
  implicit val studentsJsonFormat = jsonFormat1(Students)
  implicit val studentIdsJsonFormat = jsonFormat1(StudentIds)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

}
//#json-formats