
export class Agent {
    id: number;
    name: string;
    email: string;
    address: string;
    phoneNumber: string;
    taxIdNumber: number;
    constructor(name: string, email: string, address: string, phoneNumber: string, taxIdNumber: number, id?: number) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.taxIdNumber = taxIdNumber;
        this.id = id;
    }

}