# Kita-Registration-System
This system is designed to be a Kita Registration System. We can do a series of operations from terminal.

For students

Register a student:
curl -H "Content-Type: application/json" -X POST -d '{"name":"Sally","id":1, "dateOfBirth":"Sept082000","gender":"male"}' http://localhost:8080/student/register

Extract information of a specific student:
curl http://localhost:8080/student/1

Extract information of all students:
curl http://localhost:8080/students

Extract all student IDs in a specific class:
curl http://localhost:8080/students/infant

Assign the student a classroom:
curl http://localhost:8080/student/enroll/1

Delete a specific student:
curl -X DELETE http://localhost:8080/student/1

For teachers

Register a teacher:
curl -H "Content-Type: application/json" -X POST -d '{"name":"John","id":11}' http://localhost:8080/teacher/register

Extract information of a specific teacher:
curl http://localhost:8080/teacher/enroll/11
