import { AdvertisementWithPictures } from 'src/app/model/advertisementWithPictures';

export class RentInfo {
  id: number;
  dateTimeFrom: string;
  dateTimeTo: string;
  optedForCDW: boolean;
  advertisement: AdvertisementWithPictures;
  index: number;
  inBundle: boolean;
  additionalCost: number;
  paid: boolean;
  constructor(dateTimeFrom: string, dateTimeTo: string, optedForCDW: boolean, advertisement: AdvertisementWithPictures, id?: number,
    additionalCost?: number, paid?: boolean) {
    this.dateTimeFrom = dateTimeFrom;
    this.dateTimeTo = dateTimeTo;
    this.optedForCDW = optedForCDW;
    this.advertisement = advertisement;
    this.id = id;
    this.inBundle = false;
    this.additionalCost = additionalCost;
    this.paid = paid;
  }

}