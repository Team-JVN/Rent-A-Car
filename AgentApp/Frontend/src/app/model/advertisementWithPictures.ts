import { CarWithPictures } from './carWithPictures';
import { PriceList } from 'src/app/model/priceList';
import { DateTime } from 'luxon';
export class AdvertisementWithPictures {
    id: number;
    carWithPicturesDTO: CarWithPictures;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    dateFrom: DateTime;
    constructor(carWithPicturesDTO: CarWithPictures, priceList: PriceList, discount: number, kilometresLimit: number, dateFrom: DateTime, id?: number) {
        this.id = id;
        this.carWithPicturesDTO = carWithPicturesDTO;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.dateFrom = dateFrom;
    }

}