export class RentInfo {
    id: number;
    dateTimeFrom: string;
    dateTimeTo: string;
    optedForCDW: boolean;
    advertisement: number;
    index: number;
    inBundle: boolean;

    constructor(dateTimeFrom: string, dateTimeTo: string, optedForCDW: boolean, advertisement: number, id?: number) {
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.optedForCDW = optedForCDW;
        this.advertisement = advertisement;
        this.id = id;
        this.inBundle = false;
    }

}