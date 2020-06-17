package jvn.RentACar.mapper;

import jvn.RentACar.dto.request.AdvertisementEditDTO;
import jvn.RentACar.dto.soap.advertisement.EditPartialAdvertisementDetails;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.PriceListService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditPartialAdvertisementMapper implements MapperInterface<Advertisement, EditPartialAdvertisementDetails> {

    private ModelMapper modelMapper;

    private PriceListService priceListService;

    @Override
    public Advertisement toEntity(EditPartialAdvertisementDetails dto) {
        Advertisement entity = new Advertisement();
        entity.setMainAppId(dto.getId());
        entity.setPriceList(priceListService.getByMainAppId(dto.getPriceList()));
        entity.setKilometresLimit(dto.getKilometresLimit());
        entity.setDiscount(dto.getDiscount());
        return entity;
    }

    @Override
    public EditPartialAdvertisementDetails toDto(Advertisement entity) {
        EditPartialAdvertisementDetails dto = new EditPartialAdvertisementDetails();
        dto.setId(entity.getMainAppId());
        dto.setPriceList(entity.getPriceList().getMainAppId());
        dto.setKilometresLimit(entity.getKilometresLimit());
        dto.setDiscount(entity.getDiscount());
        return dto;
    }

    @Autowired
    public EditPartialAdvertisementMapper(ModelMapper modelMapper, PriceListService priceListService) {
        this.modelMapper = modelMapper;
        this.priceListService = priceListService;
    }
}
