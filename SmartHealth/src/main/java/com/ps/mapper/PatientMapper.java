package com.ps.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.ps.dto.request.PatientProfileRequest;
import com.ps.dto.response.APSResponse;
import com.ps.dto.response.PatientProfileResponse;
import com.ps.entity.Patient;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "profilePicPath", ignore = true)
	@Mapping(target = "profileComplete", ignore = true)
	@Mapping(target = "isActive", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "creationTime", ignore = true)
	@Mapping(target = "updationTime", ignore = true)
	Patient toPatient(PatientProfileRequest request);
	
	PatientProfileResponse toPatientProfileResponse(Patient patient);
	
	 @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	 @Mapping(target = "email", ignore = true)
	 @Mapping(target = "phone", ignore = true)
	 @Mapping(target = "password", ignore = true)
	 @Mapping(target = "profileComplete", constant = "true")
	 void updatePatientFromRequest(PatientProfileRequest request, @MappingTarget Patient patient);
	 
	 APSResponse toAPSResponse(Patient patient);
}
