import { Router } from "@angular/router";
import { environment } from "./../../environments/environment";
import { Injectable } from "@angular/core";
import { Subject } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { Comment } from "./../model/comment";

@Injectable({
  providedIn: "root",
})
export class CommentService {
  url = environment.baseUrl + environment.comment;
  approveSuccessEmitter = new Subject<Comment>();
  rejectSuccessEmitter = new Subject<Comment>();

  constructor(private httpClient: HttpClient, private router: Router) {}

  public get(id: number): any {
    return this.httpClient.get(this.url + "/" + id);
  }

  public getAll(status: string) {
    return this.httpClient.get(this.url + "/" + status + "/status");
  }
  public approve(comment: Comment, id: number) {
    return this.httpClient.put(this.url + "/" + id + "/approve", comment);
  }
  public reject(comment: Comment, id: number) {
    return this.httpClient.put(this.url + "/" + id + "/reject", comment);
  }
}
