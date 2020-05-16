import { ClientService } from './../../../service/client.service';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-account-activated',
  templateUrl: './account-activated.component.html',
  styleUrls: ['./account-activated.component.css']
})
export class AccountActivatedComponent implements OnInit {

  success = false;

  constructor(
    private clientService: ClientService,
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private router: Router,
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const param = params.get('id');
      this.activateAccount(+param);
    });
  }

  activateAccount(id: number) {
    this.clientService.activateAccount(id).subscribe(
      () => {
        this.success = true;
      },
      () => {
        this.toastr.error('Your account is already activated', 'Activate account');
        this.router.navigate(['/login']);
      });
  }

}
