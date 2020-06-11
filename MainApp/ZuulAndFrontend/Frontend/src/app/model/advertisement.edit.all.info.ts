import { PriceList } from 'src/app/model/priceList';

export class AdvertisementEditAllInfo {
    id: number;
    car: number;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    pickUpPoint: string;
    dateFrom: string;
    dateTo: string;

    constructor(car: number, priceList: PriceList, discount: number, kilometresLimit: number, pickUpPoint: string, dateFrom: string, id: number, dateTo?: string) {
        this.id = id;
        this.car = car;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.pickUpPoint = pickUpPoint;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

}