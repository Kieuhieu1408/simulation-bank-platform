import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: any): void {
    console.error('GlobalErrorHandler caught an error:', error);
    // In a real app, you would map this to a Toast notification or state update
  }
}
