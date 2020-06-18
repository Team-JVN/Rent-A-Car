import { RentInfo } from "./rentInfo";

export class RentReport {
  id: number;
  comment: string;
  // rentInfo: RentInfo;
  additionalCost: number;
  madeMileage: number;

  constructor(
    // rentInfo: RentInfo,
    madeMileage: number,
    comment?: string,
    additionalCost?: number,
    id?: number
  ) {
    // this.rentInfo = rentInfo;
    this.madeMileage = madeMileage;
    this.additionalCost = additionalCost;
    this.comment = comment;
    this.id = id;
  }
}
