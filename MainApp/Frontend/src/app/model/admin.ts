
export class Admin {
    id: number;
    name: string;
    email: string;
    constructor(name: string, email: string, id?: number) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

}