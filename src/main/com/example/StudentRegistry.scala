package com.example

//#student-registry-actor
//#actor-top
import akka.actor.typed.{ActorRef,Behavior}
import akka.actor.typed.scaladsl.Behaviors
//#actor-top

//#student-students-class
final case class Student(name: String, id: Long, dateOfBirth: String, gender: String)
final case class Students(students: List[Student])
//#student-students-class

object StudentRegistry {
  //actor protocol
  sealed trait Command
  final case class CreateStudent(student: Student, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetStudent(id: Long, replyTo: ActorRef[GetStudentResponse]) extends Command
  final case class GetStudents(replyTo: ActorRef[Students]) extends Command
  final case class DeleteStudent(id: Long, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetStudentResponse(maybeStudent: Option[Student])
  final case class ActionPerformed(description: String)
  //actor protocol

  def apply(): Behavior[Command] = registry(Set.empty)

  //message-processing
  private def registry(students: Set[Student]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateStudent(student, replyTo) =>
        replyTo ! ActionPerformed(s"Student ${student.name} created.")
        registry(students + student)
      case GetStudent(id, replyTo) =>
        replyTo ! GetStudentResponse(students.find(_.id == id))
        Behaviors.same
      case GetStudents(replyTo) =>
        replyTo ! Students(students.toList)
        Behaviors.same
      case DeleteStudent(id, replyTo) =>
        replyTo ! ActionPerformed(s"User $id deleted.")
        registry(students.filterNot(_.id == id))
    }
  //message-processing
}
//#student-registry-actor
