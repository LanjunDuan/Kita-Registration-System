package com.example

//#enroll-in-room-actor
//#actor-top
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
//#actor-top

//import-student-registry-action-performed
import com.example.StudentRegistry.ActionPerformed
//import-student-registry-action-performed

//#teacher-room-studentIds-class
final case class Teacher(name: String, id: Long)
final case class Room(name: String, studentIds: scala.collection.mutable.Set[Long], teacherIds: scala.collection.mutable.Set[Long])
final case class StudentIds(studentIds: List[Long])
//#teacher-room-studentIds-class

object EnrollInRoom{
  //actor protocol
  sealed trait Command
  final case class CreateTeacher(teacher: Teacher, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class EnrollTeacher(teacherId: Long, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class EnrollStudent(studentId: Long, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetRoomStudents(roomName: String, replyTo: ActorRef[StudentIds]) extends Command
  //actor protocol

  //#rooms
  val infantRoom =  Room("infant", scala.collection.mutable.Set.empty, scala.collection.mutable.Set.empty)
  val toddlerRoom =  Room("toddler", scala.collection.mutable.Set.empty, scala.collection.mutable.Set.empty)
  val preschoolRoom =  Room("preschool", scala.collection.mutable.Set.empty, scala.collection.mutable.Set.empty)
  val elementaryRoom = Room("elementary", scala.collection.mutable.Set.empty, scala.collection.mutable.Set.empty)
  //#rooms

  def apply(): Behavior[Command] = enroll(Set.empty)

  //message-processing
  def enroll(teachers: Set[Teacher]): Behavior[Command] = Behaviors.receiveMessage {
    case CreateTeacher(teacher, replyTo) =>
      replyTo ! ActionPerformed(s"Teacher ${teacher.name} created.")
      enroll(teachers + teacher)
    case EnrollTeacher(id, replyTo) =>
      infantRoom.teacherIds.add(id)
      teachers.find(_.id == id) match {
        case Some(teacher) => replyTo ! ActionPerformed(s"Teacher ${teacher.name} enrolled in ${infantRoom.name}.")
        case None =>
      }
      Behaviors.same
    case EnrollStudent(id, replyTo) =>
      infantRoom.studentIds.add(id)
      replyTo ! ActionPerformed(s"Student ${id} enrolled in ${infantRoom.name}.")
      Behaviors.same
    case GetRoomStudents(roomName, replyTo) =>
      roomName match {
        case "infant" => replyTo ! StudentIds(infantRoom.studentIds.toList)
        case "toddler" => replyTo ! StudentIds(toddlerRoom.studentIds.toList)
        case "preschool" => replyTo ! StudentIds(preschoolRoom.studentIds.toList)
        case "elementary" => replyTo ! StudentIds(elementaryRoom.studentIds.toList)
      }
      Behaviors.same
  }
  //message-processing
}
//#enroll-in-room-actor
