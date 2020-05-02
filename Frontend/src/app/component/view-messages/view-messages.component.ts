import { Client } from 'src/app/model/client';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthentificationService } from './../../service/authentification.service';
import { Message } from './../../model/message';
import { MessageService } from './../../service/message.service';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit, Input, ElementRef } from '@angular/core';
import { UserInfo } from 'src/app/model/userInfo';

@Component({
  selector: 'app-view-messages',
  templateUrl: './view-messages.component.html',
  styleUrls: ['./view-messages.component.scss']
})
export class ViewMessagesComponent implements OnInit {
  @Input() messages: Message[];
  @Input() client: Client;

  messagesContainer: ElementRef<HTMLDivElement>;
  senderName = "Pera";

  constructor(private toastr: ToastrService, private messageService: MessageService,
    private authentificationService: AuthentificationService) { }

  ngOnInit() {
  }


  onSendMessage(message: string) {

    if (!message) {
      this.toastr.error("Please enter message's text", 'Send message');
      return;
    }
    const loggedInUser = this.authentificationService.getLoggedInUser();
    const messsage = new Message(message, new UserInfo(loggedInUser.email));

    this.messageService.send(messsage).subscribe(
      (data: Message) => {
        this.toastr.success('Success!', 'Send message');
        this.getMessages();
        this.scrollIntoView();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Send message');
      }
    );
  }

  getMessages() {
    this.messageService.getMessages(this.client).subscribe(
      (data: Message[]) => {
        this.toastr.success('Success!', 'Fetch messages');
        this.messages = data;
        this.scrollIntoView();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Fetch messages');
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
