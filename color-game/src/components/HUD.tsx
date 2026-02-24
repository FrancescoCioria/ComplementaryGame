interface HUDProps {
  lives?: number;
  level?: number;
  bestMoves?: number;
  currentMoves?: number;
  onReset?: () => void;
  onNextLevel?: () => void;
  onBack: () => void;
}

export function HUD({ lives, level, bestMoves, currentMoves, onReset, onNextLevel, onBack }: HUDProps) {
  return (
    <div className="hud">
      <div className="hud-left">
        <button className="hud-btn" onClick={onBack}>&#x2190;</button>
        {onReset && (
          <button className="hud-btn" onClick={onReset}>Reset</button>
        )}
      </div>
      <div className="hud-center">
        {lives !== undefined && (
          <span className="hud-info">Lives: {lives}</span>
        )}
        {level !== undefined && (
          <span className="hud-info">Level: {level}</span>
        )}
      </div>
      <div className="hud-right">
        {bestMoves !== undefined && (
          <span className="hud-info">Best: {bestMoves}</span>
        )}
        {currentMoves !== undefined && (
          <span className="hud-info">Done: {currentMoves}</span>
        )}
        {onNextLevel && (
          <button className="hud-btn" onClick={onNextLevel}>Next</button>
        )}
      </div>
    </div>
  );
}
