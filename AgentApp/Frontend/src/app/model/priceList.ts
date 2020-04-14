export class PriceList {
    pricePerDay: number;
    pricePerKm: number;
    priceForCDW: number;

    constructor(pricePerDay: number, pricePerKm: number, priceForCDW: number) {
        this.pricePerDay = pricePerDay;
        this.pricePerKm = pricePerKm;
        this.priceForCDW = priceForCDW;
    }
}