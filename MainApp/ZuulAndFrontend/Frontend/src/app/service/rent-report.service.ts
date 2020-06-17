import { environment } from "./../../environments/environment";
import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { RentReport } from "../model/rentReport";

@Injectable({
  providedIn: "root",
})
export class RentReportService {
  url = environment.baseUrl + environment.rentReport;
  updateSuccessEmitter = new Subject<RentReport>();
  createSuccessEmitter = new Subject<RentReport>();

  constructor(private httpClient: HttpClient) {}

  public create(rentReport: RentReport, rentInfoId: number): any {
    return this.httpClient.post(this.url + "/" + rentInfoId, rentReport);
  }

  public edit(rentReport: RentReport): any {
    // return this.httpClient.put(this.url + '/' + rentRequest.id, rentRequest);
  }

  public getAll() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    // return this.httpClient.delete(this.url + '/' + id);
  }
}
