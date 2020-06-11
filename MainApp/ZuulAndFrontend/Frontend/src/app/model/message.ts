import { UserInfo } from "./userInfo";
import { User } from "./user";
import { RentRequest } from "./rentRequest";

export class Message {
  id: number;
  text: string;
  sender: UserInfo;
  dateAndTime: string;
  rentRequest: RentRequest;

  constructor(
    text: string,
    sender: UserInfo,
    dateAndTime: string,
    rentRequest: RentRequest,
    id?: number
  ) {
    this.text = text;
    this.sender = sender;
    this.rentRequest = rentRequest;
    this.dateAndTime = dateAndTime;
    this.id = id;
  }
}
