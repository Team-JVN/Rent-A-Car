import { UserInfo } from "./userInfo";
import { RentInfo } from "./rentInfo";

export class Comment {
  id: number;
  text: string;
  userInfo: UserInfo;
  status: string;
  rentInfo: RentInfo;
  constructor(text: string, rentInfo: RentInfo, status?: string, id?: number) {
    this.text = text;
    this.status = status;
    this.id = id;
    this.rentInfo = rentInfo;
  }
}
