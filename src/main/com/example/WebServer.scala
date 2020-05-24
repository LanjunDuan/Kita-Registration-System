package com.example

//#main-top
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.io.StdIn
//#main-top

//#main-class
object WebServer {

  //#start-http-server
  private def startHttpServer(route: Route, system: ActorSystem[_]) = {

    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    import system.executionContext
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
  //#start-http-server

  def main(args: Array[String]): Unit = {
    //#server-boot
    val rootBehavior = Behaviors.setup[Nothing] { context =>

      //#student-registry-actor
      val studentRegistryActor = context.spawn(StudentRegistry(), "StudentRegistryActor")
      context.watch(studentRegistryActor)
      //#student-registry-actor
      //#enroll-room-actor
      val enrollRoomActor = context.spawn(EnrollInRoom(), "EnrollRoomActor")
      context.watch(enrollRoomActor)
      //#enroll-room-actor

      //#all-routes
      val studentRoutes = new StudentRoutes(studentRegistryActor)(enrollRoomActor)(context.system).studentRoutes
      val teacherRoutes = new TeacherRoutes(enrollRoomActor)(context.system).teacherRoutes
      val studentsRoutes = new StudentsRoutes(studentRegistryActor)(enrollRoomActor)(context.system).studentsRoutes

      val route = concat(studentRoutes,teacherRoutes,studentsRoutes)
      //#all-routes
      //#start-http-server
      startHttpServer(route, context.system)
      //#start-http-server

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "KitaRegistrationSystem")
    //#server-boot
  }
}
//#main-class
