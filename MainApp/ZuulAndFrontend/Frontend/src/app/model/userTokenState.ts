export class UserTokenState {
    accessToken: string;
    expireIn: number;
    refreshToken: string;

    constructor(accessToken: string, expireIn: number, refreshToken: string) {
        this.accessToken = accessToken;
        this.expireIn = expireIn;
        this.refreshToken = refreshToken;
    }
}