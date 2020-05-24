package com.example

//#students-routes-spec
//#test-top
import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ Matchers, WordSpec }
import akka.actor.typed.scaladsl.adapter._
//#test-top

//#set-up
class StudentsRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.toClassic

  //use real StudentRegistryActor and EnrollInRoomActor to test
  val studentRegistryActor = testKit.spawn(StudentRegistry())
  val enrollInRoomActor = testKit.spawn(EnrollInRoom())
  lazy val studentsRoutes = new StudentsRoutes(studentRegistryActor)(enrollInRoomActor).studentsRoutes
  lazy val studentRoutes = new StudentRoutes(studentRegistryActor)(enrollInRoomActor).studentRoutes

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#set-up

  "StudentsRoutes" should {
    //#get-students-test(NULL)
    "return no students if no present (GET /students)" in {
      val request = HttpRequest(uri = "/students")

      request ~> studentsRoutes ~> check {
        status should be (StatusCodes.OK)

        contentType should be (ContentTypes.`application/json`)

        entityAs[String] should be ("""{"students":[]}""")
      }
    }
    //#get-students-test(NULL)

    //#get-students-test(List)
    "return current students when ask (GET /students)" in {
      val student = Student("Sally", 1, "Sept082000", "male")
      val studentEntity = Marshal(student).to[MessageEntity].futureValue

      val request1 = Post("/student/register").withEntity(studentEntity)
      val request2 = HttpRequest(uri = "/students")

      request1 ~> studentRoutes
      request2 ~> studentsRoutes ~> check {
        status should be (StatusCodes.OK)

        contentType should be (ContentTypes.`application/json`)

        entityAs[String] should be ("""{"students":[{"dateOfBirth":"Sept082000","gender":"male","id":1,"name":"Sally"}]}""")
      }
    }
    //#get-students-test(List)

    //#get-classroom-students-test
    "return studentIds in a specific classroom" in {
      val request1 = HttpRequest(uri = "/student/enroll/1")
      val request2 = HttpRequest(uri = "/students/infant")

      request1 ~> studentRoutes
      request2 ~> studentsRoutes ~> check {
        status should be (StatusCodes.OK)

        contentType should be (ContentTypes.`application/json`)

        entityAs[String] should be ("""{"studentIds":[1]}""")
      }
    }
    //#get-classroom-students-test
  }
}
//#students-routes-spec
