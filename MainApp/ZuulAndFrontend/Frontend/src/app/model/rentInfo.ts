import { AdvertisementFromSearch } from "./advertisementFromSearch";
import { RentReport } from "./rentReport";
import { Comment } from "./comment";

export class RentInfo {
  id: number;
  dateTimeFrom: string;
  dateTimeTo: string;
  optedForCDW: boolean;
  advertisement: AdvertisementFromSearch;
  inBundle: boolean;
  index: number;
  additionalCost: number;
  paid: boolean;
  comments: Comment[];
  rentReport: RentReport;

  constructor(
    dateTimeFrom: string,
    dateTimeTo: string,
    optedForCDW: boolean,
    advertisement: AdvertisementFromSearch,
    comments?: Comment[],
    id?: number,
    additionalCost?: number,
    paid?: boolean,
    rentReport?: RentReport
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
  }
}
