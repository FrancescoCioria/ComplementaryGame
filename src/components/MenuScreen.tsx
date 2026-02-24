interface MenuScreenProps {
  onStart: () => void;
}

export function MenuScreen({ onStart }: MenuScreenProps) {
  return (
    <div className="menu-screen">
      <h1 className="menu-title">Complementary</h1>
      <div className="menu-buttons">
        <button
          className="menu-btn menu-btn-complementary"
          onClick={onStart}
        >
          Play
        </button>
      </div>
    </div>
  );
}
