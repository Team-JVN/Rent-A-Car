
export class Client {
    id: number;
    name: string;
    password: string;
    email: string;
    address: string;
    phoneNumber: string;
    constructor(name: string, email: string, address: string, phoneNumber: string, id?: number) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

}