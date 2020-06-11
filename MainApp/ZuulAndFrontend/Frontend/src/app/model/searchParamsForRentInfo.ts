import { AdvertisementFromSearch } from './advertisementFromSearch';

export class SearchParamsForRentInfo {
    dateTimeFrom: string;
    dateTimeTo: string;
    pickUpPoint: string;
    advertisement: AdvertisementFromSearch;
    optedForCDW: boolean;

    constructor(dateTimeFrom: string, dateTimeTo: string, pickUpPoint: string, advertisement: AdvertisementFromSearch, optedForCDW?: boolean) {
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.pickUpPoint = pickUpPoint;
        this.optedForCDW = optedForCDW;
        this.advertisement = advertisement;
    }
}