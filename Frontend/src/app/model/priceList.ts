export class PriceList {
    pricePerDay: number;
    pricePerKm: number;
    priceForCDW: number;
    id: number;

    constructor(pricePerDay: number, pricePerKm: number, priceForCDW: number, id?: number) {
        this.pricePerDay = pricePerDay;
        this.pricePerKm = pricePerKm;
        this.priceForCDW = priceForCDW;
        this.id = id;
    }
}