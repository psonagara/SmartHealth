package com.ps.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IResponseConstants;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Relation;
import com.ps.entity.Specialization;
import com.ps.service.IDataService;
import com.ps.util.JwtUtil;
import com.ps.util.RestTestUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * Unit tests for {@link DataRestController}.
 * <p>
 * This class verifies the behavior of open resource data related REST API endpoints
 * using MockMvc in a Spring Boot test context.
 * <p>
 * Scope: Controller layer only (Service layer is mocked).
 */
@WebMvcTest(DataRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class DataRestControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IDataService dataService;
	
	@MockBean
	private JwtUtil jwtUtil;

	/**
	 * Test case for GET /data/doctor/degree.
	 * <p>
	 * Scenario: Valid request to fetch all degrees.  
	 * Expectation: Returns HTTP 200 (OK) with the list of degree.
	 */
	@Test
	void testGetDoctorDegrees() throws Exception {
		List<Degree> degrees = TestDataUtil.getDegrees();
		
		when(dataService.getDoctorDegrees()).thenReturn(degrees);

		MvcResult result = mockMvc.perform(get("/data/doctor/degree")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		List<Degree> actualResponse = RestTestUtil.toObjectFromJson(result.getResponse().getContentAsString(), new TypeReference<List<Degree>>() {});
		assertEquals(degrees, actualResponse);
	}
	
	/**
	 * Test case for GET /data/doctor/department.
	 * <p>
	 * Scenario: Valid request to fetch all department.  
	 * Expectation: Returns HTTP 200 (OK) with the list of department.
	 */
	@Test
	void testGetDoctorDepartment() throws Exception {
		List<Department> departments = TestDataUtil.getDepartment();
		
		when(dataService.getDoctorDepartment()).thenReturn(departments);
		
		MvcResult result = mockMvc.perform(get("/data/doctor/department")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		List<Department> actualResponse = RestTestUtil.toObjectFromJson(result.getResponse().getContentAsString(), new TypeReference<List<Department>>() {});
		assertEquals(departments, actualResponse);
	}
	
	/**
	 * Test case for GET /data/doctor/specialization.
	 * <p>
	 * Scenario: Valid request to fetch all specialization.  
	 * Expectation: Returns HTTP 200 (OK) with the list of specialization.
	 */
	@Test
	void testGetDoctorSpecializations() throws Exception {
		List<Specialization> specializations = TestDataUtil.getSpecialization();
		
		when(dataService.getDoctorSpecializations()).thenReturn(specializations);
		
		MvcResult result = mockMvc.perform(get("/data/doctor/specialization")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		List<Specialization> actualResponse = RestTestUtil.toObjectFromJson(result.getResponse().getContentAsString(), new TypeReference<List<Specialization>>() {});
		assertEquals(specializations, actualResponse);
	}

	/**
	 * Test case for GET /data/patient/relation.
	 * <p>
	 * Scenario: Valid request to fetch all relations.  
	 * Expectation: Returns HTTP 200 (OK) with the list of relations.
	 */
	@Test
	void testGetPatientRelations() throws Exception {
		List<Relation> relations = List.of(TestDataUtil.getRelation());
		
		when(dataService.getPatientRelations()).thenReturn(relations);
		
		MvcResult result = mockMvc.perform(get("/data/patient/relation")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		
		List<Relation> actualResponse = RestTestUtil.toObjectFromJson(result.getResponse().getContentAsString(), new TypeReference<List<Relation>>() {});
		assertEquals(relations, actualResponse);
	}

	/**
	 * Test case for GET /data/view/holiday.
	 * <p>
	 * Scenario: Valid request to fetch holidays.  
	 * Expectation: Returns HTTP 200 (OK) with the list of holidays with pagination.
	 */
	@Test
	void testViewHolidays() throws Exception {
		Map<String, Object> responseMap = RestTestUtil.prepareResponseMap(TestDataUtil.getHolidays().stream().map(TestConverterUtil::toHolidayDto).collect(Collectors.toList()));
		
		when(dataService.viewHolidays(any(Pageable.class))).thenReturn(responseMap);
		
		mockMvc.perform(get("/data/view/holiday")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").value("1"))
				.andExpect(jsonPath("$.data[0].holidayDate").value("2025-08-09"))
				.andExpect(jsonPath("$.data[1].id").value("2"))
				.andExpect(jsonPath("$.data[1].holidayDate").value("2025-08-15"));
	}

	/**
	 * Test case for GET /data/picture/{fileName}.
	 * <p>
	 * Scenario: Valid request to fetch holidays.  
	 * Expectation: Returns HTTP 200 (OK) with the list of holidays with pagination.
	 */
	@Test
	void testGetPictureByName() throws Exception {
		String fileName = "1_ProfilePic_user.jpg";
		byte[] imageBytes = "Conider this as profile image in bytes".getBytes();
		Resource resource = new ByteArrayResource(imageBytes);
		
		when(dataService.getPictureByName(eq(fileName), anyString())).thenReturn(resource);

		 mockMvc.perform(get("/data/picture/{fileName}", fileName)
		            .param("role", IDoctorConstants.DOCTOR_ROLE))
		            .andExpect(status().isOk())
		            .andExpect(content().bytes(imageBytes));
	}

	/**
	 * Test case for GET /data/picture/{fileName}.
	 * <p>
	 * Scenario: Invalid request to fetch holidays.  
	 * Expectation: Returns HTTP 400 (BAD_REQUEST) with message.
	 */
	@Test
	void testGetPictureByNameInvalid() throws Exception {
		// case when value of fileName is "null"
		mockMvc.perform(get("/data/picture/{fileName}", "null")
				.param("role", IDoctorConstants.DOCTOR_ROLE))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(IResponseConstants.FILE_NAME_CONSTRAINTS));

		// case when fileName is empty(blank)
		mockMvc.perform(get("/data/picture/{fileName}", " ")
				.param("role", IDoctorConstants.DOCTOR_ROLE))
		.andExpect(status().isBadRequest())
		.andExpect(content().string(IResponseConstants.FILE_NAME_CONSTRAINTS));
	}
}
