# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ComplementaryGame — an educational game with 4 modes teaching color recognition and memory skills. TypeScript + React + Canvas web app, deployed on Cloudflare Pages.

### Tech Stack
- **Vite** + **React 19** + **TypeScript**
- **HTML5 Canvas** for gameplay rendering (inside React component)
- **Web Audio API** for sounds
- **Cloudflare Pages** for deploy

### Build & Dev Commands
```bash
npm run dev          # Vite dev server
npm run build        # Production build (tsc + vite build)
npm run preview      # Preview production build locally
npm run deploy       # Build + deploy to Cloudflare Pages
```

### Architecture

**React layer** handles menu, dialogs, HUD overlay. **Canvas** handles all in-game rendering.

```
src/
├── main.tsx                          # Entry point
├── App.tsx                           # Router: menu ↔ difficulty ↔ game
├── types/
│   ├── index.ts                      # Enums: GameType, Difficulty, SquareState, SoundType
│   ├── colors.ts                     # 27-color palette, complementary pairs, helpers
│   └── levels.ts                     # Discovery (22) + Complementary (13) level data
├── engine/
│   ├── canvas-manager.ts             # HiDPI canvas setup + resize
│   ├── game-loop.ts                  # requestAnimationFrame with delta time
│   ├── input-handler.ts              # Unified touch/mouse → InputEvent
│   ├── audio-manager.ts              # Web Audio API, 5 sounds
│   ├── grid-layout.ts                # Grid sizing algorithm
│   └── renderer.ts                   # Draw functions: glossy squares, stars, ice, walls
├── components/
│   ├── MenuScreen.tsx                # 4-button main menu
│   ├── GameScreen.tsx                # Canvas + GameLoop + InputHandler + HUD wrapper
│   ├── DifficultyDialog.tsx          # Beginner/Normal/Hard/Custom
│   ├── GameOverDialog.tsx            # End-game overlay
│   └── HUD.tsx                       # Lives, level, moves, reset/next buttons
└── game-modes/
    ├── game-mode.ts                  # GameMode interface + factory
    ├── sequence/sequence-mode.ts     # Simon-says pattern game
    ├── discovery/discovery-mode.ts   # Find-the-target card flip game
    ├── memory/memory-mode.ts         # Match pairs (3-stage progression)
    └── complementary/complementary-mode.ts  # Color-pair path puzzle
```

### Game Modes

| Mode | Description | Grid |
|------|-------------|------|
| **Sequence** | Repeat growing color sequences (Simon Says) | Beginner 2×3, Normal 4×6, Hard 6×9, Custom |
| **Discovery** | Find target squares among decoys, 20 lives | 22 levels: 3×3 → 5×7 |
| **Memory** | Match card pairs, 3-stage progression | 2×3 → 4×6 → 6×9 auto |
| **Complementary** | Drag path to cycle complementary colors to target | 13 predefined levels with walls, ice, stars |

### Key Conventions
- All game logic lives in `game-modes/` — each mode implements the `GameMode` interface
- Colors defined in `types/colors.ts` — 6 game colors, 3 complementary pairs, 27-color palette
- Level data in `types/levels.ts`
- Grid sizing in `engine/grid-layout.ts`
- Audio files in `public/assets/audio/`
- No external game framework (Phaser/PixiJS) — pure Canvas 2D
