import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Agent } from '../model/agent';

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  url = environment.baseUrl + environment.agent;

  constructor(private httpClient: HttpClient) { }

  public edit(agent: Agent): any {
    return this.httpClient.put(this.url, agent);
  }

  public getLoggedInUser() {
    return this.httpClient.get(this.url + '/profile');
  }
}
