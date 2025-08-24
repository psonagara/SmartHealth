package com.ps.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ps.dto.request.LeaveRequest;
import com.ps.dto.response.LeaveResponse;
import com.ps.entity.Doctor;
import com.ps.entity.DoctorLeave;

@Mapper(componentModel = "spring")
public interface DoctorLeaveMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationTime", ignore = true)
    @Mapping(target = "updationTime", ignore = true)
    DoctorLeave toDoctorLeave(LeaveRequest request, Doctor doctor, Integer days);
    
    LeaveResponse toLeaveResponse(DoctorLeave leave);
}