import { Router } from '@angular/router';
import { BodyStyle } from '../model/bodyStyle';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class BodyStyleService {
    url = environment.baseUrl + environment.bodyStyle;
    updateSuccessEmitter = new Subject<BodyStyle>();
    createSuccessEmitter = new Subject<BodyStyle>();

    constructor(private httpClient: HttpClient, private router: Router) { }

    public create(bodyStyle: BodyStyle): any {
        return this.httpClient.post(this.url, bodyStyle);
    }

    public edit(bodyStyle: BodyStyle): any {
        return this.httpClient.put(this.url + '/' + bodyStyle.id, bodyStyle);
    }

    public getBodyStyles() {
        return this.httpClient.get(this.url);
    }

    public delete(id: number): any {
        return this.httpClient.delete(this.url + '/' + id);
    }

}
