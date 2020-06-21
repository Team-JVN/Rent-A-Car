package jvn.RentACar.mapper;

import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.soap.rentrequest.RentInfoDetails;
import jvn.RentACar.exceptionHandler.InvalidRentRequestDataException;
import jvn.RentACar.model.Comment;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.service.AdvertisementService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class RentInfoDetailsMapper implements MapperInterface<RentInfo, RentInfoDetails> {
    private ModelMapper modelMapper;

    private AdvertisementService advertisementService;

    @Override

    public RentInfo toEntity(RentInfoDetails dto) {
        RentInfo rentInfo = new RentInfo();
        rentInfo.setId(null);
        rentInfo.setMainAppId(dto.getId());
        rentInfo.setDateTimeFrom(getLocalDateTime(dto.getDateTimeFrom()));
        rentInfo.setDateTimeTo(getLocalDateTime(dto.getDateTimeTo()));
        rentInfo.setAdvertisement(advertisementService.getByMainAppId(dto.getAdvertisement()));
        rentInfo.setOptedForCDW(dto.isOptedForCDW());
        rentInfo.setRating(dto.getRating() == null ? null : dto.getRating().intValue());
//        rentInfo.setComments(dto.getCommentDetails());
        rentInfo.setKilometresLimit(dto.getKilometersLimit() == null ? null : dto.getKilometersLimit().intValue());
        rentInfo.setPricePerKm(dto.getPricePerKm());

        return rentInfo;
    }

    @Override
    public RentInfoDetails toDto(RentInfo entity) {
        RentInfoDetails rentInfoDetails = new RentInfoDetails();
        rentInfoDetails.setId(entity.getMainAppId());
        rentInfoDetails.setDateTimeFrom(getXMLGregorianCalendar(entity.getDateTimeFrom()));
        rentInfoDetails.setDateTimeTo(getXMLGregorianCalendar(entity.getDateTimeTo()));
        rentInfoDetails.setAdvertisement(entity.getAdvertisement().getMainAppId());
        rentInfoDetails.setCar(entity.getAdvertisement().getCar().getMainAppId());
        rentInfoDetails.setOptedForCDW(entity.getOptedForCDW());
        rentInfoDetails.setRating(entity.getRating() == null ? null : BigInteger.valueOf(entity.getRating()));
//        rentInfoDetails.getCommentDetails().addAll(entity.getComments());
        rentInfoDetails.setKilometersLimit(entity.getKilometresLimit() == null ? null : BigInteger.valueOf(entity.getKilometresLimit()));
        rentInfoDetails.setPricePerKm(entity.getPricePerKm());
        return rentInfoDetails;
    }

    private LocalDateTime getLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(LocalDateTime localDateTime) {
        try {
            String dateTimeStr = localDateTime.toString();
            if (localDateTime.getSecond() == 0 && localDateTime.getNano() == 0) {
                dateTimeStr += ":00"; // necessary hack because the second part is not optional in XML
            }
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeStr);
        } catch (DatatypeConfigurationException | IllegalArgumentException e) {
            throw new InvalidRentRequestDataException("Invalid date and time format.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public RentInfoDetailsMapper(ModelMapper modelMapper, AdvertisementService advertisementService) {
        this.modelMapper = modelMapper;
        this.advertisementService = advertisementService;
    }
}
