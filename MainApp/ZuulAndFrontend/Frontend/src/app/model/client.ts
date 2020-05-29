
export class Client {
    id: number;
    name: string;
    password: string;
    email: string;
    address: string;
    phoneNumber: string;
    status: string;
    canceledReservationCounter: number;
    rejectedCommentsCounter: number;
    canCreateRentRequests: boolean;
    canCreateComments: boolean;
    constructor(name: string, email: string, address: string, phoneNumber: string, id?: number, status?: string, canceledReservationCounter?: number,
        rejectedCommentsCounter?: number, canCreateRentRequests?: boolean, canCreateComments?: boolean) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.status = status;
        this.canceledReservationCounter = canceledReservationCounter;
        this.rejectedCommentsCounter = rejectedCommentsCounter;
        this.canCreateRentRequests = canCreateRentRequests;
        this.canCreateComments = canCreateComments;
    }

}