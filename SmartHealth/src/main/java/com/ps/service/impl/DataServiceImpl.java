package com.ps.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ps.config.props.PathProperties;
import com.ps.constants.IAdminConstants;
import com.ps.constants.IDoctorConstants;
import com.ps.constants.IPatientConstants;
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
import com.ps.service.IDataService;
import com.ps.util.CommonUtil;
import com.ps.util.DataUtil;

/**
 * Service implementation for retrieving application-wide reference data,
 * images, and holiday information.
 * <p>
 * Provides methods to fetch doctor degrees, departments, specializations,
 * patient relations, holiday lists, and stored images by file name and role.
 */
@Service
public class DataServiceImpl implements IDataService {
	
	@Autowired
	private DegreeRepository degreeRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private SpecializationRepository specializationRepository;

	@Autowired
	private PathProperties pathProperties;
	
	@Autowired
	private RelationRepository relationRepository;
	
	@Autowired
	private HolidayRepository holidayRepository;
	
	@Autowired
	private HolidayMapper holidayMapper;
	
	 /**
     * Retrieves the list of all degrees available for doctors.
     *
     * @return a list of {@link Degree} entities
     */
	@Override
	public List<Degree> getDoctorDegrees() {
		return degreeRepository.findAll();
	}

	 /**
     * Retrieves the list of all departments available for doctors.
     *
     * @return a list of {@link Department} entities
     */
	@Override
	public List<Department> getDoctorDepartment() {
		return departmentRepository.findAll();
	}
	
	 /**
     * Retrieves the list of all specializations available for doctors.
     *
     * @return a list of {@link Specialization} entities
     */
	@Override
	public List<Specialization> getDoctorSpecializations() {
		return specializationRepository.findAll();
	}

	/**
     * Retrieves an image resource by its file name and user role.
     * <p>
     * The method determines the storage path based on the specified role:
     * <ul>
     *   <li>{@link IPatientConstants#PATIENT_ROLE} → patient image path</li>
     *   <li>{@link IDoctorConstants#DOCTOR_ROLE} → doctor image path</li>
     *   <li>{@link IAdminConstants#ADMIN_ROLE} → admin image path</li>
     *   <li>Default → general image storage path</li>
     * </ul>
     *
     * @param fileName the name of the image file to retrieve
     * @param role     the role of the user (may be {@code null} or blank for default path)
     * @return the image as a {@link Resource}
     */
	@Override
	public Resource getPictureByName(String fileName, String role) {
		Path filePath;
		if (role == null || role.isBlank()) {
			filePath = Paths.get(pathProperties.getImageStoragePath() + fileName);
		} else {
			switch(role) {
				case IPatientConstants.PATIENT_ROLE:
					filePath = Paths.get(pathProperties.getPatientImagePath() + fileName);
					break;
					
				case IDoctorConstants.DOCTOR_ROLE:
					filePath = Paths.get(pathProperties.getDoctorImagePath() + fileName);
					break;
					
				case IAdminConstants.ADMIN_ROLE:
					filePath = Paths.get(pathProperties.getAdminImagePath() + fileName);
					break;
				default:
					filePath = Paths.get(pathProperties.getImageStoragePath() + fileName);
					break;
			}
		}
		return DataUtil.getResourceFromPath(filePath);
	}

	 /**
     * Retrieves the list of all patient relations (e.g., parent, spouse, sibling).
     *
     * @return a list of {@link Relation} entities
     */
	@Override
	public List<Relation> getPatientRelations() {
		return relationRepository.findAll();
	}
	
	 /**
     * Retrieves a paginated list of holidays.
     * <p>
     * The result contains holiday data converted to {@link HolidayDTO} objects,
     * along with pagination metadata.
     *
     * @param pageable pagination information (page number, size, sort)
     * @return a map containing the list of holiday DTOs and pagination details
     */
	@Override
	public Map<String, Object> viewHolidays(Pageable pageable) {
		Page<Holiday> pages = holidayRepository.findAll(pageable);
		List<HolidayDTO> response = pages.getContent().stream().map(holidayMapper::toDto).collect(Collectors.toList());
		return CommonUtil.prepareResponseMap(response, pages);
	}
}
