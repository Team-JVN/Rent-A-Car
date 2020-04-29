import { Model } from './model';
import { Make } from './make';
import { BodyStyle } from 'src/app/model/bodyStyle';
import { GearBoxType } from './gearboxType';
import { FuelType } from './fuelType';

export class Car {
    id: number;
    make: Make;
    model: Model;
    fuelType: FuelType;
    gearBoxType: GearBoxType;
    bodyStyle: BodyStyle;
    mileageInKm: number;
    kidsSeats: number;
    availableTracking: boolean;
    constructor(make: Make, model: Model, fuelType: FuelType, gearBoxType: GearBoxType, bodyStyle: BodyStyle,
        mileageInKm: number, kidsSeats: number, availableTracking: boolean, id?: number) {
        this.make = make;
        this.model = model;
        this.fuelType = fuelType;
        this.gearBoxType = gearBoxType;
        this.bodyStyle = bodyStyle;
        this.mileageInKm = mileageInKm;
        this.kidsSeats = kidsSeats;
        this.availableTracking = availableTracking;
        this.id = id;
    }

}