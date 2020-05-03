import { PriceList } from './priceList';
import { CarWithPictures } from 'src/app/model/carWithPictures';

export class AdvertisementWithPicturesDTO {
    id: number;
    car: CarWithPictures;
    priceList: PriceList;
    kilometresLimit: number;
    discount: number;
    cdw: boolean;
    pickUpPoint: string;
    dateFrom: string;
    dateTo: string;
    image: any;
    isImageLoading: boolean;

    constructor(car: CarWithPictures, priceList: PriceList, discount: number, kilometresLimit: number, cdw: boolean, pickUpPoint: string, dateFrom: string, dateTo?: string, id?: number,
        image?: any, isImageLoading?: boolean) {
        this.id = id;
        this.car = car;
        this.priceList = priceList;
        this.discount = discount;
        this.kilometresLimit = kilometresLimit;
        this.cdw = cdw;
        this.dateFrom = dateFrom;
        this.pickUpPoint = pickUpPoint;
        this.dateTo = dateTo;
        this.image = image;
        this.isImageLoading = isImageLoading;
    }
}