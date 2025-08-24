package com.ps.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ps.dto.HolidayDTO;
import com.ps.entity.Holiday;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HolidayMapper {

    HolidayDTO toDto(Holiday holiday);
    
    Holiday toHoliday(HolidayDTO dto);
}