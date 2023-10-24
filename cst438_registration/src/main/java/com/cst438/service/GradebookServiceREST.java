package com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "rest")
@RestController
public class GradebookServiceREST implements GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	private static String gradebook_url;

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
	    System.out.println("Start Message " + student_email + " " + course_id);

	    EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, student_email, student_name, course_id);

	    String url = gradebook_url;
	    EnrollmentDTO response = restTemplate.postForObject(url, enrollmentDTO, EnrollmentDTO.class);

	    System.out.println("Response from Gradebook: " + response);
	}
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	/*
	 * endpoint for final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades(@RequestBody FinalGradeDTO[] grades, @PathVariable("course_id") int course_id) {
	    System.out.println("Grades received " + grades.length);

	    // Iterate over the FinalGradeDTO array and update the database
	    for (FinalGradeDTO gradeDTO : grades) {
	        // Retrieve the email and grade from the DTO
	        String studentEmail = gradeDTO.studentEmail();
	        String courseGrade = gradeDTO.grade();

	        // Find the corresponding Enrollment record in the database by email and course ID
	        Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(studentEmail, course_id);

	        if (enrollment != null) {
	            // Update the course grade in the Enrollment record
	            enrollment.setCourseGrade(courseGrade);
	            // Save the updated record to the database
	            enrollmentRepository.save(enrollment);
	        } else {
	            System.out.println("No enrollment found for student with email " + studentEmail + " and course ID " + course_id);
	        }
	    }
	}

}
