import { Client } from './client';
import { RentInfo } from './rentInfo';

export class RentRequest {
    id: number;
    client: Client;
    rentInfos: RentInfo[];
    totalPrice: number;
    rentRequestStatus: string;

    constructor(client: Client, rentInfos: RentInfo[], totalPrice?: number, rentRequestStatus?: string, id?: number) {
        this.client = client;
        this.rentInfos = rentInfos;
        this.totalPrice = totalPrice;
        this.rentRequestStatus = rentRequestStatus;
        this.id = id;
    }

}