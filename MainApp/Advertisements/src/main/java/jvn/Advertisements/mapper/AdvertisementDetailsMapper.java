package jvn.Advertisements.mapper;

import jvn.Advertisements.dto.soap.advertisement.AdvertisementDetails;
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

@Component
public class AdvertisementDetailsMapper implements MapperInterface<Advertisement, AdvertisementDetails> {

    private ModelMapper modelMapper;

    @Override
    public Advertisement toEntity(AdvertisementDetails dto) {
        Advertisement entity = new Advertisement();
        entity.setId(dto.getId());
        entity.setCar(dto.getCar());
        PriceList priceList = new PriceList();
        priceList.setId(dto.getPriceList());
        entity.setPriceList(priceList);
        entity.setKilometresLimit(dto.getKilometresLimit());
        entity.setDiscount(dto.getDiscount());
        entity.setDateFrom(getLocalDate(dto.getDateFrom()));
        if (dto.getDateTo() != null) {
            entity.setDateTo(getLocalDate(dto.getDateTo()));
        }
        entity.setPickUpPoint(dto.getPickUpPoint());
        entity.setCDW(dto.isCDW());
        return entity;
    }

    @Override
    public AdvertisementDetails toDto(Advertisement entity) {
        AdvertisementDetails dto = new AdvertisementDetails();
        dto.setId(entity.getId());
        dto.setCar(entity.getCar());
        dto.setPriceList(entity.getPriceList().getId());
        dto.setKilometresLimit(entity.getKilometresLimit());
        dto.setDiscount(entity.getDiscount());
        dto.setDateFrom(getXMLGregorianCalendar(entity.getDateFrom()));
        if (entity.getDateTo() != null) {
            dto.setDateTo(getXMLGregorianCalendar(entity.getDateTo()));
        }

        dto.setPickUpPoint(entity.getPickUpPoint());
        dto.setLogicalStatus(entity.getLogicalStatus().toString());
        dto.setCDW(entity.getCDW());
        return dto;
    }

    private LocalDate getLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        LocalDate localDate = LocalDate.of(
                xmlGregorianCalendar.getYear(),
                xmlGregorianCalendar.getMonth(),
                xmlGregorianCalendar.getDay());
        return localDate;
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(LocalDate localDate) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());
        } catch (DatatypeConfigurationException e) {
            return null;
        }
    }

    @Autowired
    public AdvertisementDetailsMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}