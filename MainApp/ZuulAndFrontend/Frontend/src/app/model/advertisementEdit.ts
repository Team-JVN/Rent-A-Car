import { PriceList } from './priceList';

export class AdvertisementEdit {
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;

    constructor(priceList: PriceList, discount: number, kilometresLimit: number) {
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
    }
}