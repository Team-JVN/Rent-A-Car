import { PriceList } from 'src/app/model/priceList';
import { Car } from 'src/app/model/car';
import { DateTime } from 'luxon';
export class Advertisement {
    id: number;
    car: Car;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    dateFrom: DateTime;
    constructor(car: Car, priceList: PriceList, discount: number, kilometresLimit: number, dateFrom: DateTime, id?: number) {
        this.id = id;
        this.car = car;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.dateFrom = dateFrom;
    }

}