// Main game colors (from GameView.java)
export const GAME_COLORS = {
  RED: 'rgb(190, 0, 0)',
  GREEN: 'rgb(0, 160, 0)',
  BLUE: 'rgb(11, 97, 164)',
  ORANGE: 'rgb(255, 146, 0)',
  PURPLE: 'rgb(159, 62, 213)',
  YELLOW: 'rgb(255, 200, 0)',
} as const;

// Complementary color pairs
export const COMPLEMENTARY_PAIRS: [string, string][] = [
  [GAME_COLORS.YELLOW, GAME_COLORS.PURPLE],   // YELLOW_PURPLE = 0
  [GAME_COLORS.BLUE, GAME_COLORS.ORANGE],     // BLUE_ORANGE = 1
  [GAME_COLORS.GREEN, GAME_COLORS.RED],        // GREEN_RED = 2
];

// 27-color palette for Memory/Sequence (from randomColor in GameView.java)
export const COLOR_PALETTE: string[] = [
  '#ffff00',   // 0: yellow
  '#0000ff',   // 1: blue
  '#A52A2A',   // 2: brown
  '#ff0000',   // 3: red
  '#a020f0',   // 4: purple
  '#006400',   // 5: dark_green
  '#ff1493',   // 6: fucsia
  '#009999',   // 7: verdeacqua_scuro
  '#daa520',   // 8: gold
  '#32cd32',   // 9: green
  '#cd5c5c',   // 10: indian_red
  '#4682b4',   // 11: steel_blue
  '#fffacd',   // 12: lemon
  '#d3d3d3',   // 13: light_gray
  '#00cc66',   // 14: light_green
  '#ee82ee',   // 15: violetto
  '#994C00',   // 16: marrone
  '#FFFFFF',   // 17: white
  '#00ffff',   // 18: cyan
  '#d2691e',   // 19: chocolate
  '#f0e68c',   // 20: kakhi
  '#fa8072',   // 21: salmon
  '#FFCCCC',   // 22: rosa
  '#FFFFCC',   // 23: giallo_chiaro
  '#7fffd4',   // 24: verdeacqua
  '#20B2AA',   // 25: light_seagreen
  '#ffa500',   // 26: orange
];

// Discovery mode colors
export const DISCOVERY_TARGET_COLOR = '#00cc66'; // light_green
export const DISCOVERY_WRONG_COLOR = '#ff0000';  // red
export const DISCOVERY_COVER_COLOR = '#FFCCCC';  // rosa
export const DISCOVERY_FRAME_COLOR = '#d2691e';  // chocolate
export const DISCOVERY_BG_COLOR = '#994C00';     // marrone

// Generate a random RGB color
export function randomRGB(): string {
  const r = Math.floor(Math.random() * 256);
  const g = Math.floor(Math.random() * 256);
  const b = Math.floor(Math.random() * 256);
  return `rgb(${r}, ${g}, ${b})`;
}

// Get a color from the palette by index. If index < 0, returns random color
export function getColorByIndex(index: number): string {
  if (index < 0) return randomRGB();
  if (index < COLOR_PALETTE.length) return COLOR_PALETTE[index];
  return randomRGB();
}
