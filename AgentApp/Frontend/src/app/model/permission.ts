export class Permission {
    name: string;
    id: number;
    isAssigned: boolean;
    constructor(name: string, id: number) {
        this.name = name;
        this.id = id;
        this.isAssigned = false;
    }
}