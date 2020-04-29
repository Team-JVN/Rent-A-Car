import { Message } from '@angular/compiler/src/i18n/i18n_ast';
import { Component, OnInit, Input, ElementRef } from '@angular/core';

@Component({
  selector: 'app-view-messages',
  templateUrl: './view-messages.component.html',
  styleUrls: ['./view-messages.component.scss']
})
export class ViewMessagesComponent implements OnInit {
  @Input() messages: Message[];
  messagesContainer: ElementRef<HTMLDivElement>;
  senderName = "Pera";

  constructor() { }

  ngOnInit() {
  }


  onSendMessage(message: string) {
    //success this.scrollIntoView();
    console.log("Haj")
  }

  private scrollIntoView() {
    if (this.messagesContainer) {
      const { nativeElement } = this.messagesContainer;
      nativeElement.scrollTop = nativeElement.scrollHeight;
    }
  }
}
