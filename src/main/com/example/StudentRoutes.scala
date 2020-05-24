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

import scala.concurrent.Future
import scala.concurrent.duration._
//#routes-top

//#student-routes-class
class StudentRoutes(studentRegistryActor: ActorRef[StudentRegistry.Command])(enrollRoomActor: ActorRef[EnrollInRoom.Command])(implicit val system: ActorSystem[_]) {
  //#student-routes-class

  //#import-json-formats
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  //#timeout-setting
  implicit val timeout: Timeout = 5.seconds
  //#timeout-setting

  //#ask-functions
  def getStudent(id: Long): Future[GetStudentResponse] =
    studentRegistryActor.ask(GetStudent(id, _))
  def createStudent(student: Student): Future[ActionPerformed] =
    studentRegistryActor.ask(CreateStudent(student, _))
  def deleteStudent(id: Long): Future[ActionPerformed] =
    studentRegistryActor.ask(DeleteStudent(id, _))
  def enrollStudent(id: Long): Future[ActionPerformed] =
    enrollRoomActor.ask(EnrollStudent(id, _))
  //#ask-functions

  //#all-student-routes
  //#enroll-post-get-delete
  val studentRoutes: Route =
    pathPrefix("student") {
      concat(
        //#enroll
        path("enroll" / LongNumber) { id =>
          onSuccess(enrollStudent(id)) { performed =>
            complete((StatusCodes.OK, performed))
          }
        },
        //#enroll

        //#post
        path("register") {
          post {
            entity(as[Student]) { student =>
              onSuccess(createStudent(student)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          }
        },
        //#post

        //#get-delete
        path(LongNumber) { id =>
          concat(
            get {
              onSuccess(getStudent(id)) { response =>
                complete(response.maybeStudent)
              }
            },
            delete {
              onSuccess(deleteStudent(id)) { performed =>
                complete((StatusCodes.OK, performed))
              }
            }
          )
        }
        //#get-delete
      )
    }
  //#enroll-post-get-delete
  //#all-student-routes
}
