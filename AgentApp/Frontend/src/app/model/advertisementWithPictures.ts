import { Advertisement } from './advertisement';

export class AdvertisementWithPicturesDTO {
    advertisementDTO: Advertisement;
    pictures: string[];
    image: any;
    isImageLoading: boolean;

    constructor(advertisementDTO: Advertisement, pictures: any, image?: any, isImageLoading?: boolean) {
        this.advertisementDTO = advertisementDTO;
        this.pictures = pictures;
        this.image = image;
        this.isImageLoading = isImageLoading;
    }

}