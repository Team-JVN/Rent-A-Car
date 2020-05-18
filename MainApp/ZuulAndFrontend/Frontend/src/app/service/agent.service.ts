import { Agent } from './../model/agent';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  url = environment.baseUrl + environment.agent;
  createSuccessEmitter = new Subject<Agent>();

  constructor(private httpClient: HttpClient) { }

  public add(agent: Agent): any {
    return this.httpClient.post(this.url, agent);
  }

  public edit(agent: Agent): any {
    return this.httpClient.put(this.url + '/' + agent.id, agent);
  }

  public getAgents() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }

}
