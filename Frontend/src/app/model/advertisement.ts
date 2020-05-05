import { PriceList } from 'src/app/model/priceList';
import { Car } from 'src/app/model/car';
export class Advertisement {
    id: number;
    car: Car;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    cdw: boolean;
    pickUpPoint: string;
    dateFrom: string;
    dateTo: string;

    constructor(car: Car, priceList: PriceList, discount: number, kilometresLimit: number, cdw: boolean, pickUpPoint: string, dateFrom: string, id?: number, dateTo?: string) {
        this.id = id;
        this.car = car;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.cdw = cdw;
        this.pickUpPoint = pickUpPoint;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

}