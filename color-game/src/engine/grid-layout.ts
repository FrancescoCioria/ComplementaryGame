import { GameType } from '../types';

export interface GridLayout {
  size: number;          // square size in px
  gap: number;           // gap between squares
  marginX: number;       // horizontal margin
  marginY: number;       // vertical margin
  columns: number;
  rows: number;
  totalWidth: number;
  totalHeight: number;
}

export function calculateGridLayout(
  canvasWidth: number,
  canvasHeight: number,
  columns: number,
  rows: number,
  gameType: GameType,
  topOffset: number = 0,  // for complementary HUD
): GridLayout {
  let size = 100;
  let gap: number;
  let marginX: number;

  if (gameType === GameType.DISCOVERY || gameType === GameType.COMPLEMENTARY) {
    gap = 20;
    marginX = 20;
  } else {
    gap = 5;
    marginX = 5;
  }

  const availableHeight = canvasHeight - topOffset;

  // Shrink if too big
  let toobig = false;
  while (
    (canvasWidth - (size * columns + gap * (columns - 1))) < 2 * marginX ||
    (availableHeight - (size * rows + gap * (rows - 1))) < 2 * marginX
  ) {
    toobig = true;
    if (size <= gap * 4) {
      gap--;
    } else {
      size -= 2;
    }
    if (size <= 10) break;
  }

  // Grow if too small
  while (
    !toobig &&
    (canvasWidth - (size * columns + gap * (columns - 1))) >= 2 * marginX &&
    (availableHeight - (size * rows + gap * (rows - 1))) >= 2 * marginX
  ) {
    size++;
  }
  if (!toobig) size--; // went one too far

  // Cap for Discovery/Complementary
  if (
    (gameType === GameType.DISCOVERY || gameType === GameType.COMPLEMENTARY) &&
    size > canvasWidth / 5
  ) {
    size = Math.floor(canvasWidth / 5);
  }

  // Center the grid
  marginX = Math.floor((canvasWidth - (size * columns + gap * (columns - 1))) / 2);
  const marginY = Math.floor((availableHeight - (size * rows + gap * (rows - 1))) / 2) + topOffset;

  return {
    size,
    gap,
    marginX,
    marginY,
    columns,
    rows,
    totalWidth: size * columns + gap * (columns - 1),
    totalHeight: size * rows + gap * (rows - 1),
  };
}

// Get x,y position for a square at grid index
export function getSquarePosition(
  index: number,
  layout: GridLayout,
): { x: number; y: number } {
  const col = index % layout.columns;
  const row = Math.floor(index / layout.columns);
  return {
    x: layout.marginX + col * (layout.size + layout.gap),
    y: layout.marginY + row * (layout.size + layout.gap),
  };
}

// Find which square index was hit, or -1
export function hitTestGrid(
  px: number,
  py: number,
  layout: GridLayout,
): number {
  const totalSquares = layout.columns * layout.rows;
  for (let i = 0; i < totalSquares; i++) {
    const pos = getSquarePosition(i, layout);
    if (
      px >= pos.x && px <= pos.x + layout.size &&
      py >= pos.y && py <= pos.y + layout.size
    ) {
      return i;
    }
  }
  return -1;
}
