import { SearchParams } from './../model/searchParams';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class SearchService {
    url = environment.baseUrl + environment.searchAdvertisements;

    constructor(private httpClient: HttpClient) { }

    public get(id: number) {
        return this.httpClient.get(this.url + "/" + id);
    }

    public getAll() {
        return this.httpClient.get(this.url);
    }

    public searchAdvertisements(searchParams: SearchParams) {
        return this.httpClient.post(this.url + "/search", searchParams);
    }

    public getAllMy(status: string) {
        return this.httpClient.get(this.url + '/all/' + status);
    }
}
