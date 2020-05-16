import { UserInfo } from './userInfo';
import { User } from './user';

export class Message {
    id: number;
    text: string;
    senderInfo: UserInfo;
    constructor(text: string, senderInfo: UserInfo, id?: number) {
        this.text = text;
        this.senderInfo = senderInfo;
        this.id = id;
    }

}