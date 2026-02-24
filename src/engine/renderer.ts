import { CanvasManager } from './canvas-manager';
import { GAME_COLORS, COMPLEMENTARY_PAIRS } from '../types/colors';
import { SquareState } from '../types';

// Draw a glossy colored square with gradient
export function drawGlossySquare(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  color: string,
  alpha: number = 1,
) {
  const ctx = cm.ctx;
  ctx.save();
  ctx.globalAlpha = alpha;

  // Base color
  ctx.fillStyle = color;
  ctx.fillRect(x, y, size, size);

  // Glossy highlight (top half gradient)
  const grad = ctx.createLinearGradient(x, y, x, y + size);
  grad.addColorStop(0, 'rgba(255, 255, 255, 0.35)');
  grad.addColorStop(0.5, 'rgba(255, 255, 255, 0.05)');
  grad.addColorStop(0.5, 'rgba(0, 0, 0, 0.05)');
  grad.addColorStop(1, 'rgba(0, 0, 0, 0.15)');
  ctx.fillStyle = grad;
  ctx.fillRect(x, y, size, size);

  ctx.restore();
}

// Draw a square with a cover (for Sequence/Memory/Discovery)
export function drawCoveredSquare(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  color: string,
  coverColor: string,
  coverAlpha: number,
  frameColor: string = '#FFFFFF',
  flip: number = 0,
) {
  const ctx = cm.ctx;

  // Frame
  ctx.fillStyle = frameColor;
  ctx.fillRect(x - 2 + flip, y - 2, size + 4 - flip * 2, size + 4);

  // Color underneath
  if (flip < size / 2) {
    drawGlossySquare(cm, x + flip, y, size - flip * 2, color);
  }

  // Cover
  ctx.fillStyle = coverColor;
  ctx.globalAlpha = coverAlpha / 255;
  ctx.fillRect(x + flip, y, size - flip * 2, size);
  ctx.globalAlpha = 1;
}

// Draw empty square (translucent)
export function drawEmptySquare(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
) {
  const ctx = cm.ctx;
  ctx.save();
  ctx.globalAlpha = 0.3;
  ctx.fillStyle = '#888888';
  ctx.fillRect(x, y, size, size);
  // Glossy
  const grad = ctx.createLinearGradient(x, y, x, y + size);
  grad.addColorStop(0, 'rgba(255, 255, 255, 0.3)');
  grad.addColorStop(0.5, 'rgba(255, 255, 255, 0)');
  grad.addColorStop(1, 'rgba(0, 0, 0, 0.2)');
  ctx.fillStyle = grad;
  ctx.fillRect(x, y, size, size);
  ctx.restore();
}

// Draw star
export function drawStar(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  filled: boolean,
) {
  const ctx = cm.ctx;
  ctx.save();
  ctx.globalAlpha = filled ? 0.7 : 0.4;

  const cx = x + size / 2;
  const cy = y + size / 2;
  const outerR = size * 0.4;
  const innerR = size * 0.18;
  const spikes = 5;

  ctx.beginPath();
  for (let i = 0; i < spikes * 2; i++) {
    const r = i % 2 === 0 ? outerR : innerR;
    const angle = (Math.PI * i) / spikes - Math.PI / 2;
    const sx = cx + Math.cos(angle) * r;
    const sy = cy + Math.sin(angle) * r;
    if (i === 0) ctx.moveTo(sx, sy);
    else ctx.lineTo(sx, sy);
  }
  ctx.closePath();

  ctx.fillStyle = filled ? '#FFD700' : '#666666';
  ctx.fill();
  ctx.strokeStyle = filled ? '#FFA500' : '#444444';
  ctx.lineWidth = 2;
  ctx.stroke();

  ctx.restore();
}

// Draw ice block
export function drawIceBlock(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  broken: boolean,
) {
  const ctx = cm.ctx;
  ctx.save();

  // Ice background
  const grad = ctx.createLinearGradient(x, y, x + size, y + size);
  grad.addColorStop(0, broken ? 'rgba(180, 220, 240, 0.6)' : 'rgba(200, 235, 255, 0.9)');
  grad.addColorStop(1, broken ? 'rgba(140, 180, 210, 0.4)' : 'rgba(160, 210, 240, 0.8)');
  ctx.fillStyle = grad;
  ctx.fillRect(x, y, size, size);

  // Shine
  ctx.fillStyle = 'rgba(255, 255, 255, 0.4)';
  ctx.fillRect(x + size * 0.1, y + size * 0.1, size * 0.3, size * 0.15);

  if (broken) {
    // Crack lines
    ctx.strokeStyle = 'rgba(100, 150, 180, 0.8)';
    ctx.lineWidth = 2;
    ctx.beginPath();
    ctx.moveTo(x + size * 0.3, y);
    ctx.lineTo(x + size * 0.5, y + size * 0.5);
    ctx.lineTo(x + size * 0.7, y + size);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(x, y + size * 0.4);
    ctx.lineTo(x + size * 0.5, y + size * 0.5);
    ctx.lineTo(x + size, y + size * 0.6);
    ctx.stroke();
  }

  ctx.restore();
}

// Draw vertical wall
export function drawVerticalWall(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  gap: number,
) {
  const ctx = cm.ctx;
  const wallWidth = Math.max(gap * 0.8, 6);
  const inset = size * 0.07;
  ctx.fillStyle = '#000000';
  ctx.fillRect(
    x + size + (gap - wallWidth) / 2,
    y + inset,
    wallWidth,
    size - inset * 2,
  );
}

// Draw horizontal wall
export function drawHorizontalWall(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  gap: number,
) {
  const ctx = cm.ctx;
  const wallHeight = Math.max(gap * 0.8, 6);
  const inset = size * 0.07;
  ctx.fillStyle = '#000000';
  ctx.fillRect(
    x + inset,
    y + size + (gap - wallHeight) / 2,
    size - inset * 2,
    wallHeight,
  );
}

// Draw ultimo/penultimo indicator
export function drawSquareIndicator(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  type: 'ultimo' | 'penultimo',
) {
  const ctx = cm.ctx;
  ctx.save();
  ctx.globalAlpha = 0.5;

  const gap = type === 'ultimo' ? Math.floor(size / 3) : Math.floor(2 * size / 5);
  const indicatorSize = size - gap * 2;

  ctx.fillStyle = 'rgba(255, 255, 255, 0.7)';
  const grad = ctx.createLinearGradient(x + gap, y + gap, x + gap + indicatorSize, y + gap + indicatorSize);
  grad.addColorStop(0, 'rgba(255, 255, 255, 0.6)');
  grad.addColorStop(1, 'rgba(255, 255, 255, 0.2)');
  ctx.fillStyle = grad;
  ctx.fillRect(x + gap, y + gap, indicatorSize, indicatorSize);

  ctx.restore();
}

// Draw text
export function drawText(
  cm: CanvasManager,
  text: string,
  x: number,
  y: number,
  color: string = '#000000',
  fontSize: number = 20,
  align: CanvasTextAlign = 'left',
) {
  const ctx = cm.ctx;
  ctx.fillStyle = color;
  ctx.font = `bold ${fontSize}px sans-serif`;
  ctx.textAlign = align;
  ctx.textBaseline = 'middle';
  ctx.fillText(text, x, y);
}

// Draw a button rectangle
export function drawButton(
  cm: CanvasManager,
  x: number,
  y: number,
  width: number,
  height: number,
  color: string,
  text: string,
  textColor: string = '#FFFFFF',
) {
  const ctx = cm.ctx;
  ctx.fillStyle = color;
  ctx.fillRect(x, y, width, height);
  drawText(cm, text, x + width / 2, y + height / 2, textColor, 14, 'center');
}

// Get complementary colors array for a type
export function getComplementaryColors(type: number): string[] {
  if (type < COMPLEMENTARY_PAIRS.length) {
    return [...COMPLEMENTARY_PAIRS[type]];
  }
  // Triple colors (types 3-5) — all use yellow/orange/red
  return [GAME_COLORS.YELLOW, GAME_COLORS.ORANGE, GAME_COLORS.RED];
}
