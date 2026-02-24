export type UpdateFn = (dt: number) => void;
export type RenderFn = () => void;

export class GameLoop {
  private animId: number = 0;
  private lastTime: number = 0;
  private running: boolean = false;
  private updateFn: UpdateFn;
  private renderFn: RenderFn;

  constructor(updateFn: UpdateFn, renderFn: RenderFn) {
    this.updateFn = updateFn;
    this.renderFn = renderFn;
  }

  start() {
    if (this.running) return;
    this.running = true;
    this.lastTime = performance.now();
    this.tick(this.lastTime);
  }

  stop() {
    this.running = false;
    if (this.animId) {
      cancelAnimationFrame(this.animId);
      this.animId = 0;
    }
  }

  private tick = (now: number) => {
    if (!this.running) return;
    const dt = Math.min(now - this.lastTime, 50); // cap at 50ms
    this.lastTime = now;
    this.updateFn(dt);
    this.renderFn();
    this.animId = requestAnimationFrame(this.tick);
  };
}
