import { Car } from './../model/car';
import { Router } from '@angular/router';
import { BodyStyle } from '../model/bodyStyle';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class CarService {
    url = environment.baseUrl + environment.car;
    updateSuccessEmitter = new Subject<Car>();
    createSuccessEmitter = new Subject<Car>();

    constructor(private httpClient: HttpClient, private router: Router) { }

    public create(formData: FormData): any {
        return this.httpClient.post(this.url, formData);
    }

    public edit(car: Car): any {
        return this.httpClient.put(this.url + '/' + car.id, car);
    }

    public getCars() {
        return this.httpClient.get(this.url);
    }

    public delete(id: number): any {
        return this.httpClient.delete(this.url + '/' + id);
    }


}
