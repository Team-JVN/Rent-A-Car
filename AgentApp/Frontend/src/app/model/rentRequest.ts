import { RentInfo } from './rentInfo';
import { Advertisement } from './advertisement';
import { Client } from './client';

export class RentRequest {
    id: number;
    client: Client;
    rentInfos: RentInfo[];
    totalPrice: number;

    constructor(client: Client, rentInfos: RentInfo[], totalPrice?: number, id?: number) {
        this.client = client;
        this.rentInfos = rentInfos;
        this.totalPrice = totalPrice;
        this.id = id;
    }

}