import { Client } from "src/app/model/client";
import { HttpErrorResponse } from "@angular/common/http";
import { AuthentificationService } from "./../../service/authentification.service";
import { Message } from "./../../model/message";
import { MessageService } from "./../../service/message.service";
import { ToastrService } from "ngx-toastr";
import { Component, OnInit, Input, ElementRef } from "@angular/core";
import { UserInfo } from "src/app/model/userInfo";
import { RentRequest } from "src/app/model/rentRequest";
import { formatDate } from "@angular/common";
import { Router } from "@angular/router";

@Component({
  selector: "app-view-messages",
  templateUrl: "./view-messages.component.html",
  styleUrls: ["./view-messages.component.scss"],
})
export class ViewMessagesComponent implements OnInit {
  @Input() messages: Message[];
  @Input() client: Client;
  @Input() rentRequest: RentRequest;

  isLoaded: boolean = false;
  messagesContainer: ElementRef<HTMLDivElement>;
  loggedInUser: string;

  constructor(
    private toastr: ToastrService,
    private messageService: MessageService,
    private authentificationService: AuthentificationService
  ) { }

  ngOnInit() {
    this.getMessages();
    this.loggedInUser = this.authentificationService.getLoggedInUserEmail();
  }

  onSendMessage(text: string) {
    if (!text) {
      this.toastr.error("Please enter message's text", "Send message");
      return;
    }

    const dateAndTime = formatDate(new Date(), "yyyy-MM-dd hh:mm", "en-US");
    const message = new Message(
      text,
      new UserInfo(this.loggedInUser),
      dateAndTime
    );
    console.log(message);

    this.messageService.send(message, this.rentRequest.id).subscribe(
      (data: Message) => {
        this.toastr.success("Success!", "Send message");
        this.getMessages();
        this.scrollIntoView();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Send message");
      }
    );
  }

  getMessages() {
    console.log(this.client.email);
    this.messageService.getMessages(this.rentRequest.id).subscribe(
      (data: Message[]) => {
        this.toastr.success("Success!", "Fetch messages");
        this.messages = data.sort((a, b) => a.id - b.id);
        this.scrollIntoView();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Fetch messages");
      }
    );
  }

  scrollIntoView() {
    if (this.messagesContainer) {
      const { nativeElement } = this.messagesContainer;
      nativeElement.scrollTop = nativeElement.scrollHeight;
    }
  }

}
