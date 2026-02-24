export enum GameType {
  SEQUENCE = 0,
  DISCOVERY = 1,
  MEMORY = 2,
  COMPLEMENTARY = 3,
}

export enum Difficulty {
  BEGINNER = 0,
  NORMAL = 1,
  HARD = 2,
  CUSTOM = -1,
}

export enum SquareState {
  EMPTY = 0,
  COLORED = 1,
  ICE_BLOCK = 2,
  ICE_BLOCK_BROKEN = 3,
  INVISIBLE = 4,
}

export enum SoundType {
  NO_SOUND = 0,
  GAME_OVER = 1,
  DING = 2,
  ERROR = 3,
  SELECT = 4,
  BLOCK = 5,
}

export enum ComplementaryColorType {
  YELLOW_PURPLE = 0,
  BLUE_ORANGE = 1,
  GREEN_RED = 2,
}

export interface GridConfig {
  columns: number;
  rows: number;
}

export interface InputEvent {
  type: 'down' | 'move' | 'up';
  x: number;
  y: number;
}

export interface GameState {
  gameType: GameType;
  difficulty: Difficulty;
  level: number;
  lives: number;
  score: number;
  isPlaying: boolean;
  isAnimating: boolean;
  isGameOver: boolean;
}

export const DIFFICULTY_GRIDS: Record<Difficulty, GridConfig> = {
  [Difficulty.BEGINNER]: { columns: 2, rows: 3 },
  [Difficulty.NORMAL]: { columns: 4, rows: 6 },
  [Difficulty.HARD]: { columns: 6, rows: 9 },
  [Difficulty.CUSTOM]: { columns: 4, rows: 6 },
};
