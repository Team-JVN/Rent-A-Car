import { UserTokenState } from './userTokenState';
export class LoggedInUser {
    id: number;
    email: string;
    role: string;
    userTokenState: UserTokenState;
    constructor(id: number, email: string, role: string, userTokenState: UserTokenState) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.userTokenState = userTokenState;
    }
}