import { UserInfo } from './userInfo';

export class Comment {
    text: string;
    userInfo: UserInfo;
    constructor(text: string) {
        this.text = text;
    }

}