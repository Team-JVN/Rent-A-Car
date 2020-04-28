import { Advertisement } from "./advertisement";
import { RentReport } from "./rentReport";

export class RentInfo {
  id: number;
  dateTimeFrom: string;
  dateTimeTo: string;
  pickUpPoint: string;
  optedForCDW: boolean;
  advertisement: Advertisement;
  availableCreatingRentReport: boolean;
  rentReport: RentReport;

  constructor(
    dateTimeFrom: string,
    dateTimeTo: string,
    pickUpPoint: string,
    optedForCDW: boolean,
    advertisement: Advertisement,
    availableCreatingRentReport?: boolean,
    id?: number,
    rentReport?: RentReport
  ) {
    this.dateTimeFrom = dateTimeFrom;
    this.dateTimeTo = dateTimeTo;
    this.pickUpPoint = pickUpPoint;
    this.optedForCDW = optedForCDW;
    this.advertisement = advertisement;
    this.id = id;
    this.availableCreatingRentReport = availableCreatingRentReport;
    this.rentReport = rentReport;
  }
}
