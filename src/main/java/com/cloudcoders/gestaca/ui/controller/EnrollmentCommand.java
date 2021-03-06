package com.cloudcoders.gestaca.ui.controller;

import com.cloudcoders.gestaca.logic.course.GetAllCourses;
import com.cloudcoders.gestaca.logic.enrollment.AddEnrollment;
import com.cloudcoders.gestaca.logic.exceptions.InvalidPersonException;
import com.cloudcoders.gestaca.logic.exceptions.InvalidTaughtCourse;
import com.cloudcoders.gestaca.logic.student.AddStudent;
import com.cloudcoders.gestaca.logic.student.GetStudent;
import com.cloudcoders.gestaca.model.Course;
import com.cloudcoders.gestaca.model.Enrollment;
import com.cloudcoders.gestaca.model.Student;
import com.cloudcoders.gestaca.model.TaughtCourse;
import com.cloudcoders.gestaca.ui.View;

import java.util.List;

public class EnrollmentCommand implements Command {

  public static final String ENROLLMENT = "inscribir alumno";

  private View view;
  private GetStudent getStudentUseCase;
  private GetAllCourses getAllCoursesUseCase;
  private AddEnrollment addEnrollment;
  private AddStudent addStudent;

  public EnrollmentCommand(View view,
                           GetStudent getStudentUseCase,
                           GetAllCourses getAllCoursesUseCase,
                           AddEnrollment addEnrollment,
                           AddStudent addStudent) {
    this.view = view;
    this.getStudentUseCase = getStudentUseCase;
    this.getAllCoursesUseCase = getAllCoursesUseCase;
    this.addEnrollment = addEnrollment;
    this.addStudent = addStudent;
  }

  @Override
  public boolean matches(String cmd) {
    return cmd.equals(ENROLLMENT);
  }

  public void execute() {
    List<Course> courses = getAllCoursesUseCase.getCourses();

    if (courses.isEmpty()) {
      view.showEmptyCourses();
      return;
    }

    view.showCourses(courses);
    Course course = view.askCourse();
    String dni = view.askDNI();
    Student student = getStudentUseCase.getStudent(dni);
    if (student == null) {
      view.showStudentNotFound();
      student = view.askStudent();
      addStudent.add(student);
    }

    TaughtCourse taughtCourse = new TaughtCourse(1, 1, null, 1, null, null, 0, null, null, course);
    Enrollment enrollment = new Enrollment(null, null, false, 0, taughtCourse, student);
    try {
      addEnrollment.add(enrollment);
      view.showStudent(student);
    } catch (InvalidPersonException e) {
      e.printStackTrace();
      view.showStudentNotFound();
    } catch (InvalidTaughtCourse invalidTaughtCourse) {
      view.showStudentFoundAndNotEnrolled();
    }
  }

}
