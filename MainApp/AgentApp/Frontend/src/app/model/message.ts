import { UserInfo } from "./userInfo";
import { User } from "./user";
import { RentRequest } from "./rentRequest";

export class Message {
  id: number;
  text: string;
  sender: UserInfo;
  dateAndTime: string;

  constructor(
    text: string,
    sender: UserInfo,
    dateAndTime: string,
    id?: number
  ) {
    this.text = text;
    this.sender = sender;
    this.dateAndTime = dateAndTime;
    this.id = id;
  }
}
