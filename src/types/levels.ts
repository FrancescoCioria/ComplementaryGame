import { SquareState } from './index';

export interface DiscoveryLevelData {
  columns: number;
  rows: number;
  squares: number;  // number of target squares
  viewTime: number; // time to view before flip (ms)
}

export interface ComplementaryLevelData {
  columns: number;
  rows: number;
  game: SquareState[];         // square states
  verticalWalls: number[];     // 1 = wall present
  horizontalWalls: number[];   // 1 = wall present
  star: number;                // square index for star, -1 = no star
  endColor: number;            // target color state
  squaresType: number;         // complementary color type
}

// 22 Discovery levels (from MainActivity.initializeDiscovery)
export const DISCOVERY_LEVELS: DiscoveryLevelData[] = [
  // 3x3
  { columns: 3, rows: 3, squares: 2, viewTime: 0 },
  { columns: 3, rows: 3, squares: 3, viewTime: 0 },
  { columns: 3, rows: 3, squares: 4, viewTime: 0 },
  // 3x4
  { columns: 3, rows: 4, squares: 3, viewTime: 0 },
  { columns: 3, rows: 4, squares: 4, viewTime: 0 },
  { columns: 3, rows: 4, squares: 5, viewTime: 0 },
  // 4x4
  { columns: 4, rows: 4, squares: 4, viewTime: 0 },
  { columns: 4, rows: 4, squares: 5, viewTime: 0 },
  { columns: 4, rows: 4, squares: 6, viewTime: 0 },
  // 4x5
  { columns: 4, rows: 5, squares: 5, viewTime: 0 },
  { columns: 4, rows: 5, squares: 6, viewTime: 0 },
  { columns: 4, rows: 5, squares: 7, viewTime: 0 },
  // 5x5
  { columns: 5, rows: 5, squares: 6, viewTime: 0 },
  { columns: 5, rows: 5, squares: 7, viewTime: 0 },
  { columns: 5, rows: 5, squares: 8, viewTime: 0 },
  // 5x6
  { columns: 5, rows: 6, squares: 7, viewTime: 0 },
  { columns: 5, rows: 6, squares: 8, viewTime: 0 },
  { columns: 5, rows: 6, squares: 9, viewTime: 0 },
  { columns: 5, rows: 6, squares: 10, viewTime: 0 },
  // 5x7
  { columns: 5, rows: 7, squares: 9, viewTime: 0 },
  { columns: 5, rows: 7, squares: 10, viewTime: 0 },
  { columns: 5, rows: 7, squares: 11, viewTime: 0 },
  { columns: 5, rows: 7, squares: 12, viewTime: 0 },
];

// 13 Complementary levels (from MainActivity.initializeComplementaryLevels)
export function createComplementaryLevel(
  squares: SquareState[],
  verticalWallPositions: number[],
  horizontalWallPositions: number[],
  star: number,
  rows: number,
  columns: number,
): ComplementaryLevelData {
  const vWalls = new Array(squares.length).fill(0);
  const hWalls = new Array(squares.length).fill(0);

  for (const pos of verticalWallPositions) {
    vWalls[pos] = 1;
  }
  for (const pos of horizontalWallPositions) {
    hWalls[pos] = 1;
  }

  return {
    columns,
    rows,
    game: [...squares],
    verticalWalls: vWalls,
    horizontalWalls: hWalls,
    star,
    endColor: 0,      // EMPTY state is the target
    squaresType: 0,    // will be randomized at runtime
  };
}

const E = SquareState.EMPTY;
const C = SquareState.COLORED;
const I = SquareState.ICE_BLOCK;
const V = SquareState.INVISIBLE;

export const COMPLEMENTARY_LEVELS: ComplementaryLevelData[] = [
  // TUTORIAL
  createComplementaryLevel([E,E,C, E,E,C, E,E,C], [], [], 1, 3, 3),
  createComplementaryLevel([E,C,E, E,E,C, E,C,C], [], [], 1, 3, 3),
  // NORMAL
  createComplementaryLevel([C,E,E, E,E,E, C,C,E], [], [], 1, 3, 3),
  createComplementaryLevel([E,E,C, E,E,E, E,C,E], [], [], 1, 3, 3),
  createComplementaryLevel([C,E,E, E,E,E, C,E,C], [], [], -1, 3, 3),
  createComplementaryLevel([E,C,E, E,E,E, E,C,E], [], [], -1, 3, 3),
  createComplementaryLevel([C,E,E, E,E,E, E,E,C], [], [], -1, 3, 3),
  createComplementaryLevel([E,E,E, E,C,E, C,E,C], [], [], -1, 3, 3),
  createComplementaryLevel([E,C,E, C,E,C, E,C,E], [], [], -1, 3, 3),
  // INVISIBLE (4x4)
  createComplementaryLevel(
    [C,E,E,V, E,E,E,V, V,E,C,E, V,C,E,C],
    [], [], -1, 4, 4
  ),
  // ICE_BLOCK
  createComplementaryLevel([C,E,E, I,E,E, E,C,E], [], [], -1, 3, 3),
  // WALL - vertical wall at position 5
  createComplementaryLevel([E,E,E, E,E,E, C,C,C], [5], [], -1, 3, 3),
  // WALL - horizontal wall at position 4
  createComplementaryLevel([E,C,E, E,C,E, E,C,E], [], [4], -1, 3, 3),
];
