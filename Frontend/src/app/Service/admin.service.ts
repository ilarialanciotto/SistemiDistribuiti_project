import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../environments/environment';
import {RequestDTO} from '../Model/response_request.model';


@Injectable({
  providedIn: 'root'
})
export class TicketAdminService {

  private readonly BASE_URL = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  updateState(id_t : number , newState: String): Observable<string> {

    const request: RequestDTO = {
      id: id_t,
      change: newState
    };

    return this.http.post<string>(`${this.BASE_URL}/updateState`, request, {responseType: 'text' as 'json'});
  }

  getGlobalStats(): Observable<any> {
    return this.http.get<any>(`${this.BASE_URL}/report`);
  }

  deleteTicket(id : number): Observable<string> {
    return this.http.post<string>(`${this.BASE_URL}/deleteTicket`, id, {responseType: 'text' as 'json'});
  }

  viewTickets(page: number, size: number): Observable<any> {
    let queryParams = new HttpParams()
      .append('page', page.toString())
      .append('size', size.toString());
    return this.http.get<any>(`${this.BASE_URL}/view`, { params: queryParams });
  }

  searchTickets(filters: any): Observable<any> {
    let queryParams = new HttpParams();
    Object.keys(filters).forEach(key => {
      if (filters[key] !== undefined && filters[key] !== null && filters[key] !== '') {
        queryParams = queryParams.append(key, filters[key].toString());
      }
    });
    return this.http.get<any>(`${this.BASE_URL}/search`, { params: queryParams });
  }

}
