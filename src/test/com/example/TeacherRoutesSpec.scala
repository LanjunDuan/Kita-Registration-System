package com.example

//#teacher-routes-spec
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
class TeacherRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  lazy val testKit = ActorTestKit()
  implicit def typedSystem = testKit.system
  override def createActorSystem(): akka.actor.ActorSystem =
    testKit.system.toClassic

  //use a real EnrollInRoomActor to test
  val enrollInRoomActor = testKit.spawn(EnrollInRoom())
  lazy val teacherRoutes = new TeacherRoutes(enrollInRoomActor).teacherRoutes

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#set-up

  "TeacherRoutes" should {
    //#post-teacher-test
    "be able to register a teacher" in {
      val teacher = Teacher("John", 11)
      val teacherEntity = Marshal(teacher).to[MessageEntity].futureValue

      val request = Post("/teacher/register").withEntity(teacherEntity)

      request ~> teacherRoutes ~> check {
        status should be (StatusCodes.Created)

        contentType should be (ContentTypes.`application/json`)

        entityAs[String] should be ("""{"description":"Teacher John created."}""")
      }
    }
    //#post-teacher-test

    //#enroll-specific-teacher-test
    "be able to enroll a specific teacher in a classroom" in {
      val request = HttpRequest(uri = "/teacher/enroll/11")

      request ~> teacherRoutes ~> check {
        status should be (StatusCodes.OK)

        contentType should be (ContentTypes.`application/json`)

        entityAs[String] should be ("""{"description":"Teacher John enrolled in infant."}""")
      }
    }
    //#enroll-specific-teacher-test
  }
}
//#teacher-routes-spec
