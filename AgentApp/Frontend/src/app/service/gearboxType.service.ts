import { GearBoxType } from './../model/gearboxType';
import { FuelType } from './../model/fuelType';
import { Router } from '@angular/router';
import { BodyStyle } from './../model/bodystyle';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class GearboxTypeService {
    url = environment.baseUrl + environment.gearBoxType;
    updateSuccessEmitter = new Subject<GearBoxType>();
    createSuccessEmitter = new Subject<GearBoxType>();

    constructor(private httpClient: HttpClient, private router: Router) { }

    public create(gearBoxType: GearBoxType): any {
        return this.httpClient.post(this.url, gearBoxType);
    }

    public edit(gearBoxType: GearBoxType): any {
        return this.httpClient.put(this.url, gearBoxType);
    }

    public getGearboxTypes() {
        return this.httpClient.get(this.url);
    }

    public delete(id: number): any {
        return this.httpClient.delete(this.url + '/' + id);
    }

}
