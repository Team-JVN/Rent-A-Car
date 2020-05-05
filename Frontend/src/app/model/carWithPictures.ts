import { UserInfo } from './userInfo';
import { Model } from './model';
import { Make } from './make';
import { BodyStyle } from 'src/app/model/bodyStyle';
import { GearBoxType } from './gearboxType';
import { FuelType } from './fuelType';
import { Picture } from './picture';

export class CarWithPictures {
    id: number;
    make: Make;
    model: Model;
    fuelType: FuelType;
    gearBoxType: GearBoxType;
    bodyStyle: BodyStyle;
    mileageInKm: number;
    kidsSeats: number;
    availableTracking: boolean;
    avgRating: number;
    owner: UserInfo;
    commentsCount: number;
    pictures: Picture[];
    image: any;
    isImageLoading: boolean;

    constructor(make: Make, model: Model, fuelType: FuelType, gearBoxType: GearBoxType, bodyStyle: BodyStyle,
        mileageInKm: number, kidsSeats: number, availableTracking: boolean, pictures: Picture[], id?: number, avgRating?: number, owner?: UserInfo, commentsCount?: number, image?: any,
        isImageLoading?: boolean) {
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
        this.commentsCount = commentsCount;
        this.image = image;
        this.isImageLoading = isImageLoading;
        this.pictures = pictures;
    }

}