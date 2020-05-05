import { PriceList } from './priceList';
import { CarWithPictures } from 'src/app/model/carWithPictures';

export class AdvertisementWithPictures {
    id: number;
    car: CarWithPictures;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    cdw: boolean;
    pickUpPoint: string;
    dateFrom: string;
    dateTo: string;

    constructor(car: CarWithPictures, priceList: PriceList, discount: number, kilometresLimit: number, cdw: boolean, pickUpPoint: string, dateFrom: string, dateTo?: string, id?: number,
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
    }
}