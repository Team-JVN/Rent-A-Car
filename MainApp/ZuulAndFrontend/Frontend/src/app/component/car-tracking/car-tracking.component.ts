import { environment } from 'src/environments/environment';
import { AdvertisementService } from 'src/app/service/advertisement.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { Location } from '@angular/common';
import { LocationInfo } from 'src/app/model/locationInfo';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-car-tracking',
  templateUrl: './car-tracking.component.html',
  styleUrls: ['./car-tracking.component.css']
})
export class CarTrackingComponent implements OnInit {
  private serverUrl = environment.baseUrl + '/advertisements/socket'
  private stompClient;
  advId: number;
  isLoaded: boolean = false;
  ws;

  constructor(private activatedRoute: ActivatedRoute,
    private router: Router, private advertisementService: AdvertisementService,
    private toastr: ToastrService, private location: Location) { }

  ngOnInit() {
    this.initializeWebSocketConnection();
    this.activatedRoute.params.subscribe((params: Params) => {
      this.advId = params['id'];
      // this.advertisementService.getLocation(this.advId).subscribe(
      //   (data: LocationInfo) => {
      //     this.toastr.success('Cao je sam jelena', 'Rent Request Details');
      //   },
      //   (httpErrorResponse: HttpErrorResponse) => {
      //     this.toastr.error(httpErrorResponse.error.message, 'Rent Request Details');
      //     this.location.back();
      //   }
      // )
    });
  }

  initializeWebSocketConnection() {
    var socket = new WebSocket('ws://localhost:8080/advertisements/websocket/socket');
    this.ws = Stomp.over(socket);

    this.ws.connect({}, function (frame) {

      this.ws.subscribe("/socket-publisher", function (message) {
        this.toastr.success(message.body, 'Socet success');
      });
    }, function (error) {
      alert("STOMP error " + error);
    });
  }
  // initializeWebSocketConnection() {

  //   // otvaranje konekcije sa serverom
  //   // serverUrl je vrednost koju smo definisali u registerStompEndpoints() metodi na serveru
  //   let ws = new SockJS(this.serverUrl);
  //   this.stompClient = Stomp.over(ws);
  //   let that = this;

  //   this.stompClient.connect({}, function () {
  //     that.isLoaded = true;
  //     that.openGlobalSocket()
  //   });

  // }

  // openGlobalSocket() {
  //   if (this.isLoaded) {
  //     this.stompClient.subscribe("/socket-publisher", (message: any) => {
  //       console.log("1");
  //       console.log(message);
  //       // console.log(message.body)
  //       // this.handleResult(location);
  //     });
  //   }
  // }

  // handleResult(location: LocationInfo) {
  //   console.log("2")
  //   if (location.name) {
  //     console.log("3")
  //     this.toastr.success(location.name, 'Rent Request Details');
  //   }
  // }
}
