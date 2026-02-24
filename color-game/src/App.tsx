import { useState, useRef, useCallback } from 'react';
import { GameType, Difficulty } from './types';
import { AudioManager } from './engine/audio-manager';
import { MenuScreen } from './components/MenuScreen';
import { GameScreen } from './components/GameScreen';

type Screen = 'menu' | 'game';

export function App() {
  const [screen, setScreen] = useState<Screen>('menu');
  const audioRef = useRef(new AudioManager());

  const handleStart = useCallback(() => {
    setScreen('game');
  }, []);

  const handleBackToMenu = useCallback(() => {
    setScreen('menu');
  }, []);

  return (
    <div className="app">
      {screen === 'menu' && (
        <MenuScreen onStart={handleStart} />
      )}
      {screen === 'game' && (
        <GameScreen
          gameType={GameType.COMPLEMENTARY}
          difficulty={Difficulty.NORMAL}
          customGrid={{ columns: 3, rows: 3 }}
          audio={audioRef.current}
          onBack={handleBackToMenu}
        />
      )}
    </div>
  );
}
