import { Owner } from './owner';
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
    owner: Owner;
    logicalStatus: string;

    constructor(car: CarFromSearch, priceList: PriceList, discount: number, kilometresLimit: number, cdw: boolean, pickUpPoint: string,
        dateFrom: string, owner: Owner, logicalStatus?: string, dateTo?: string, id?: number
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
        this.owner = owner;
        this.logicalStatus = logicalStatus;
    }
}