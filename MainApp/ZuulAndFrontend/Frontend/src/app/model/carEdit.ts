export class CarEdit {
    id: number;
    mileageInKm: number;
    kidsSeats: number;
    availableTracking: boolean;

    constructor(mileageInKm: number, kidsSeats: number, availableTracking: boolean, id?: number) {
        this.mileageInKm = mileageInKm;
        this.kidsSeats = kidsSeats;
        this.availableTracking = availableTracking;
        this.id = id;
    }
}