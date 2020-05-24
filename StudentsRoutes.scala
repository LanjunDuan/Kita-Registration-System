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

//#students-routes-class
class StudentsRoutes(studentRegistryActor: ActorRef[StudentRegistry.Command])(enrollRoomActor: ActorRef[EnrollInRoom.Command])(implicit val system: ActorSystem[_]) {
  //#students-routes-class

  //#import-json-formats
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  //#timeout-setting
  implicit val timeout: Timeout = 5.seconds
  //#timeout-setting

  //#ask-functions
  def getStudents(): Future[Students] =
    studentRegistryActor.ask(GetStudents)
  def getRoomStudents(className: String): Future[StudentIds] =
    enrollRoomActor.ask(GetRoomStudents(className, _))
  //#ask-functions

  //#all-students-routes
  //#getAll-getClassroom
  val studentsRoutes: Route =
    pathPrefix("students" ) {
      concat(
        //#getAll
        pathEnd {
          complete(getStudents())
        },
        //#getAll

        //#getClassroom
        path(Segment) { className =>
          complete(getRoomStudents(className))
        }
        //#getClassroom
      )
    }
  //#getAll-getClassroom
  //#all-students-routes
}
