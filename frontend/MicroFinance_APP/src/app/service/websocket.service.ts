// websocket.service.ts
import { Injectable } from '@angular/core';
import { Client, over, Frame } from 'stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject } from 'rxjs';
import { Dealer } from '../model/Dealer';
import { HPLoan } from './hp-loan.service';
import { Smeloan } from '../model/SmeLoan';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompClient!: Client;
  private dealerSubject = new BehaviorSubject<Dealer | null>(null);
  private statusSubject = new BehaviorSubject<Dealer | null>(null);
  private hpLoanSubject = new BehaviorSubject<HPLoan | null>(null);
private hpLoanStatusSubject = new BehaviorSubject<HPLoan | null>(null);
private smeLoanSubject = new BehaviorSubject<Smeloan | null>(null);
private smeLoanStatusSubject = new BehaviorSubject<Smeloan | null>(null);

  private isConnected = false;

  constructor() {
    this.initializeWebSocketConnection();
  }

  private initializeWebSocketConnection() {
    const serverUrl = 'http://localhost:8081/ws';
    const ws = new SockJS(serverUrl);
    this.stompClient = over(ws);

    this.stompClient.connect({},
      (frame) => this.onConnect(frame),
      (error) => this.onError(error)
    );
  }

  

  private onConnect(frame: any) {
    this.isConnected = true;
    console.log('WebSocket connected:', frame);
    
    this.stompClient.subscribe('/topic/dealers', (message) => {
      this.dealerSubject.next(JSON.parse(message.body));
    });
    
    this.stompClient.subscribe('/topic/dealer-status', (message) => {
      this.statusSubject.next(JSON.parse(message.body));
    });
    this.stompClient.subscribe('/topic/hp-loans', (message) => {
      this.hpLoanSubject.next(JSON.parse(message.body));
    });
    
    this.stompClient.subscribe('/topic/hp-loan-status', (message) => {
      this.hpLoanStatusSubject.next(JSON.parse(message.body));
    });
    this.stompClient.subscribe('/topic/sme-loans', (message) => {
      this.smeLoanSubject.next(JSON.parse(message.body));
    });
    
    this.stompClient.subscribe('/topic/sme-loan-status', (message) => {
      this.smeLoanStatusSubject.next(JSON.parse(message.body));
    });
  }

  

  private onError(error: string | Frame | Event) {
    this.isConnected = false;
    setTimeout(() => this.initializeWebSocketConnection(), 5000);
  }

  getNewDealers() {
    return this.dealerSubject.asObservable();
  }

  getStatusUpdates() {
    return this.statusSubject.asObservable();
  }

  getNewHPLoans() {
    return this.hpLoanSubject.asObservable();
  }
  
  getHPLoanStatusUpdates() {
    return this.hpLoanStatusSubject.asObservable();
  }
  
  getNewSMELoans() {
    return this.smeLoanSubject.asObservable();
  }
  
  getSMELoanStatusUpdates() {
    return this.smeLoanStatusSubject.asObservable();
  }
}