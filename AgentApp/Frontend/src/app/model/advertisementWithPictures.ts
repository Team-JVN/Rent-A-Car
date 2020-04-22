import { Advertisement } from './advertisement';

export class AdvertisementWithPicturesDTO {
    advertisement: Advertisement;
    pictures: string[];
    image: any;
    isImageLoading: boolean;

    constructor(advertisementDTO: Advertisement, pictures: any, image?: any, isImageLoading?: boolean) {
        this.advertisement = advertisementDTO;
        this.pictures = pictures;
        this.image = image;
        this.isImageLoading = isImageLoading;
    }

}