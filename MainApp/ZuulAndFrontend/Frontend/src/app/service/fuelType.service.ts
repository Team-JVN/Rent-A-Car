import { FuelType } from './../model/fuelType';
import { Router } from '@angular/router';
import { BodyStyle } from '../model/bodyStyle';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class FuelTypeService {
    url = environment.baseUrl + environment.fuelType;
    updateSuccessEmitter = new Subject<FuelType>();
    createSuccessEmitter = new Subject<FuelType>();

    constructor(private httpClient: HttpClient, private router: Router) { }

    public create(fuelType: FuelType): any {
        return this.httpClient.post(this.url, fuelType);
    }

    public edit(fuelType: FuelType): any {
        return this.httpClient.put(this.url + '/' + fuelType.id, fuelType);
    }

    public getFuelTypes() {
        return this.httpClient.get(this.url);
    }

    public delete(id: number): any {
        return this.httpClient.delete(this.url + '/' + id);
    }

}
