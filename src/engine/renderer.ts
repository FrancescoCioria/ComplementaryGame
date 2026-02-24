import { CanvasManager } from './canvas-manager';
import { GAME_COLORS, COMPLEMENTARY_PAIRS } from '../types/colors';
import { SquareState } from '../types';

// Helpers
function roundRect(
  ctx: CanvasRenderingContext2D,
  x: number, y: number, w: number, h: number, r: number,
) {
  r = Math.min(r, w / 2, h / 2);
  ctx.beginPath();
  ctx.moveTo(x + r, y);
  ctx.arcTo(x + w, y, x + w, y + h, r);
  ctx.arcTo(x + w, y + h, x, y + h, r);
  ctx.arcTo(x, y + h, x, y, r);
  ctx.arcTo(x, y, x + w, y, r);
  ctx.closePath();
}

function parseColor(color: string): [number, number, number] {
  const m = color.match(/rgb\((\d+),\s*(\d+),\s*(\d+)\)/);
  if (m) return [+m[1], +m[2], +m[3]];
  // hex
  const h = color.replace('#', '');
  if (h.length === 6) {
    return [parseInt(h.slice(0, 2), 16), parseInt(h.slice(2, 4), 16), parseInt(h.slice(4, 6), 16)];
  }
  return [128, 128, 128];
}

function darken(r: number, g: number, b: number, amt: number): string {
  return `rgb(${Math.max(0, r - amt)}, ${Math.max(0, g - amt)}, ${Math.max(0, b - amt)})`;
}

function lighten(r: number, g: number, b: number, amt: number): string {
  return `rgb(${Math.min(255, r + amt)}, ${Math.min(255, g + amt)}, ${Math.min(255, b + amt)})`;
}

// Draw a glossy colored square with rounded corners and 3D effect
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

  const radius = size * 0.12;
  const [cr, cg, cb] = parseColor(color);

  // Drop shadow
  ctx.shadowColor = 'rgba(0, 0, 0, 0.35)';
  ctx.shadowBlur = size * 0.08;
  ctx.shadowOffsetX = size * 0.02;
  ctx.shadowOffsetY = size * 0.04;

  // Base fill
  roundRect(ctx, x, y, size, size, radius);
  ctx.fillStyle = color;
  ctx.fill();

  // Reset shadow for overlays
  ctx.shadowColor = 'transparent';
  ctx.shadowBlur = 0;
  ctx.shadowOffsetX = 0;
  ctx.shadowOffsetY = 0;

  // Top highlight gradient (glossy dome)
  roundRect(ctx, x, y, size, size, radius);
  ctx.clip();
  const glossGrad = ctx.createLinearGradient(x, y, x, y + size);
  glossGrad.addColorStop(0, 'rgba(255, 255, 255, 0.38)');
  glossGrad.addColorStop(0.35, 'rgba(255, 255, 255, 0.08)');
  glossGrad.addColorStop(0.5, 'rgba(0, 0, 0, 0.0)');
  glossGrad.addColorStop(1, 'rgba(0, 0, 0, 0.18)');
  ctx.fillStyle = glossGrad;
  ctx.fillRect(x, y, size, size);

  // Radial highlight (top-left shine)
  const shineGrad = ctx.createRadialGradient(
    x + size * 0.3, y + size * 0.25, size * 0.05,
    x + size * 0.3, y + size * 0.25, size * 0.55,
  );
  shineGrad.addColorStop(0, 'rgba(255, 255, 255, 0.2)');
  shineGrad.addColorStop(1, 'rgba(255, 255, 255, 0)');
  ctx.fillStyle = shineGrad;
  ctx.fillRect(x, y, size, size);

  ctx.restore();

  // Border: light top-left, dark bottom-right for 3D bevel
  ctx.save();
  ctx.globalAlpha = alpha;
  const inset = 0.5;
  roundRect(ctx, x + inset, y + inset, size - inset * 2, size - inset * 2, radius);
  ctx.strokeStyle = lighten(cr, cg, cb, 40);
  ctx.lineWidth = 1.5;
  ctx.stroke();

  roundRect(ctx, x + inset, y + inset, size - inset * 2, size - inset * 2, radius);
  // Bottom-right shadow edge
  const borderGrad = ctx.createLinearGradient(x, y, x + size, y + size);
  borderGrad.addColorStop(0, 'rgba(255, 255, 255, 0.25)');
  borderGrad.addColorStop(0.5, 'rgba(255, 255, 255, 0)');
  borderGrad.addColorStop(0.5, 'rgba(0, 0, 0, 0)');
  borderGrad.addColorStop(1, 'rgba(0, 0, 0, 0.3)');
  ctx.strokeStyle = borderGrad;
  ctx.lineWidth = 1.5;
  ctx.stroke();
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
  const radius = size * 0.12;

  // Frame
  ctx.save();
  roundRect(ctx, x - 2 + flip, y - 2, size + 4 - flip * 2, size + 4, radius + 2);
  ctx.fillStyle = frameColor;
  ctx.fill();
  ctx.restore();

  // Color underneath
  if (flip < size / 2) {
    drawGlossySquare(cm, x + flip, y, size - flip * 2, color);
  }

  // Cover
  ctx.save();
  roundRect(ctx, x + flip, y, size - flip * 2, size, radius);
  ctx.fillStyle = coverColor;
  ctx.globalAlpha = coverAlpha / 255;
  ctx.fill();
  ctx.globalAlpha = 1;
  ctx.restore();
}

// Draw empty square (completed/target-state tile)
export function drawEmptySquare(
  cm: CanvasManager,
  x: number,
  y: number,
  size: number,
  bgColor?: string,
) {
  const ctx = cm.ctx;
  ctx.save();

  const radius = size * 0.12;

  // Inset shadow to look "pressed in"
  ctx.shadowColor = 'rgba(0, 0, 0, 0.3)';
  ctx.shadowBlur = size * 0.04;
  ctx.shadowOffsetX = 0;
  ctx.shadowOffsetY = size * 0.02;

  // Fill with darkened version of background color
  roundRect(ctx, x, y, size, size, radius);
  if (bgColor) {
    const [r, g, b] = parseColor(bgColor);
    ctx.fillStyle = darken(r, g, b, 50);
    ctx.globalAlpha = 0.45;
  } else {
    ctx.fillStyle = '#444444';
    ctx.globalAlpha = 0.3;
  }
  ctx.fill();

  ctx.shadowColor = 'transparent';
  ctx.shadowBlur = 0;
  ctx.shadowOffsetX = 0;
  ctx.shadowOffsetY = 0;

  // Subtle inner gradient (darker on top = inset feel)
  roundRect(ctx, x, y, size, size, radius);
  ctx.clip();
  ctx.globalAlpha = 0.2;
  const grad = ctx.createLinearGradient(x, y, x, y + size);
  grad.addColorStop(0, 'rgba(0, 0, 0, 0.3)');
  grad.addColorStop(0.4, 'rgba(0, 0, 0, 0)');
  grad.addColorStop(1, 'rgba(255, 255, 255, 0.1)');
  ctx.fillStyle = grad;
  ctx.fillRect(x, y, size, size);

  ctx.restore();

  // Thin solid border
  ctx.save();
  ctx.globalAlpha = 0.18;
  roundRect(ctx, x + 1, y + 1, size - 2, size - 2, radius);
  ctx.strokeStyle = 'rgba(255, 255, 255, 0.5)';
  ctx.lineWidth = 1;
  ctx.stroke();
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

  const cx = x + size / 2;
  const cy = y + size / 2;
  const outerR = size * 0.35;
  const innerR = size * 0.15;
  const spikes = 5;

  // Star path helper
  function starPath() {
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
  }

  if (filled) {
    // Glow
    ctx.shadowColor = 'rgba(255, 215, 0, 0.6)';
    ctx.shadowBlur = size * 0.2;
    starPath();
    const starGrad = ctx.createRadialGradient(cx, cy - outerR * 0.3, 0, cx, cy, outerR);
    starGrad.addColorStop(0, '#FFF7A0');
    starGrad.addColorStop(0.5, '#FFD700');
    starGrad.addColorStop(1, '#E5A800');
    ctx.fillStyle = starGrad;
    ctx.fill();

    ctx.shadowColor = 'transparent';
    ctx.shadowBlur = 0;

    // Outline
    starPath();
    ctx.strokeStyle = '#CC8800';
    ctx.lineWidth = 1.5;
    ctx.stroke();

    // Inner shine
    ctx.globalAlpha = 0.4;
    starPath();
    ctx.clip();
    const shineGrad = ctx.createLinearGradient(cx, cy - outerR, cx, cy);
    shineGrad.addColorStop(0, 'rgba(255, 255, 255, 0.6)');
    shineGrad.addColorStop(1, 'rgba(255, 255, 255, 0)');
    ctx.fillStyle = shineGrad;
    ctx.fillRect(x, y, size, size);
  } else {
    // Unfilled: ghost star
    ctx.globalAlpha = 0.25;
    starPath();
    ctx.fillStyle = '#888888';
    ctx.fill();
    starPath();
    ctx.strokeStyle = '#666666';
    ctx.lineWidth = 1;
    ctx.stroke();
  }

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

  const radius = size * 0.12;

  if (!broken) {
    // Solid ice — glassy look
    ctx.shadowColor = 'rgba(100, 180, 220, 0.3)';
    ctx.shadowBlur = size * 0.06;
    roundRect(ctx, x, y, size, size, radius);
    const iceGrad = ctx.createLinearGradient(x, y, x + size, y + size);
    iceGrad.addColorStop(0, 'rgba(210, 240, 255, 0.92)');
    iceGrad.addColorStop(0.5, 'rgba(170, 215, 240, 0.85)');
    iceGrad.addColorStop(1, 'rgba(140, 195, 225, 0.8)');
    ctx.fillStyle = iceGrad;
    ctx.fill();

    ctx.shadowColor = 'transparent';
    ctx.shadowBlur = 0;

    // Glossy highlight
    roundRect(ctx, x, y, size, size, radius);
    ctx.clip();
    const shineGrad = ctx.createLinearGradient(x, y, x, y + size * 0.5);
    shineGrad.addColorStop(0, 'rgba(255, 255, 255, 0.45)');
    shineGrad.addColorStop(1, 'rgba(255, 255, 255, 0)');
    ctx.fillStyle = shineGrad;
    ctx.fillRect(x, y, size, size * 0.5);

    ctx.restore();
    ctx.save();

    // Small sparkle rectangles
    ctx.globalAlpha = 0.5;
    ctx.fillStyle = '#ffffff';
    const s1 = size * 0.06;
    ctx.fillRect(x + size * 0.18, y + size * 0.15, s1 * 2.5, s1);
    ctx.fillRect(x + size * 0.15, y + size * 0.22, s1, s1 * 1.5);

    // Border
    ctx.globalAlpha = 0.4;
    roundRect(ctx, x + 0.5, y + 0.5, size - 1, size - 1, radius);
    ctx.strokeStyle = 'rgba(200, 230, 255, 0.6)';
    ctx.lineWidth = 1.5;
    ctx.stroke();
  } else {
    // Broken ice
    ctx.globalAlpha = 0.45;
    roundRect(ctx, x, y, size, size, radius);
    const brokenGrad = ctx.createLinearGradient(x, y, x + size, y + size);
    brokenGrad.addColorStop(0, 'rgba(180, 215, 235, 0.7)');
    brokenGrad.addColorStop(1, 'rgba(140, 180, 210, 0.5)');
    ctx.fillStyle = brokenGrad;
    ctx.fill();

    // Crack lines
    ctx.globalAlpha = 0.7;
    ctx.strokeStyle = 'rgba(80, 140, 170, 0.8)';
    ctx.lineWidth = 1.5;
    ctx.lineCap = 'round';

    ctx.beginPath();
    ctx.moveTo(x + size * 0.25, y + size * 0.05);
    ctx.lineTo(x + size * 0.4, y + size * 0.35);
    ctx.lineTo(x + size * 0.55, y + size * 0.5);
    ctx.lineTo(x + size * 0.75, y + size * 0.95);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(x + size * 0.05, y + size * 0.45);
    ctx.lineTo(x + size * 0.4, y + size * 0.35);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(x + size * 0.55, y + size * 0.5);
    ctx.lineTo(x + size * 0.95, y + size * 0.55);
    ctx.stroke();

    // Faded border
    ctx.globalAlpha = 0.2;
    roundRect(ctx, x + 0.5, y + 0.5, size - 1, size - 1, radius);
    ctx.strokeStyle = 'rgba(150, 200, 220, 0.5)';
    ctx.lineWidth = 1;
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
  ctx.save();

  const wallWidth = Math.max(gap * 0.6, 4);
  const inset = size * 0.05;
  const wx = x + size + (gap - wallWidth) / 2;
  const wy = y + inset;
  const wh = size - inset * 2;
  const r = wallWidth * 0.4;

  // Shadow
  ctx.shadowColor = 'rgba(0, 0, 0, 0.4)';
  ctx.shadowBlur = 3;
  ctx.shadowOffsetX = 1;
  ctx.shadowOffsetY = 1;

  roundRect(ctx, wx, wy, wallWidth, wh, r);
  const wallGrad = ctx.createLinearGradient(wx, wy, wx + wallWidth, wy);
  wallGrad.addColorStop(0, '#555555');
  wallGrad.addColorStop(0.4, '#3a3a3a');
  wallGrad.addColorStop(1, '#222222');
  ctx.fillStyle = wallGrad;
  ctx.fill();

  ctx.restore();
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
  ctx.save();

  const wallHeight = Math.max(gap * 0.6, 4);
  const inset = size * 0.05;
  const wx = x + inset;
  const wy = y + size + (gap - wallHeight) / 2;
  const ww = size - inset * 2;
  const r = wallHeight * 0.4;

  ctx.shadowColor = 'rgba(0, 0, 0, 0.4)';
  ctx.shadowBlur = 3;
  ctx.shadowOffsetX = 1;
  ctx.shadowOffsetY = 1;

  roundRect(ctx, wx, wy, ww, wallHeight, r);
  const wallGrad = ctx.createLinearGradient(wx, wy, wx, wy + wallHeight);
  wallGrad.addColorStop(0, '#555555');
  wallGrad.addColorStop(0.4, '#3a3a3a');
  wallGrad.addColorStop(1, '#222222');
  ctx.fillStyle = wallGrad;
  ctx.fill();

  ctx.restore();
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

  const radius = size * 0.12;
  const inset = type === 'ultimo' ? size * 0.15 : size * 0.22;
  const iSize = size - inset * 2;
  const iRadius = radius * 0.6;

  // Pulsing white rounded rect
  ctx.globalAlpha = type === 'ultimo' ? 0.45 : 0.25;
  roundRect(ctx, x + inset, y + inset, iSize, iSize, iRadius);
  const grad = ctx.createRadialGradient(
    x + size / 2, y + size / 2, 0,
    x + size / 2, y + size / 2, iSize * 0.7,
  );
  grad.addColorStop(0, 'rgba(255, 255, 255, 0.8)');
  grad.addColorStop(1, 'rgba(255, 255, 255, 0.1)');
  ctx.fillStyle = grad;
  ctx.fill();

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
  const radius = 6;
  roundRect(ctx, x, y, width, height, radius);
  ctx.fillStyle = color;
  ctx.fill();
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
