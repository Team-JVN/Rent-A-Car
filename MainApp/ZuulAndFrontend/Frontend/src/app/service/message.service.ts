import { Client } from "./../model/client";
import { Router } from "@angular/router";
import { Message } from "./../model/message";
import { environment } from "./../../environments/environment";
import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class MessageService {
  url = environment.baseUrl + environment.rentRequest;
  updateSuccessEmitter = new Subject<Message>();
  createSuccessEmitter = new Subject<Message>();

  constructor(private httpClient: HttpClient, private router: Router) {}

  public send(message: Message, id: number): any {
    return this.httpClient.post(this.url + "/" + id + "/message", message);
  }

  public getMessages(id: number) {
    return this.httpClient.get(this.url + "/" + id + "/message");
  }
}
