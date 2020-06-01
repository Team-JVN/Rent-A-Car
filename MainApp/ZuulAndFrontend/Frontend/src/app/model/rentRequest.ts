import { RentInfo } from './rentInfo';

export class RentRequest {
    id: number;
    client: number;
    rentInfos: RentInfo[];
    totalPrice: number;
    rentRequestStatus: string;
    constructor(client: number, rentInfos: RentInfo[], totalPrice?: number, rentRequestStatus?: string, id?: number) {
        this.client = client;
        this.rentInfos = rentInfos;
        this.totalPrice = totalPrice;
        this.rentRequestStatus = rentRequestStatus;
        this.id = id;
    }

}