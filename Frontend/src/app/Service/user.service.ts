import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {TicketDTO} from '../Model/ticketModel.model';
import {environment} from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/user`;

  createTicket(ticketDTO: TicketDTO, file: File | null): Observable<string> {
    if(ticketDTO.problem_title!= "" && ticketDTO.problem === "") ticketDTO.problem="generic"
    if (!file) {
      return this.http.post(`${this.apiUrl}/load`, ticketDTO, {responseType: 'text' });
    }
    const formData = new FormData();
    formData.append('ticket', JSON.stringify(ticketDTO));
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/load`, formData, { responseType: 'text' });
  }
}
