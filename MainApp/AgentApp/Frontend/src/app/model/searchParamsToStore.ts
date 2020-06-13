import { BodyStyle } from './bodyStyle';
import { GearBoxType } from './gearboxType';
import { FuelType } from './fuelType';
import { Model } from './model';
import { Make } from './make';

export class SearchParamsToStore {
    dateFrom: string;
    timeFrom: string;
    dateTo: string;
    timeTo: string;
    pickUpPoint: string;
    make: Make;
    model: Model;
    fuelType: FuelType;
    gearBoxType: GearBoxType;
    bodyStyle: BodyStyle;
    minRating: number;
    minPricePerDay: number;
    maxPricePerDay: number;
    kidsSeats: number;
    mileageInKm: number;
    kilometresLimit: number;
    cdw: boolean;

    constructor(dateFrom: string, timeFrom: string, dateTo: string, timeTo: string, pickUpPoint: string, make: Make, model: Model, fuelType: FuelType,
        gearBoxType: GearBoxType, bodyStyle: BodyStyle, minRating: number, minPricePerDay: number, maxPricePerDay: number,
        kidsSeats: number, mileageInKm: number, kilometresLimit: number, cdw: boolean) {

        this.dateFrom = dateFrom;
        this.timeFrom = timeFrom;
        this.dateTo = dateTo;
        this.timeTo = timeTo;
        this.pickUpPoint = pickUpPoint;
        this.make = make;
        this.model = model;
        this.fuelType = fuelType;
        this.gearBoxType = gearBoxType;
        this.bodyStyle = bodyStyle;
        this.minRating = minRating;
        this.minPricePerDay = minPricePerDay;
        this.maxPricePerDay = maxPricePerDay;
        this.kidsSeats = kidsSeats;
        this.mileageInKm = mileageInKm;
        this.kilometresLimit = kilometresLimit;
        this.cdw = cdw;
    }
}