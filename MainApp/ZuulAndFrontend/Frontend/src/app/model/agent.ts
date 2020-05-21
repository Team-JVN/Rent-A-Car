
export class Agent {
    id: number;
    name: string;
    email: string;
    address: string;
    phoneNumber: string;
    taxId: number;
    constructor(name: string, email: string, address: string, phoneNumber: string, taxId: number, id?: number) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.taxId = taxId;
        this.id = id;
    }

}