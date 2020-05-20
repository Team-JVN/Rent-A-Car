
export class RegistrationClient {
    id: number;
    name: string;
    password: string;
    email: string;
    address: string;
    phoneNumber: string;
    constructor(name: string, email: string, password: string, address: string, phoneNumber: string, id?: number) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

}