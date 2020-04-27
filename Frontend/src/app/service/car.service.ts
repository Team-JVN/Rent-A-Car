import { CarEdit } from './../model/carEdit';
import { Car } from './../model/car';
import { Router } from '@angular/router';
import { BodyStyle } from '../model/bodyStyle';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

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

    public edit(formData: FormData, id: number): any {
        return this.httpClient.put(this.url + '/' + id, formData);
    }

    public editPartial(formData: FormData, id: number): any {
        return this.httpClient.put(this.url + '/' + id + '/partial', formData);
    }

    public getPicture(fileName: string, id: number): Observable<Blob> {
        let params = new HttpParams();
        params = params.append('fileName', fileName);

        return this.httpClient.get(this.url + '/' + id + '/picture', {
            params: params,
            responseType: 'blob'
        });
    }

    public getEditType(id: number) {
        return this.httpClient.get(this.url + '/' + id + '/edit');
    }

    public getCars() {
        return this.httpClient.get(this.url);
    }

    public delete(id: number): any {
        return this.httpClient.delete(this.url + '/' + id);
    }


}
