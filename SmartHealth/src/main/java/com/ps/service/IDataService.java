package com.ps.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Relation;
import com.ps.entity.Specialization;

public interface IDataService {

	List<Degree> getDoctorDegrees();
	List<Department> getDoctorDepartment();
	List<Specialization> getDoctorSpecializations();
	List<Relation> getPatientRelations();
	Resource getPictureByName(String fileName, String role);
	Map<String, Object> viewHolidays(Pageable pageable);
}
