export class SearchParams {
    dateTimeFrom: string;
    dateTimeTo: string;
    pickUpPoint: string;
    make: string;
    model: string;
    fuelType: string;
    gearBoxType: string;
    bodyStyle: string;
    minRating: number;
    minPricePerDay: number;
    maxPricePerDay: number;
    kidsSeats: number;
    mileageInKm: number;
    kilometresLimit: number;
    cdw: boolean;

    constructor(dateTimeFrom: string, dateTimeTo: string, pickUpPoint: string, make: string, model: string, fuelType: string,
        gearBoxType: string, bodyStyle: string, minRating: number, minPricePerDay: number, maxPricePerDay: number,
        kidsSeats: number, mileageInKm: number, kilometresLimit: number, cdw: boolean) {

        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
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