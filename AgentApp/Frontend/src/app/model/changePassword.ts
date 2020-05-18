export class ChangePassword {
    email: string;
    oldPassword: string;
    newPassword: string;
    repeatPassword: string;
    constructor(email: string, oldPassword: string, newPassword: string, repeatPassword: string) {
        this.email = email;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.repeatPassword = repeatPassword;
    }
}