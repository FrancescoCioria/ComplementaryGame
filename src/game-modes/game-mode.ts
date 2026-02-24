import { GameType, Difficulty, InputEvent } from '../types';
import { CanvasManager } from '../engine/canvas-manager';
import { AudioManager } from '../engine/audio-manager';
import { ComplementaryMode } from './complementary/complementary-mode';

export interface HudState {
  lives?: number;
  level?: number;
  bestMoves?: number;
  currentMoves?: number;
  showReset?: boolean;
  showNextLevel?: boolean;
}

export interface GameModeConfig {
  canvasManager: CanvasManager;
  audio: AudioManager;
  grid: { columns: number; rows: number };
  difficulty: Difficulty;
  onHudUpdate: (state: Partial<HudState>) => void;
  onGameOver: (title: string, message: string) => void;
}

export interface GameMode {
  init(): void;
  update(dt: number): void;
  render(): void;
  handleInput(event: InputEvent): void;
  resize(width: number, height: number): void;
  reset(): void;
  restart(): void;
  nextLevel(): void;
}

export function createGameMode(type: GameType, config: GameModeConfig): GameMode {
  return new ComplementaryMode(config);
}
