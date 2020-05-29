import { UserInfo } from './userInfo';
import { Picture } from './picture';

export class CarFromSearch {
    id: number;
    make: string;
    model: string;
    fuelType: string;
    gearBoxType: string;
    bodyStyle: string;
    mileageInKm: number;
    kidsSeats: number;
    availableTracking: boolean;
    avgRating: number;
    owner: UserInfo;
    pictures: string[];
    image: any;
    isImageLoading: boolean;

    constructor(make: string, model: string, fuelType: string, gearBoxType: string, bodyStyle: string, mileageInKm: number, kidsSeats: number,
        availableTracking: boolean, pictures: string[], id?: number, avgRating?: number, owner?: UserInfo, image?: any, isImageLoading?: boolean) {
        this.make = make;
        this.model = model;
        this.fuelType = fuelType;
        this.gearBoxType = gearBoxType;
        this.bodyStyle = bodyStyle;
        this.mileageInKm = mileageInKm;
        this.kidsSeats = kidsSeats;
        this.availableTracking = availableTracking;
        this.id = id;
        this.avgRating = avgRating;
        this.owner = owner;
        this.image = image;
        this.isImageLoading = isImageLoading;
        this.pictures = pictures;
    }

}