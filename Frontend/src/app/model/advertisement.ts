import { PriceList } from 'src/app/model/priceList';
import { Car } from 'src/app/model/car';
export class Advertisement {
    id: number;
    car: Car;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    cdw: boolean;
    pickUpPoint: string;   // TODO: ADD IN CONSTRUCTOR
    dateFrom: string;
    dateTo: string;   // TODO: ADD IN CONSTRUCTOR
    active: boolean;

    constructor(car: Car, priceList: PriceList, discount: number, kilometresLimit: number, cdw: boolean, dateFrom: string, active?: boolean, id?: number) {
        this.id = id;
        this.car = car;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.cdw = cdw;
        this.dateFrom = dateFrom;
        this.active = active;
    }

}