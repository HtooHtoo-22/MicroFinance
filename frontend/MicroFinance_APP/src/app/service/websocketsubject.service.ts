// import { Injectable } from '@angular/core';
// import { WebSocketSubject, webSocket } from 'rxjs/webSocket';
// import { environment } from '../../environments/environment';

// @Injectable({
//   providedIn: 'root'
// })
// export class WebsocketsubjectService {
//   private socket$: WebSocketSubject<any>;

//   constructor() {
//     this.socket$ = webSocket(environment.webSocketUrl);
//   }

//   public connect() {
//     return this.socket$;
//   }

//   public sendMessage(message: any) {
//     this.socket$.next(message);
//   }

//   public close() {
//     this.socket$.complete();
//   }
// }
