import { Car } from './car';
<<<<<<< HEAD
export class CarWithPictures {
=======

export class CarWithPicturesDTO {
>>>>>>> listAdvertisements
    carDTO: Car;
    pictures: string[];
    image: any;
    isImageLoading: boolean;

    constructor(carDTO: Car, pictures: any, image?: any, isImageLoading?: boolean) {
        this.carDTO = carDTO;
        this.pictures = pictures;
        this.image = image;
        this.isImageLoading = isImageLoading;
    }

}