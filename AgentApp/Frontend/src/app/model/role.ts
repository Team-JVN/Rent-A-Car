import { Permission } from './permission';
export class Role {
    name: string;
    id: number;
    permissions: Permission[];
    constructor(name: string, id: number, permissions: Permission[]) {
        this.name = name;
        this.id = id;
        this.permissions = permissions;
    }
}