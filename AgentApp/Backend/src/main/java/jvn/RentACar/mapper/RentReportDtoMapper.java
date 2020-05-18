package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.RentInfoDTO;
import jvn.RentACar.dto.both.RentReportDTO;

import jvn.RentACar.model.RentInfo;
import jvn.RentACar.model.RentReport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RentReportDtoMapper implements MapperInterface<RentReport, RentReportDTO>{

    private ModelMapper modelMapper;

    @Override
    public RentReport toEntity(RentReportDTO dto) throws ParseException {

        RentReport entity = modelMapper.map(dto, RentReport.class);
        return entity;

    }

    @Override
    public RentReportDTO toDto(RentReport entity) {
        RentReportDTO dto = modelMapper.map(entity, RentReportDTO.class);
        return dto;
    }

    @Autowired
    public RentReportDtoMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

}
