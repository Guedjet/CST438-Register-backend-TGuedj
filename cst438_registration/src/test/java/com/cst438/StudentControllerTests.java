package com.cst438;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.cst438.domain.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testListAllStudents() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAddStudent() throws Exception {
        Student student = new Student();
        student.setName("John Doe");
        student.setEmail("johndoe@example.com");
        student.setStatusCode(0);
        student.setStatus("Active");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

//    @Test
//    public void testUpdateStudentStatus() throws Exception {
//
//        String updatedStatus = "Inactive";
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .put("/students/{id}", 1)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"status\":\"" + updatedStatus + "\"}")
//        		.accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.status").value(updatedStatus));
//    }

    @Test
    public void testUpdateStudentStatus() throws Exception {
        MockHttpServletResponse response;

        int studentId = 2;
        String newStatus = "active";

        response = mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/students/" + studentId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"" + newStatus + "\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        Student result = fromJsonString(response.getContentAsString(), Student.class);
        assertEquals(newStatus, result.getStatus());
    }
    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


    @Test
    public void testDeleteStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/students/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
