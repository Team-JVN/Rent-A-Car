package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.AdvertisementDTO;
import jvn.RentACar.dto.response.AdvertisementWithPicturesDTO;
import jvn.RentACar.enumeration.LogicalStatus;
import jvn.RentACar.enumeration.RentRequestStatus;
import jvn.RentACar.exceptionHandler.InvalidAdvertisementDataException;
import jvn.RentACar.mapper.AdvertisementDtoMapper;
import jvn.RentACar.model.Advertisement;
import jvn.RentACar.model.Picture;
import jvn.RentACar.model.PriceList;
import jvn.RentACar.model.RentInfo;
import jvn.RentACar.repository.AdvertisementRepository;
import jvn.RentACar.service.AdvertisementService;
import jvn.RentACar.service.CarService;
import jvn.RentACar.service.PriceListService;
import jvn.RentACar.service.RentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    private CarService carService;

    private PriceListService priceListService;

    private AdvertisementRepository advertisementRepository;

    private AdvertisementDtoMapper advertisementMapper;

    private RentInfoService rentInfoService;

    @Override
    public Advertisement create(Advertisement createAdvertisementDTO) {
        checkIfCarIsAvailable(createAdvertisementDTO.getCar().getId(), createAdvertisementDTO.getDateFrom(), null);
        createAdvertisementDTO.setCar(carService.get(createAdvertisementDTO.getCar().getId()));
        createAdvertisementDTO.setPriceList(priceListService.get(createAdvertisementDTO.getPriceList().getId()));
        PriceList priceList = createAdvertisementDTO.getPriceList();
        if (priceList.getPricePerKm() != null && createAdvertisementDTO.getKilometresLimit() == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPricePerKm() == null) {
            createAdvertisementDTO.setKilometresLimit(null);
        }
        if (priceList.getPriceForCDW() != null) {
            createAdvertisementDTO.setCDW(true);
        } else {
            createAdvertisementDTO.setCDW(false);
        }
        return advertisementRepository.save(createAdvertisementDTO);
    }

    @Override
    public Advertisement edit(Long id, Advertisement advertisement) {
        Advertisement dbAdvertisement = get(advertisement.getId());
        isEditable(dbAdvertisement);
        checkIfCarIsAvailable(advertisement.getCar().getId(), advertisement.getDateFrom(), advertisement.getId());
        dbAdvertisement.setDateFrom(advertisement.getDateFrom());
        dbAdvertisement.setCar(carService.get(advertisement.getCar().getId()));
        dbAdvertisement.setPriceList(priceListService.get(advertisement.getPriceList().getId()));
        PriceList priceList = dbAdvertisement.getPriceList();
        if (priceList.getPricePerKm() != null && advertisement.getKilometresLimit() == null) {
            throw new InvalidAdvertisementDataException("You have to set kilometres limit.", HttpStatus.BAD_REQUEST);
        }
        if (priceList.getPricePerKm() == null) {
            dbAdvertisement.setKilometresLimit(null);
        } else {
            dbAdvertisement.setKilometresLimit(advertisement.getKilometresLimit());
        }
        if (priceList.getPriceForCDW() != null) {
            dbAdvertisement.setCDW(true);
        } else {
            dbAdvertisement.setCDW(false);
        }

        dbAdvertisement.setDiscount(advertisement.getDiscount());
        return advertisementRepository.save(dbAdvertisement);
    }

    @Override
    public void delete(Long id) {
        Advertisement advertisement = get(id);
        isEditable(advertisement);
        advertisement.setLogicalStatus(LogicalStatus.DELETED);
        advertisementRepository.save(advertisement);
    }

    @Override
    public Advertisement get(Long id) {
        Advertisement advertisement = advertisementRepository.findByIdAndLogicalStatus(id, LogicalStatus.EXISTING);
        if (advertisement == null) {
            throw new InvalidAdvertisementDataException("Requested advertisement does not exist.", HttpStatus.NOT_FOUND);
        }
        return advertisement;
    }

    public List<AdvertisementWithPicturesDTO> getAll(String status) {
        List<Advertisement> ads = null;
        if (status.equals("all")) {
            ads = advertisementRepository.findAllByLogicalStatusNot(LogicalStatus.DELETED);
        } else if (status.equals("active")) {
            ads = advertisementRepository.findAllByLogicalStatusNotAndActive(LogicalStatus.DELETED, true);
        } else {
            ads = advertisementRepository.findAllByLogicalStatusNotAndActive(LogicalStatus.DELETED, false);
        }
        List<AdvertisementWithPicturesDTO> adsDTOList = new ArrayList<>();
        for (Advertisement ad : ads) {
            AdvertisementDTO advertisementDTO = advertisementMapper.toDto(ad);
            List<String> pictures = new ArrayList<>();
            for (Picture picture : ad.getCar().getPictures()) {
                pictures.add(picture.getData());
            }
            adsDTOList.add(new AdvertisementWithPicturesDTO(advertisementDTO, pictures));
        }
        return adsDTOList;
    }

    private void checkIfCarIsAvailable(Long carId, LocalDate advertisementDateFrom, Long advertisementId) {
        if (!advertisementRepository.findByCarIdAndActiveAndLogicalStatus(carId, true, LogicalStatus.EXISTING).isEmpty()) {
            if (advertisementId != null) {
                List<Advertisement> advertisements = advertisementRepository.findByCarIdAndActiveAndLogicalStatus(carId, true, LogicalStatus.EXISTING);
                if (advertisements.size() != 1 || !advertisements.get(0).getId().equals(advertisementId)) {
                    throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new InvalidAdvertisementDataException("Active advertisement for this car already exist!", HttpStatus.BAD_REQUEST);
            }

        }
        List<RentInfo> paidRentInfos = rentInfoService.getPaidRentInfos(carId);
        if (!paidRentInfos.isEmpty()) {
            if (advertisementDateFrom.isBefore(paidRentInfos.get(0).getDateTimeTo().toLocalDate()) || advertisementDateFrom.isEqual(paidRentInfos.get(0).getDateTimeTo().toLocalDate())) {
                throw new InvalidAdvertisementDataException("Car is not available until " + paidRentInfos.get(0).getDateTimeTo().toLocalDate() + " !", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void isEditable(Advertisement advertisement) {
        if (!advertisement.getRentInfos().isEmpty() && advertisementRepository.findByIdAndRentInfosRentRequestRentRequestStatusNotAndLogicalStatus(advertisement.getId(), RentRequestStatus.CANCELED, LogicalStatus.EXISTING) != null) {
            throw new InvalidAdvertisementDataException("This advertisement is in use and therefore can not be edited/deleted.", HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    public AdvertisementServiceImpl(CarService carService, PriceListService priceListService, RentInfoService rentInfoService,
                                    AdvertisementRepository advertisementRepository, AdvertisementDtoMapper advertisementMapper) {
        this.carService = carService;
        this.priceListService = priceListService;
        this.advertisementRepository = advertisementRepository;
        this.advertisementMapper = advertisementMapper;
        this.rentInfoService = rentInfoService;
    }
}
