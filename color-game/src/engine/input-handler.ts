import { InputEvent } from '../types';

export type InputCallback = (event: InputEvent) => void;

export class InputHandler {
  private canvas: HTMLCanvasElement;
  private callback: InputCallback;
  private isDown: boolean = false;

  constructor(canvas: HTMLCanvasElement, callback: InputCallback) {
    this.canvas = canvas;
    this.callback = callback;
    this.bind();
  }

  private bind() {
    // Mouse events
    this.canvas.addEventListener('mousedown', this.onMouseDown);
    this.canvas.addEventListener('mousemove', this.onMouseMove);
    this.canvas.addEventListener('mouseup', this.onMouseUp);
    // Touch events
    this.canvas.addEventListener('touchstart', this.onTouchStart, { passive: false });
    this.canvas.addEventListener('touchmove', this.onTouchMove, { passive: false });
    this.canvas.addEventListener('touchend', this.onTouchEnd, { passive: false });
  }

  destroy() {
    this.canvas.removeEventListener('mousedown', this.onMouseDown);
    this.canvas.removeEventListener('mousemove', this.onMouseMove);
    this.canvas.removeEventListener('mouseup', this.onMouseUp);
    this.canvas.removeEventListener('touchstart', this.onTouchStart);
    this.canvas.removeEventListener('touchmove', this.onTouchMove);
    this.canvas.removeEventListener('touchend', this.onTouchEnd);
  }

  private getPos(e: MouseEvent | Touch): { x: number; y: number } {
    const rect = this.canvas.getBoundingClientRect();
    return {
      x: e.clientX - rect.left,
      y: e.clientY - rect.top,
    };
  }

  private onMouseDown = (e: MouseEvent) => {
    this.isDown = true;
    const pos = this.getPos(e);
    this.callback({ type: 'down', ...pos });
  };

  private onMouseMove = (e: MouseEvent) => {
    if (!this.isDown) return;
    const pos = this.getPos(e);
    this.callback({ type: 'move', ...pos });
  };

  private onMouseUp = (e: MouseEvent) => {
    this.isDown = false;
    const pos = this.getPos(e);
    this.callback({ type: 'up', ...pos });
  };

  private onTouchStart = (e: TouchEvent) => {
    e.preventDefault();
    this.isDown = true;
    const pos = this.getPos(e.touches[0]);
    this.callback({ type: 'down', ...pos });
  };

  private onTouchMove = (e: TouchEvent) => {
    e.preventDefault();
    if (!this.isDown) return;
    const pos = this.getPos(e.touches[0]);
    this.callback({ type: 'move', ...pos });
  };

  private onTouchEnd = (e: TouchEvent) => {
    e.preventDefault();
    this.isDown = false;
    // Use changedTouches for touchend
    const pos = this.getPos(e.changedTouches[0]);
    this.callback({ type: 'up', ...pos });
  };
}
