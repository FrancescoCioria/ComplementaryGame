import { SoundType } from '../types';

export class AudioManager {
  private ctx: AudioContext | null = null;
  private buffers: Map<SoundType, AudioBuffer> = new Map();
  private loaded: boolean = false;

  async init() {
    if (this.ctx) return;
    this.ctx = new AudioContext();
    await this.loadAll();
  }

  // Resume context on user interaction (browser policy)
  async resume() {
    if (this.ctx?.state === 'suspended') {
      await this.ctx.resume();
    }
    if (!this.ctx) {
      await this.init();
    }
  }

  private async loadAll() {
    const sounds: [SoundType, string][] = [
      [SoundType.DING, '/assets/audio/ding.wav'],
      [SoundType.GAME_OVER, '/assets/audio/gameover.wav'],
      [SoundType.ERROR, '/assets/audio/error.mp3'],
      [SoundType.SELECT, '/assets/audio/select.mp3'],
      [SoundType.BLOCK, '/assets/audio/block.mp3'],
    ];

    await Promise.all(
      sounds.map(async ([type, url]) => {
        try {
          const response = await fetch(url);
          const arrayBuffer = await response.arrayBuffer();
          const audioBuffer = await this.ctx!.decodeAudioData(arrayBuffer);
          this.buffers.set(type, audioBuffer);
        } catch (e) {
          console.warn(`Failed to load sound: ${url}`, e);
        }
      })
    );
    this.loaded = true;
  }

  play(sound: SoundType) {
    if (!this.ctx || sound === SoundType.NO_SOUND) return;
    const buffer = this.buffers.get(sound);
    if (!buffer) return;

    const source = this.ctx.createBufferSource();
    source.buffer = buffer;
    source.connect(this.ctx.destination);
    source.start(0);
  }
}
