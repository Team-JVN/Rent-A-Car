import { Client } from "./client";
import { RentInfo } from "./rentInfo";
import { Message } from "./message";

export class RentRequest {
  id: number;
  client: Client;
  rentInfos: RentInfo[];
  totalPrice: number;
  rentRequestStatus: string;
  messages: Message[];

  constructor(
    client: Client,
    rentInfos: RentInfo[],
    messages?: Message[],
    totalPrice?: number,
    rentRequestStatus?: string,
    id?: number
  ) {
    this.client = client;
    this.rentInfos = rentInfos;
    this.messages = messages;
    this.totalPrice = totalPrice;
    this.rentRequestStatus = rentRequestStatus;
    this.id = id;
  }
}
