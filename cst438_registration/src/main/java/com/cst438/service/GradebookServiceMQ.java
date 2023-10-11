package com.cst438.service;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "mq")
public class GradebookServiceMQ implements GradebookService {
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	Queue gradebookQueue = new Queue("gradebook-queue", true);

	// send message to grade book service about new student enrollment in course
	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message " + student_email + " " + course_id);
		
		// Create an EnrollmentDTO object
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, student_email, student_name, course_id);

		// Convert the EnrollmentDTO to JSON string
		String enrollmentJSON = asJsonString(enrollmentDTO);

		// Send the JSON message to the gradebookQueue
		rabbitTemplate.convertAndSend(gradebookQueue.getName(), enrollmentJSON);
	}
	
	@RabbitListener(queues = "registration-queue")
	@Transactional
	public void receive(String message) {
		System.out.println("Receive grades: " + message);
		
		// Deserialize the string message to FinalGradeDTO[]
		FinalGradeDTO[] finalGradeDTOs = fromJsonString(message, FinalGradeDTO[].class);

		// Update the course grades in the database for each student
		for (FinalGradeDTO gradeDTO : finalGradeDTOs) {
			// Retrieve the email and grade from the DTO
			String studentEmail = gradeDTO.studentEmail();
			String courseGrade = gradeDTO.grade();

			// Find the corresponding Enrollment record in the database by email and course ID
			Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(studentEmail, gradeDTO.courseId());

			if (enrollment != null) {
				// Update the course grade in the Enrollment record
				enrollment.setCourseGrade(courseGrade);
				// Save the updated record to the database
				enrollmentRepository.save(enrollment);
			} else {
				System.out.println("No enrollment found for student with email " + studentEmail + " and course ID " + gradeDTO.courseId());
			}
		}
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T fromJsonString(String str, Class<T> valueType) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
