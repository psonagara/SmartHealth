package com.ps.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.ps.dto.request.AGRequest;
import com.ps.dto.response.AGPreferenceResponse;
import com.ps.entity.AGPreference;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AGPreferenceMapper {

    void updateAGPreferenceFromRequest(AGRequest request, @MappingTarget AGPreference preference);

    AGPreferenceResponse toResponse(AGPreference preference);
}
