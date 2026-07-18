import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {noteDTO} from '../Model/note.model';
import {Observable} from 'rxjs';
import {RequestDTO} from '../Model/response_request.model';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private readonly baseUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  createNote(dto: noteDTO): Observable<string> {
    return this.http.post(this.baseUrl + '/create', dto, { responseType: 'text' });
  }

  viewNotes(page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
    return this.http.get<any>(this.baseUrl + '/viewNotes', { params });
  }

  updateNote(id: number, change: string): Observable<string> {
    const body: RequestDTO = { id, change };
    return this.http.post(this.baseUrl + '/updateNote', body, { responseType: 'text' });
  }

  deleteNote(id: number): Observable<string> {
    return this.http.post(this.baseUrl + '/deleteNote', id, {
      headers: { 'Content-Type': 'application/json' },
      responseType: 'text'
    });
  }
}
