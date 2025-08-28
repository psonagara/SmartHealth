package com.ps.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ps.entity.Degree;
import com.ps.entity.Department;
import com.ps.entity.Relation;
import com.ps.entity.Specialization;
import com.ps.repo.DegreeRepository;
import com.ps.repo.DepartmentRepository;
import com.ps.repo.RelationRepository;
import com.ps.repo.SpecializationRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Component
public class DataInitializer {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private DegreeRepository degreeRepository;

	@Autowired
	private SpecializationRepository specializationRepository;

	@Autowired
	private RelationRepository relationRepository;

	@PostConstruct
	@Transactional
	public void initData() {
		setDepartments();
		setDegrees();
		setSpecializations();
		setRelations();
	}

	private void setDepartments() {
		List<String> departments = List.of("Cardiology", "Neurology", "Pediatrics", "Orthopedics");
		for (String name : departments) {
			departmentRepository.findByName(name).orElseGet(() -> {
				Department department = new Department();
				department.setName(name);
				return departmentRepository.save(department);
			});
		}
	}

	private void setDegrees() {
		List<String> degrees = List.of("MBBS", "MD", "DO", "PhD");
		for (String name : degrees) {
			degreeRepository.findByName(name).orElseGet(() -> {
				Degree degree = new Degree();
				degree.setName(name);
				return degreeRepository.save(degree);
			});
		}
	}

	private void setSpecializations() {
		List<String> specializations = List.of("Cardiology Specialist", "Dermatologist", "Radiologist");
		for (String name : specializations) {
			specializationRepository.findByName(name).orElseGet(() -> {
				Specialization specialization = new Specialization();
				specialization.setName(name);
				return specializationRepository.save(specialization);
			});
		}
	}

	private void setRelations() {
		List<String> relations = List.of("Father", "Mother", "Spouse", "Child");
		for (String name : relations) {
			relationRepository.findByName(name).orElseGet(() -> {
				Relation relation = new Relation();
				relation.setName(name);
				return relationRepository.save(relation);
			});
		}
	}
}
