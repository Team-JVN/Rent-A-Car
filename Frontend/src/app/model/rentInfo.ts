import { AdvertisementWithPicturesDTO } from 'src/app/model/advertisementWithPictures';
import { Advertisement } from './advertisement';

export class RentInfo {
    id: number;
    dateTimeFrom: string;
    dateTimeTo: string;
    optedForCDW: boolean;
    advertisement: AdvertisementWithPicturesDTO;

    constructor(dateTimeFrom: string, dateTimeTo: string, optedForCDW: boolean, advertisement: AdvertisementWithPicturesDTO, id?: number) {
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.optedForCDW = optedForCDW;
        this.advertisement = advertisement;
        this.id = id;
    }

}