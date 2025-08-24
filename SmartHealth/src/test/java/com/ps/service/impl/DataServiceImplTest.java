package com.ps.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ps.config.props.PathProperties;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPatientConstants;
import com.ps.constants.IResponseConstants;
import com.ps.dto.HolidayDTO;
import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Holiday;
import com.ps.entity.Relation;
import com.ps.entity.Specialization;
import com.ps.mapper.HolidayMapper;
import com.ps.repo.DegreeRepository;
import com.ps.repo.DepartmentRepository;
import com.ps.repo.HolidayRepository;
import com.ps.repo.RelationRepository;
import com.ps.repo.SpecializationRepository;
import com.ps.util.DataUtil;
import com.ps.util.TestConverterUtil;
import com.ps.util.TestDataUtil;

/**
 * This test class contains unit test case for
 * methods of {@link DataServiceImpl}
 * 
 */
@ExtendWith(MockitoExtension.class)
class DataServiceImplTest {
	
	@InjectMocks
	private DataServiceImpl dataService;
	
	@Mock
	private DegreeRepository degreeRepository;
	
	@Mock
	private DepartmentRepository departmentRepository;
	
	@Mock
	private SpecializationRepository specializationRepository;
	
	private MockedStatic<DataUtil> dataUtilMock;
	
	@Mock
	private PathProperties pathProperties;
	
	@Mock
	private RelationRepository relationRepository;
	
	@Mock
	private HolidayRepository holidayRepository;
	
	@Mock
	private HolidayMapper holidayMapper;

	@BeforeEach
	void setUp() throws Exception {
		dataUtilMock = mockStatic(DataUtil.class);
	}

	@AfterEach
	void tearDown() throws Exception {
		dataUtilMock.close();
	}

	/**
	 * This method is used to test {@code getDoctorDegrees}
	 * method of {@link DataServiceImpl}
	 */
	@Test
	void testGetDoctorDegrees() {
		List<Degree> degrees = TestDataUtil.getDegrees();
		when(degreeRepository.findAll()).thenReturn(degrees);
		assertEquals(degrees, dataService.getDoctorDegrees());
	}
	
	/**
	 * This method is used to test {@code getDoctorDepartment}
	 * method of {@link DataServiceImpl}
	 */
	@Test
	void testGetDoctorDepartment() {
		List<Department> departments = TestDataUtil.getDepartment();
		when(departmentRepository.findAll()).thenReturn(departments);
		assertEquals(departments, dataService.getDoctorDepartment());
	}

	/**
	 * This method is used to test {@code getDoctorSpecializations}
	 * method of {@link DataServiceImpl}
	 */
	@Test
	void testGetDoctorSpecializations() {
		List<Specialization> specializations = TestDataUtil.getSpecialization();
		when(specializationRepository.findAll()).thenReturn(specializations);
		assertEquals(specializations, dataService.getDoctorSpecializations());
	}

	/**
	 * This method is used to test {@code getPictureByName}
	 * method of {@link DataServiceImpl}
	 * Scenario where role is patient.
	 */
	@Test
	void testGetPictureByNamePatient() {
		Resource resource = mock(Resource.class);
		String fileName = "1_ProfilePic_user.jpg";
		String filePath = "/images/patient/";
		
		when(pathProperties.getPatientImagePath()).thenReturn(filePath);
		when(DataUtil.getResourceFromPath(Paths.get(filePath + fileName))).thenReturn(resource);
		
		Resource actualResource = dataService.getPictureByName(fileName, IPatientConstants.PATIENT_ROLE);
		assertEquals(resource, actualResource);
	}

	/**
	 * This method is used to test {@code getPictureByName}
	 * method of {@link DataServiceImpl}
	 * Scenario where role is doctor.
	 */
	@Test
	void testGetPictureByNameDoctor() {
		Resource resource = mock(Resource.class);
		String fileName = "1_ProfilePic_user.jpg";
		String filePath = "/images/doctor/";
		
		when(pathProperties.getDoctorImagePath()).thenReturn(filePath);
		when(DataUtil.getResourceFromPath(Paths.get(filePath + fileName))).thenReturn(resource);
		
		Resource actualResource = dataService.getPictureByName(fileName, IDoctorConstants.DOCTOR_ROLE);
		assertEquals(resource, actualResource);
	}

	/**
	 * This method is used to test {@code getPictureByName}
	 * method of {@link DataServiceImpl}
	 * Scenario where role is admin.
	 */
	@Test
	void testGetPictureByNameAdmin() {
		Resource resource = mock(Resource.class);
		String fileName = "1_ProfilePic_user.jpg";
		String filePath = "/images/admin/";
		
		when(pathProperties.getAdminImagePath()).thenReturn(filePath);
		when(DataUtil.getResourceFromPath(Paths.get(filePath + fileName))).thenReturn(resource);
		
		Resource actualResource = dataService.getPictureByName(fileName, IAdminConstants.ADMIN_ROLE);
		assertEquals(resource, actualResource);
	}

	/**
	 * This method is used to test {@code getPictureByName}
	 * method of {@link DataServiceImpl}
	 * Scenario where provided role is not in any switch case then it falls in default.
	 */
	@Test
	void testGetPictureByNameDefault() {
		Resource resource = mock(Resource.class);
		String fileName = "1_ProfilePic_user.jpg";
		String filePath = "/images/";
		
		when(pathProperties.getImageStoragePath()).thenReturn(filePath);
		when(DataUtil.getResourceFromPath(Paths.get(filePath + fileName))).thenReturn(resource);
		
		Resource actualResource = dataService.getPictureByName(fileName, "OTHER_ROLE");
		assertEquals(resource, actualResource);
	}

	/**
	 * This method is used to test {@code getPictureByName}
	 * method of {@link DataServiceImpl}
	 * Scenario where provided role is null or empty.
	 */
	@Test
	void testGetPictureByNameRoleValidation() {
		Resource resource = mock(Resource.class);
		String fileName = "1_ProfilePic_user.jpg";
		String filePath = "/images/";
		
		when(pathProperties.getImageStoragePath()).thenReturn(filePath);
		when(DataUtil.getResourceFromPath(Paths.get(filePath + fileName))).thenReturn(resource);
		
		Resource actualResource = dataService.getPictureByName(fileName, null);
		assertEquals(resource, actualResource);

		actualResource = dataService.getPictureByName(fileName, " ");
		assertEquals(resource, actualResource);
	}

	/**
	 * This method is used to test {@code getPatientRelations}
	 * method of {@link DataServiceImpl}
	 */
	@Test
	void testGetPatientRelations() {
		List<Relation> relations = List.of(TestDataUtil.getRelation());
		when(relationRepository.findAll()).thenReturn(relations);
		assertEquals(relations, dataService.getPatientRelations());
	}

	/**
	 * This method is used to test {@code viewHolidays}
	 * method of {@link DataServiceImpl}
	 */
	@Test
	void testViewHolidays() {
		Pageable pageable = PageRequest.of(0, 10);
		List<Holiday> holidays = TestDataUtil.getHolidays();
		Page<Holiday> pages = new PageImpl<>(holidays, pageable, holidays.size());
		
		when(holidayRepository.findAll(pageable)).thenReturn(pages);
		when(holidayMapper.toDto(holidays.get(0))).thenReturn(TestConverterUtil.toHolidayDto(holidays.get(0)));
		
		Map<String,Object> viewHolidays = dataService.viewHolidays(pageable);
		assertNotNull(viewHolidays);
		@SuppressWarnings("unchecked")
		List<HolidayDTO> response = (List<HolidayDTO>) viewHolidays.get(IResponseConstants.DATA);
		assertNotNull(response);
		assertEquals(holidays.get(0).getId(), response.get(0).getId());
		assertEquals(holidays.get(0).getHolidayDate(), response.get(0).getHolidayDate());
		assertEquals(holidays.get(0).getCreationTime(), response.get(0).getCreationTime());
		assertEquals(holidays.get(0).getReason(), response.get(0).getReason());
	}

}
