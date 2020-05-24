package com.example

//#routes-top
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.example.EnrollInRoom._
import com.example.StudentRegistry._

import scala.concurrent.duration._
import scala.concurrent.Future
//#routes-top

//#teacher-routes-class
class TeacherRoutes (enrollRoomActor: ActorRef[EnrollInRoom.Command])(implicit val system: ActorSystem[_]) {
  //#teacher-routes-class

  //#import-json-formats
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  //#timeout-setting
  implicit val timeout: Timeout = 5.seconds
  //#timeout-setting

  //#ask-functions
  def createTeacher(teacher: Teacher): Future[ActionPerformed] =
    enrollRoomActor.ask(CreateTeacher(teacher, _))
  def enrollTeacher(id: Long): Future[ActionPerformed] =
    enrollRoomActor.ask(EnrollTeacher(id, _))
  //#ask-functions

  //#all-teacher-routes
  //#post-enroll
  val teacherRoutes: Route =
    pathPrefix("teacher") {
      concat(
        //#post
        path("register") {
          post{
            entity(as[Teacher]) { teacher =>
              onSuccess(createTeacher(teacher)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          }
        },
        //#post

        //#enroll
        path("enroll" / LongNumber) { id =>
          onSuccess(enrollTeacher(id)) { performed =>
            complete((StatusCodes.OK, performed))
          }
        }
        //#enroll
      )
    }
  //#post-enroll
  //#all-teacher-routes
}
