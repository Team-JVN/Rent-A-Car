import { AdvertisementWithPictures } from './advertisementWithPictures';

export class SearchParamsForRentInfo {
    dateTimeFrom: string;
    dateTimeTo: string;
    pickUpPoint: string;
    advertisement: AdvertisementWithPictures;
    optedForCDW: boolean;

    constructor(dateTimeFrom: string, dateTimeTo: string, pickUpPoint: string, advertisement: AdvertisementWithPictures, optedForCDW?: boolean) {
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.pickUpPoint = pickUpPoint;
        this.optedForCDW = optedForCDW;
        this.advertisement = advertisement;
    }
}