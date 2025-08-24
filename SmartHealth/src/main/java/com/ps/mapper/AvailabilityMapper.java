package com.ps.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ps.dto.response.AVResponse;
import com.ps.entity.Availability;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AvailabilityMapper {

    AVResponse toResponse(Availability availability);
}
