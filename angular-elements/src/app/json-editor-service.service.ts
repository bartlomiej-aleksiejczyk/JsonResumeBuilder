import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class JsonEditorService {
  constructor(private http: HttpClient) { }

  sendJson(jsonData: object, url: string): Observable<any> {
    return this.http.post(url, jsonData);
  }
}
