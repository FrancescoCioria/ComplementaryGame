import { useEffect, useRef, useState, useCallback } from 'react';
import { GameType, Difficulty, DIFFICULTY_GRIDS, SoundType } from '../types';
import { CanvasManager } from '../engine/canvas-manager';
import { GameLoop } from '../engine/game-loop';
import { InputHandler } from '../engine/input-handler';
import { AudioManager } from '../engine/audio-manager';
import { HUD } from './HUD';
import { GameOverDialog } from './GameOverDialog';
import { GameMode, createGameMode } from '../game-modes/game-mode';

interface GameScreenProps {
  gameType: GameType;
  difficulty: Difficulty;
  customGrid: { columns: number; rows: number };
  audio: AudioManager;
  onBack: () => void;
}

export function GameScreen({ gameType, difficulty, customGrid, audio, onBack }: GameScreenProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const gameModeRef = useRef<GameMode | null>(null);
  const gameLoopRef = useRef<GameLoop | null>(null);
  const inputRef = useRef<InputHandler | null>(null);
  const [hudState, setHudState] = useState({
    lives: undefined as number | undefined,
    level: undefined as number | undefined,
    bestMoves: undefined as number | undefined,
    currentMoves: undefined as number | undefined,
    showReset: false,
    showNextLevel: false,
  });
  const [gameOver, setGameOver] = useState<{ title: string; message: string } | null>(null);

  const getGrid = useCallback(() => {
    if (difficulty === Difficulty.CUSTOM) return customGrid;
    return DIFFICULTY_GRIDS[difficulty] || DIFFICULTY_GRIDS[Difficulty.NORMAL];
  }, [difficulty, customGrid]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    audio.resume();

    const cm = new CanvasManager(canvas);
    const grid = getGrid();

    const mode = createGameMode(gameType, {
      canvasManager: cm,
      audio,
      grid,
      difficulty,
      onHudUpdate: (state) => setHudState((prev) => ({ ...prev, ...state })),
      onGameOver: (title, message) => setGameOver({ title, message }),
    });

    gameModeRef.current = mode;
    mode.init();

    const loop = new GameLoop(
      (dt) => mode.update(dt),
      () => mode.render(),
    );
    gameLoopRef.current = loop;

    const input = new InputHandler(canvas, (event) => {
      audio.resume();
      mode.handleInput(event);
    });
    inputRef.current = input;

    const onResize = () => {
      cm.resize();
      mode.resize(cm.width, cm.height);
    };
    window.addEventListener('resize', onResize);

    loop.start();

    return () => {
      loop.stop();
      input.destroy();
      window.removeEventListener('resize', onResize);
    };
  }, [gameType, difficulty, customGrid, audio, getGrid]);

  const handleReset = useCallback(() => {
    gameModeRef.current?.reset();
  }, []);

  const handleNextLevel = useCallback(() => {
    gameModeRef.current?.nextLevel();
  }, []);

  const handlePlayAgain = useCallback(() => {
    setGameOver(null);
    gameModeRef.current?.restart();
  }, []);

  return (
    <div className="game-screen">
      <HUD
        lives={hudState.lives}
        level={hudState.level}
        bestMoves={hudState.bestMoves}
        currentMoves={hudState.currentMoves}
        onReset={hudState.showReset ? handleReset : undefined}
        onNextLevel={hudState.showNextLevel ? handleNextLevel : undefined}
        onBack={onBack}
      />
      <div className="canvas-container">
        <canvas ref={canvasRef} />
      </div>
      {gameOver && (
        <GameOverDialog
          title={gameOver.title}
          message={gameOver.message}
          onPlayAgain={handlePlayAgain}
          onBackToMenu={onBack}
        />
      )}
    </div>
  );
}
