package jvn.Advertisements.mapper;

import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.request.AdvertisementEditAllInfoDTO;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.dto.soap.advertisement.AdvertisementDetails;
import jvn.Advertisements.dto.soap.advertisement.EditPartialAdvertisementDetails;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.model.PriceList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class EditPartialAdvertisementMapper implements MapperInterface<AdvertisementEditDTO, EditPartialAdvertisementDetails> {

    private ModelMapper modelMapper;

    @Override
    public AdvertisementEditDTO toEntity(EditPartialAdvertisementDetails dto) {
        AdvertisementEditDTO entity = new AdvertisementEditDTO();
        entity.setId(dto.getId());
        PriceListDTO priceList = new PriceListDTO();
        priceList.setId(dto.getPriceList());
        entity.setPriceList(priceList);
        entity.setKilometresLimit(dto.getKilometresLimit());
        entity.setDiscount(dto.getDiscount());
        return entity;
    }

    @Override
    public EditPartialAdvertisementDetails toDto(AdvertisementEditDTO entity) {
        EditPartialAdvertisementDetails dto = new EditPartialAdvertisementDetails();
        dto.setId(entity.getId());
        dto.setPriceList(entity.getPriceList().getId());
        dto.setKilometresLimit(entity.getKilometresLimit());
        dto.setDiscount(entity.getDiscount());
        return dto;
    }


    @Autowired
    public EditPartialAdvertisementMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}