import { Injectable } from '@angular/core';
import { Model } from './../model/model';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from './../../environments/environment';
import { Subject } from 'rxjs';
import { Make } from '../model/make';

@Injectable({
  providedIn: 'root'
})
export class MakeService {
  url = environment.baseUrl + environment.make;
  updateSuccessEmitter = new Subject<Make>();
  createSuccessEmitter = new Subject<Make>();
  createModelSuccessEmitter = new Subject<Model>();

  constructor(private httpClient: HttpClient, private router: Router) { }

  public create(make: Make): any {
    return this.httpClient.post(this.url, make);
  }

  public edit(make: Make): any {
    return this.httpClient.put(this.url + '/' + make.id, make);
  }

  public getMakes() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }

  public createModel(makeId: number, model: Model): any {
    return this.httpClient.post(this.url + '/' + makeId + '/model', model);
  }

  public editModel(makeId: number, model: Model): any {
    return this.httpClient.put(this.url + '/' + makeId + '/model/' + model.id, model);
  }

  public getModels(makeId: number) {
    return this.httpClient.get(this.url + '/' + makeId + '/models');
  }

  public deleteModel(makeId: number, modelId: number): any {
    return this.httpClient.delete(this.url + '/' + makeId + '/model/' + modelId);
  }
}
