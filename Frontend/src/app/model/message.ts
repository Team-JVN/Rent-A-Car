import { User } from './user';

export class Message {
    id: number;
    text: string;
    sender: User;
    senderName: string;
    constructor(text: string, sender: User, senderName: string, id?: number) {
        this.text = text;
        this.sender = sender;
        this.id = id;
        this.senderName = senderName;
    }

}