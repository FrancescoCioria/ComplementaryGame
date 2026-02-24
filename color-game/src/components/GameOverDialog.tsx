interface GameOverDialogProps {
  title: string;
  message: string;
  onPlayAgain: () => void;
  onBackToMenu: () => void;
}

export function GameOverDialog({ title, message, onPlayAgain, onBackToMenu }: GameOverDialogProps) {
  return (
    <div className="dialog-overlay">
      <div className="dialog">
        <h2>{title}</h2>
        <p className="dialog-message">{message}</p>
        <div className="dialog-buttons">
          <button className="dialog-btn dialog-btn-primary" onClick={onPlayAgain}>
            Play Again
          </button>
          <button className="dialog-btn" onClick={onBackToMenu}>
            Back to Menu
          </button>
        </div>
      </div>
    </div>
  );
}
