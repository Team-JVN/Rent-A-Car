import { Feedback } from './../model/feedback';
import { Comment } from './../model/comment';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RentRequest } from '../model/rentRequest';

@Injectable({
  providedIn: 'root'
})
export class RentRequestService {
  url = environment.baseUrl + environment.rentRequest;
  updateSuccessEmitter = new Subject<RentRequest>();
  createSuccessEmitter = new Subject<RentRequest>();

  constructor(private httpClient: HttpClient) { }

  public create(rentRequest: RentRequest): any {
    return this.httpClient.post(this.url, rentRequest);
  }

  public edit(rentRequest: RentRequest): any {
    return this.httpClient.put(this.url + '/' + rentRequest.id, rentRequest);
  }

  public accept(rentRequest: RentRequest): any {
    return this.httpClient.put(this.url + '/' + rentRequest.id, { status: "PAID" });
  }

  public reject(rentRequest: RentRequest): any {
    return this.httpClient.put(this.url + '/' + rentRequest.id, { status: "CANCELED" });
  }

  public getRentRequests(status: string) {
    return this.httpClient.get(this.url + '/all/' + status);
  }

  public getClientRentRequests(status: string) {
    return this.httpClient.get(this.url + '/' + status + '/mine');
  }

  public getAdvertisementsRentRequests(advertisementId: number, status: string) {
    return this.httpClient.get(this.url + '/' + status + '/advertisement/' + advertisementId);
  }

  public get(id: number) {
    return this.httpClient.get(this.url + "/" + id);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }

  public cancel(id: number): any {
    return this.httpClient.put(this.url + '/' + id, { status: "CANCELED" });
  }

  public getRentInfoFeedback(rentInfoId: number) {
    return this.httpClient.get(this.url + '/rent-info/' + rentInfoId);
  }

  public createComment(comment: Comment, rentInfoId: number, rentRequestId: number): any {
    return this.httpClient.post(this.url + "/" + rentRequestId + "/rent-info/" + rentInfoId + "/comment", comment);
  }

  public leaveFeedback(feedback: Feedback, rentInfoId: number, rentRequestId: number): any {
    return this.httpClient.post(this.url + "/" + rentRequestId + "/rent-info/" + rentInfoId + "/feedback", feedback);
  }

  public getAdvertisementEditType(id: number) {
    return this.httpClient.get(this.url + '/advertisement/' + id + '/edit-type');
  }

}
