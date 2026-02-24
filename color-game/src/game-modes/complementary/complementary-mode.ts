import { GameType, InputEvent, SoundType, SquareState } from '../../types';
import { CanvasManager } from '../../engine/canvas-manager';
import { GameMode, GameModeConfig } from '../game-mode';
import { calculateGridLayout, getSquarePosition, hitTestGrid, GridLayout } from '../../engine/grid-layout';
import {
  drawGlossySquare, drawEmptySquare, drawStar, drawIceBlock,
  drawVerticalWall, drawHorizontalWall, drawSquareIndicator,
} from '../../engine/renderer';
import { COMPLEMENTARY_LEVELS, ComplementaryLevelData } from '../../types/levels';
import { COMPLEMENTARY_PAIRS, GAME_COLORS } from '../../types/colors';

interface CompSquareData {
  currentState: SquareState;
  originalState: SquareState;
  isStar: boolean;
  hasTakenStar: boolean;
  counter: number;
  penultimo: boolean;
  ultimo: boolean;
}

const HUD_HEIGHT = 45;

export class ComplementaryMode implements GameMode {
  private config: GameModeConfig;
  private cm: CanvasManager;
  private layout!: GridLayout;
  private squares: CompSquareData[] = [];
  private levelData!: ComplementaryLevelData;
  private currentLevel: number = 0;
  private path: number[] = [];
  private lastTouched: number = -1;
  private isPlaying: boolean = false;
  private currentMoves: number = 0;
  private perfectMoves: number = 40;
  private lastWin: number = 0;
  private lastError: number = 0;
  private colorPair: string[] = [];
  private backgroundColor: string = GAME_COLORS.YELLOW;

  constructor(config: GameModeConfig) {
    this.config = config;
    this.cm = config.canvasManager;
  }

  init() {
    this.currentLevel = 0;
    this.initLevel(0);
    this.config.onHudUpdate({
      showReset: true,
      showNextLevel: true,
      bestMoves: this.perfectMoves,
      currentMoves: 0,
    });
  }

  private initLevel(levelIndex: number) {
    if (levelIndex >= COMPLEMENTARY_LEVELS.length) {
      levelIndex = 0; // wrap
    }
    this.levelData = COMPLEMENTARY_LEVELS[levelIndex];
    this.currentLevel = levelIndex;

    this.layout = calculateGridLayout(
      this.cm.width, this.cm.height,
      this.levelData.columns, this.levelData.rows,
      GameType.COMPLEMENTARY,
      HUD_HEIGHT,
    );

    // Random color pair
    const pairIndex = Math.floor(Math.random() * 3);
    this.colorPair = [...COMPLEMENTARY_PAIRS[pairIndex]];

    // Random order for background
    const order = Math.floor(Math.random() * 2);
    this.backgroundColor = this.colorPair[order];

    // Init squares
    this.squares = [];
    for (let i = 0; i < this.levelData.game.length; i++) {
      this.squares.push({
        currentState: this.levelData.game[i],
        originalState: this.levelData.game[i],
        isStar: this.levelData.star === i,
        hasTakenStar: false,
        counter: 0,
        penultimo: false,
        ultimo: false,
      });
    }

    this.path = [];
    this.lastTouched = -1;
    this.isPlaying = false;
    this.currentMoves = 0;
    this.perfectMoves = 40; // default, could be computed by solver
    this.lastWin = 0;

    this.config.onHudUpdate({
      bestMoves: this.perfectMoves,
      currentMoves: 0,
      level: levelIndex + 1,
      showReset: true,
      showNextLevel: true,
    });
  }

  update(_dt: number) {
    // Complementary mode is mostly event-driven, no per-frame animation
  }

  render() {
    this.cm.clear(this.backgroundColor);
    const ctx = this.cm.ctx;

    // Draw squares
    for (let i = 0; i < this.squares.length; i++) {
      const sq = this.squares[i];
      if (sq.currentState === SquareState.INVISIBLE) continue;

      const pos = getSquarePosition(i, this.layout);
      const size = this.layout.size;

      if (sq.currentState === SquareState.EMPTY || sq.currentState === SquareState.COLORED) {
        if (sq.currentState === this.levelData.endColor) {
          // "Empty" state = draw translucent
          drawEmptySquare(this.cm, pos.x, pos.y, size);
        } else {
          // Colored state
          const colorIndex = sq.currentState;
          const color = this.colorPair[colorIndex] || this.colorPair[0];
          drawGlossySquare(this.cm, pos.x, pos.y, size, color);
        }
      } else if (sq.currentState === SquareState.ICE_BLOCK || sq.currentState === SquareState.ICE_BLOCK_BROKEN) {
        drawIceBlock(this.cm, pos.x, pos.y, size, sq.currentState === SquareState.ICE_BLOCK_BROKEN);
      }

      // Star
      if (sq.isStar) {
        drawStar(this.cm, pos.x, pos.y, size, sq.hasTakenStar);
      }

      // Indicators
      if (sq.ultimo) {
        drawSquareIndicator(this.cm, pos.x, pos.y, size, 'ultimo');
      } else if (sq.penultimo) {
        drawSquareIndicator(this.cm, pos.x, pos.y, size, 'penultimo');
      }
    }

    // Draw walls
    for (let i = 0; i < this.squares.length; i++) {
      const pos = getSquarePosition(i, this.layout);
      // Vertical walls (between column i and i+1)
      if (this.levelData.verticalWalls[i] === 1) {
        drawVerticalWall(this.cm, pos.x, pos.y, this.layout.size, this.layout.gap);
      }
      // Horizontal walls (between row and row below)
      if (this.levelData.horizontalWalls[i] === 1) {
        drawHorizontalWall(this.cm, pos.x, pos.y, this.layout.size, this.layout.gap);
      }
    }
  }

  handleInput(event: InputEvent) {
    if (event.type === 'down') {
      this.isPlaying = true;
    }

    if (event.type === 'move' || event.type === 'up') {
      if (!this.isPlaying) return;
      if (Date.now() - this.lastWin < 500) return;

      const z = hitTestGrid(event.x, event.y, this.layout);
      if (z < 0 || z >= this.squares.length) return;
      if (z === this.lastTouched) return;
      if (this.squares[z].currentState === SquareState.INVISIBLE) return;

      if (this.isAdjacent(z, this.lastTouched)) {
        // Check if going backward (to penultimo = undo)
        let backward = false;
        if (this.path.length >= 2 && this.path[this.path.length - 2] === z) {
          backward = true;
        }

        if (!backward) {
          // Forward move
          this.nextColor(z);
          this.lastTouched = z;
          this.path.push(z);
          this.currentMoves++;

          if (this.currentMoves > this.perfectMoves) {
            // Too many moves, reset
            this.isPlaying = false;
            this.resetBoard();
            this.config.audio.play(SoundType.ERROR);
          } else {
            this.config.audio.play(SoundType.SELECT);
          }
        } else {
          // Undo
          this.lastColor(this.lastTouched);
          this.path.pop();
          this.lastTouched = z;
          this.currentMoves--;
          this.config.audio.play(SoundType.SELECT);
        }

        // Check win
        if (this.checkWin()) {
          this.onWin();
        }

        this.updateSignals();
        this.config.onHudUpdate({ currentMoves: this.currentMoves });
      } else {
        if (Date.now() - this.lastError > 300) {
          this.config.audio.play(SoundType.BLOCK);
          this.lastError = Date.now();
        }
      }
    }

    if (event.type === 'up') {
      this.isPlaying = false;
    }
  }

  private isAdjacent(z: number, last: number): boolean {
    const cols = this.levelData.columns;
    const rows = this.levelData.rows;

    // First choice always valid
    if (last === -1) return true;

    // Can't move to broken ice
    const sq = this.squares[z];
    if (sq.currentState === SquareState.ICE_BLOCK_BROKEN && this.path.length >= 2 && this.path[this.path.length - 2] !== z) {
      return false;
    }

    // Check orthogonal adjacency
    const zRow = Math.floor(z / cols);
    const zCol = z % cols;
    const lastRow = Math.floor(last / cols);
    const lastCol = last % cols;

    const dRow = zRow - lastRow;
    const dCol = zCol - lastCol;

    // Must be exactly 1 step orthogonal
    if (Math.abs(dRow) + Math.abs(dCol) !== 1) return false;

    // Check walls
    if (dRow === -1) {
      // Moving up: check horizontal wall on the destination row
      if (this.levelData.horizontalWalls[z] === 1) return false;
    } else if (dRow === 1) {
      // Moving down: check horizontal wall on last position
      if (this.levelData.horizontalWalls[last] === 1) return false;
    } else if (dCol === 1) {
      // Moving right: check vertical wall at last position
      const wallIndex = lastRow * (cols - 1) + lastCol;
      if (wallIndex >= 0 && wallIndex < this.levelData.verticalWalls.length) {
        if (this.levelData.verticalWalls[wallIndex] === 1) return false;
      }
    } else if (dCol === -1) {
      // Moving left: check vertical wall at destination
      const wallIndex = zRow * (cols - 1) + zCol;
      if (wallIndex >= 0 && wallIndex < this.levelData.verticalWalls.length) {
        if (this.levelData.verticalWalls[wallIndex] === 1) return false;
      }
    }

    return true;
  }

  private nextColor(z: number) {
    const sq = this.squares[z];
    if (sq.currentState === SquareState.EMPTY || sq.currentState === SquareState.COLORED) {
      const numColors = this.colorPair.length;
      if (sq.currentState < numColors - 1) {
        sq.currentState++;
      } else {
        sq.currentState = SquareState.EMPTY;
      }
    } else if (sq.currentState === SquareState.ICE_BLOCK) {
      sq.currentState = SquareState.ICE_BLOCK_BROKEN;
    }
    if (sq.isStar) {
      sq.hasTakenStar = true;
    }
    sq.counter++;
  }

  private lastColor(z: number) {
    const sq = this.squares[z];
    if (sq.currentState === SquareState.EMPTY || sq.currentState === SquareState.COLORED) {
      if (sq.currentState > 0) {
        sq.currentState--;
      } else {
        sq.currentState = (this.colorPair.length - 1) as SquareState;
      }
    } else if (sq.currentState === SquareState.ICE_BLOCK_BROKEN) {
      sq.currentState = SquareState.ICE_BLOCK;
    }
    if (sq.isStar && sq.counter === 1) {
      sq.hasTakenStar = false;
    }
    sq.counter--;
  }

  private checkWin(): boolean {
    for (const sq of this.squares) {
      if ((sq.currentState === SquareState.EMPTY || sq.currentState === SquareState.COLORED) &&
          sq.currentState !== this.levelData.endColor) {
        return false;
      }
    }
    return true;
  }

  private onWin() {
    this.config.audio.play(SoundType.DING);
    this.lastWin = Date.now();

    this.currentLevel++;
    if (this.currentLevel < COMPLEMENTARY_LEVELS.length) {
      this.initLevel(this.currentLevel);
    } else {
      this.config.onGameOver(
        'Congratulations!',
        `You completed all ${COMPLEMENTARY_LEVELS.length} Complementary levels!`,
      );
    }
  }

  private updateSignals() {
    for (const sq of this.squares) {
      sq.penultimo = false;
      sq.ultimo = false;
    }
    if (this.path.length >= 2) {
      this.squares[this.path[this.path.length - 2]].penultimo = true;
    }
    if (this.path.length >= 1) {
      this.squares[this.path[this.path.length - 1]].ultimo = true;
    }
  }

  private resetBoard() {
    this.path = [];
    this.currentMoves = 0;
    this.lastTouched = -1;
    for (let i = 0; i < this.squares.length; i++) {
      const sq = this.squares[i];
      sq.currentState = sq.originalState;
      sq.penultimo = false;
      sq.ultimo = false;
      sq.hasTakenStar = false;
      sq.counter = 0;
    }
    this.config.onHudUpdate({ currentMoves: 0 });
  }

  resize(width: number, height: number) {
    this.layout = calculateGridLayout(
      width, height,
      this.levelData.columns, this.levelData.rows,
      GameType.COMPLEMENTARY,
      HUD_HEIGHT,
    );
  }

  reset() {
    this.resetBoard();
  }

  restart() {
    this.initLevel(0);
  }

  nextLevel() {
    this.currentLevel++;
    if (this.currentLevel >= COMPLEMENTARY_LEVELS.length) {
      this.currentLevel = 0;
    }
    this.initLevel(this.currentLevel);
  }
}
