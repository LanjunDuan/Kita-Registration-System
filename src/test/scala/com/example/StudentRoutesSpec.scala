package com.example

//#student-routes-spec
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
class StudentRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.toClassic

  //use real StudentRegistryActor and EnrollInRoomActor to test
  val studentRegistryActor = testKit.spawn(StudentRegistry())
  val enrollInRoomActor = testKit.spawn(EnrollInRoom())
  lazy val studentRoutes = new StudentRoutes(studentRegistryActor)(enrollInRoomActor).studentRoutes

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#set-up

  "StudentRoutes" should {
    //#post-student-test
    "be able to add student (POST /student)" in {
      val student = Student("Sally", 1, "Sept082000", "male")
      val studentEntity = Marshal(student).to[MessageEntity].futureValue

      val request = Post("/student/register").withEntity(studentEntity)
      request ~> studentRoutes ~> check {
        status should be (StatusCodes.Created)
        contentType should be (ContentTypes.`application/json`)
        entityAs[String] should be ("""{"description":"Student Sally created."}""")
      }
    }
    //#post-student-test

    //#get-specific-student-test
    "return student information when ask a specific student id" in {
      val request = HttpRequest(uri = "/student/1")

      request ~> studentRoutes ~> check {
        status should be (StatusCodes.OK)
        contentType should be (ContentTypes.`application/json`)
        entityAs[String] should be ("""{"dateOfBirth":"Sept082000","gender":"male","id":1,"name":"Sally"}""")
      }
    }
    //#get-specific-student-test

    //#enroll-specific-student-test
    "be able to enroll specified student in a classroom" in {
      val request = HttpRequest(uri = "/student/enroll/1")

      request ~> studentRoutes ~> check {
        status should be (StatusCodes.OK)
        contentType should be (ContentTypes.`application/json`)
        entityAs[String] should be ("""{"description":"Student 1 enrolled in infant."}""")
      }
    }
    //#enroll-specific-student-test

    //#delete-specific-student-test
    "be able to remove student (DELETE /student)" in {
      val request = Delete(uri = "/student/1")
      request ~> studentRoutes ~> check {
        status should be (StatusCodes.OK)

        // we expect the response to be json:
        contentType should be (ContentTypes.`application/json`)

        // and no entries should be in the list:
        entityAs[String] should be ("""{"description":"User 1 deleted."}""")
      }
    }
    //#delete-specific-student-test
  }
}
//#student-routes-spec
