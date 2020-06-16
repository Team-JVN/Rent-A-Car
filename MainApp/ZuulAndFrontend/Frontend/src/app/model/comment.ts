import { UserInfo } from "./userInfo";
import { RentInfo } from "./rentInfo";

export class Comment {
  id: number;
  text: string;
  sender: UserInfo;
  status: string;
  constructor(text: string, sender: UserInfo, status?: string, id?: number) {
    this.text = text;
    this.status = status;
    this.id = id;
    this.sender = sender;
  }
}
