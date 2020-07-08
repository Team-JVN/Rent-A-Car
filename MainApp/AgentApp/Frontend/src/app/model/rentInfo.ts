import { AdvertisementWithPictures } from "src/app/model/advertisementWithPictures";
import { RentReport } from "./rentReport";
import { Comment } from "./comment";

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
  comments: Comment[];
  rentReport: RentReport;
  rating: number;
  constructor(
    dateTimeFrom: string,
    dateTimeTo: string,
    optedForCDW: boolean,
    advertisement: AdvertisementWithPictures,
    comments?: Comment[],
    id?: number,
    additionalCost?: number,
    paid?: boolean,
    rentReport?: RentReport,
    rating?: number
  ) {
    this.dateTimeFrom = dateTimeFrom;
    this.dateTimeTo = dateTimeTo;
    this.optedForCDW = optedForCDW;
    this.advertisement = advertisement;
    this.comments = comments;
    this.id = id;
    this.inBundle = false;
    this.additionalCost = additionalCost;
    this.paid = paid;
    this.rentReport = rentReport;
    this.rating = rating;
  }
}
