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
import { ActivatedRoute, Router, Params } from "@angular/router";

import * as Stomp from "stompjs";
import * as SockJS from "sockjs-client";
import { environment } from "./../../../environments/environment";

@Component({
  selector: "app-view-messages",
  templateUrl: "./view-messages.component.html",
  styleUrls: ["./view-messages.component.scss"],
})
export class ViewMessagesComponent implements OnInit {
  @Input() messages: Message[];
  @Input() client: Client;
  @Input() rentRequest: RentRequest;

  private stompClient;
  // private serverUrl = environment.baseUrl + environment.socket + "socket";
  private serverUrl = "http://localhost:8083/socket";
  isLoaded: boolean = false;

  messagesContainer: ElementRef<HTMLDivElement>;
  senderName = "Pera";

  constructor(
    private toastr: ToastrService,
    private messageService: MessageService,
    private router: Router,
    private authentificationService: AuthentificationService
  ) {}

  ngOnInit() {
    this.getMessages();
    this.initializeWebSocketConnection();
  }

  onSendMessage(text: string) {
    console.log("PORUKA: " + text);
    console.log("ZAHTEV: " + this.rentRequest.id);
    console.log(this.rentRequest);
    if (!text) {
      this.toastr.error("Please enter message's text", "Send message");
      return;
    }
    const loggedInUser = this.authentificationService.getLoggedInUserEmail();
    const dateAndTime = formatDate(new Date(), "yyyy-MM-dd hh:mm", "en-US");
    const message = new Message(
      text,
      new UserInfo(loggedInUser),
      dateAndTime,
      this.rentRequest
    );
    console.log(message);

    // Primer slanja poruke preko web socketa sa klijenta. URL je
    //  - ApplicationDestinationPrefix definisan u config klasi na serveru (configureMessageBroker() metoda)
    //  - vrednost @MessageMapping anotacije iz kontrolera na serveru
    this.stompClient.send(
      "/socket-subscriber/message",
      {},
      JSON.stringify(message)
    );

    // this.messageService.send(message, this.rentRequest.id).subscribe(
    //   (data: Message) => {
    //     this.toastr.success("Success!", "Send message");
    //     this.getMessages();
    //     this.scrollIntoView();
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     this.toastr.error(httpErrorResponse.error.message, "Send message");
    //   }
    // );
  }

  getMessages() {
    console.log(this.client.email);
    this.messageService.getMessages(this.rentRequest.id).subscribe(
      (data: Message[]) => {
        this.toastr.success("Success!", "Fetch messages");
        this.messages = data.sort(
          (a, b) =>
            new Date(a.dateAndTime).getTime() -
            new Date(b.dateAndTime).getTime()
        );
        console.log("PORUKE: ");
        console.log(this.messages);
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

  initializeWebSocketConnection() {
    // otvaranje konekcije sa serverom
    // serverUrl je vrednost koju smo definisali u registerStompEndpoints() metodi na serveru
    console.log("initializeWebSocketConnection");
    let ws = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(ws);
    let that = this;

    this.stompClient.connect(
      {},
      function () {
        console.log("connecting");
        that.isLoaded = true;
        that.openGlobalSocket();
      },
      function () {
        console.log("error");
      }
    );
  }
  openGlobalSocket() {
    if (this.isLoaded) {
      // pretplata na topic /socket-publisher (definise se u configureMessageBroker() metodi)

      this.stompClient.subscribe(
        "/socket-publisher",
        (message: { body: string }) => {
          console.log("handle result");
          this.handleResult(message);
        }
      );
    }
  }
  // funkcija koja se poziva kada server posalje poruku na topic na koji se klijent pretplatio
  handleResult(message: { body: string }) {
    console.log(message);
    if (message.body) {
      let messageResult: Message = JSON.parse(message.body);
      console.log(messageResult);
      this.messages.push(messageResult);
      this.toastr
        .success("New message recieved", null, {
          timeOut: 3000,
        })
        .onTap.subscribe((action) => {
          this.router.navigate(["/fuel-types"]);
        });
    }
  }
}
