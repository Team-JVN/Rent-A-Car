package jvn.RentACar.mapper;


import jvn.RentACar.dto.soap.advertisement.AdvertisementDetails;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Car;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.service.CarService;
import jvn.RentACar.service.PriceListService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

@Component
public class AdvertisementDetailsMapper implements MapperInterface<Advertisement, AdvertisementDetails> {

    private ModelMapper modelMapper;

    private CarService carService;

    private PriceListService priceListService;

    @Override
    public Advertisement toEntity(AdvertisementDetails dto) {
        Advertisement entity = new Advertisement();
        entity.setMainAppId(dto.getId());
        entity.setCar(carService.getByMainAppId(dto.getCar()));
        entity.setPriceList(priceListService.getByMainAppId(dto.getPriceList()));
        entity.setKilometresLimit(dto.getKilometresLimit());
        entity.setDiscount(dto.getDiscount());
        entity.setDateFrom(getLocalDate(dto.getDateFrom()));
        if (dto.getDateTo() != null) {
            entity.setDateTo(getLocalDate(dto.getDateTo()));
        }
        entity.setPickUpPoint(dto.getPickUpPoint());
        entity.setCDW(dto.isCDW());
        entity.setLogicalStatus(getLogicalStatus(dto.getLogicalStatus()));
        return entity;
    }

    private LogicalStatus getLogicalStatus(String status) {
        try {
            return LogicalStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AdvertisementDetails toDto(Advertisement entity) {
        AdvertisementDetails dto = new AdvertisementDetails();
        dto.setId(entity.getId());
        dto.setCar(entity.getCar().getMainAppId());
        dto.setPriceList(entity.getPriceList().getMainAppId());
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
    public AdvertisementDetailsMapper(ModelMapper modelMapper, CarService carService, PriceListService priceListService) {
        this.modelMapper = modelMapper;
        this.carService = carService;
        this.priceListService = priceListService;
    }
}