import { Car } from './car';
export class CarWithPictures {
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