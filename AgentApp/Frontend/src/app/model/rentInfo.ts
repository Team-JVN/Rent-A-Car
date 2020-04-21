import { Advertisement } from './advertisement';

export class RentInfo {
    id: number;
    dateTimeFrom: string;
    dateTimeTo: string;
    pickUpPoint: string;
    optedForCDW: boolean;
    advertisement: Advertisement;

    constructor(dateTimeFrom: string, dateTimeTo: string, pickUpPoint: string, optedForCDW: boolean, advertisement: Advertisement, id?: number) {
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.pickUpPoint = pickUpPoint;
        this.optedForCDW = optedForCDW;
        this.advertisement = advertisement;
        this.id = id;
    }

}