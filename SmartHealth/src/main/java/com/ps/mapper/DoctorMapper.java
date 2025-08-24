package com.ps.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.ps.dto.request.DoctorProfileRequest;
import com.ps.dto.response.ADSResponse;
import com.ps.dto.response.DoctorProfileResponse;
import com.ps.entity.Doctor;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profilePicPath", ignore = true)
    @Mapping(target = "profileComplete", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "agPreference", ignore = true)
    @Mapping(target = "creationTime", ignore = true)
    @Mapping(target = "updationTime", ignore = true)
    Doctor toDoctor(DoctorProfileRequest request);
    
    DoctorProfileResponse toDoctorProfileResponse(Doctor doctor);
    
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profilePicPath", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "profileComplete", constant = "true")
    void updateDoctorFromRequest(DoctorProfileRequest request, @MappingTarget Doctor doctor);
    
    ADSResponse toADSResponse(Doctor doctor);
}
