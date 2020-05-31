import { CarFromSearch } from './carFromSearch';
import { PriceList } from './priceList';

export class AdvertisementFromSearch {
    id: number;
    car: CarFromSearch;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    cdw: boolean;
    pickUpPoint: string;
    dateFrom: string;
    dateTo: string;
    logicalStatus: string;

    constructor(car: CarFromSearch, priceList: PriceList, discount: number, kilometresLimit: number,
        cdw: boolean, pickUpPoint: string, dateFrom: string, logicalStatus: string, dateTo?: string, id?: number
    ) {
        this.id = id;
        this.car = car;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.cdw = cdw;
        this.dateFrom = dateFrom;
        this.pickUpPoint = pickUpPoint;
        this.dateTo = dateTo;
        this.logicalStatus = logicalStatus;
    }
}