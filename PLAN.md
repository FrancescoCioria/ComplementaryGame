# Piano di Riscrittura: ComplementaryGame → TypeScript Web App

## Contesto

Il progetto Android Java (Eclipse ADT, 2013) viene riscritto come web app moderna TypeScript. 4 modalità di gioco: Sequence, Discovery, Memory, Complementary. **React** per UI/menu, **Canvas** per il gameplay, asset grafici generati programmaticamente, deploy su Cloudflare Pages.

## Tech Stack

- **Vite** + **React** + **TypeScript**
- **HTML5 Canvas** per rendering gioco (dentro un componente React)
- **Web Audio API** per suoni
- **Cloudflare Pages** per deploy (`wrangler pages deploy dist/`)
- Niente game framework (Phaser/PixiJS) — il gioco è semplice (rettangoli + gradienti)

## Struttura Progetto

```
color-game/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── wrangler.toml                      # Cloudflare Pages config
├── public/
│   └── assets/audio/                  # 5 file audio copiati da res/raw/
│       ├── ding.wav                   # (era newmail.wav)
│       ├── gameover.wav
│       ├── error.mp3
│       ├── select.mp3
│       └── block.mp3
├── src/
│   ├── main.tsx                       # React entry point
│   ├── App.tsx                        # Router: Menu ↔ Game
│   ├── types/
│   │   ├── index.ts                   # GameType, Difficulty, SquareState, enums
│   │   ├── colors.ts                  # Palette 27 colori, complementary pairs
│   │   └── levels.ts                  # Tipi per level data
│   ├── engine/
│   │   ├── game-loop.ts               # requestAnimationFrame loop
│   │   ├── canvas-manager.ts          # Setup canvas, DPR, resize
│   │   ├── input-handler.ts           # Touch/mouse unificato → InputEvent
│   │   ├── audio-manager.ts           # Web Audio API, 5 suoni
│   │   ├── grid-layout.ts            # Calcolo dimensioni griglia (port da GameView)
│   │   └── renderer.ts               # Funzioni draw: quadrati, gradienti, stelle, muri
│   ├── components/
│   │   ├── MenuScreen.tsx             # Schermata principale con 4 bottoni
│   │   ├── GameScreen.tsx             # Wrapper: canvas + HUD + dialoghi
│   │   ├── DifficultyDialog.tsx       # Selezione difficoltà
│   │   ├── GameOverDialog.tsx         # Fine partita con statistiche
│   │   └── HUD.tsx                    # Vite, mosse, livello (overlay HTML)
│   ├── game-modes/
│   │   ├── game-mode.ts               # Interfaccia GameMode
│   │   ├── sequence/
│   │   │   ├── sequence-mode.ts
│   │   │   └── sequence-square.ts
│   │   ├── discovery/
│   │   │   ├── discovery-mode.ts
│   │   │   ├── discovery-square.ts
│   │   │   └── discovery-levels.ts    # 22 livelli predefiniti
│   │   ├── memory/
│   │   │   ├── memory-mode.ts
│   │   │   └── memory-square.ts
│   │   └── complementary/
│   │       ├── complementary-mode.ts
│   │       ├── complementary-square.ts
│   │       ├── complementary-levels.ts # 13 livelli predefiniti
│   │       ├── wall.ts
│   │       └── solver.ts              # Port di everyWay() per mosse minime
│   └── styles/
│       └── index.css                  # Stili globali, mobile-first
```

## Fasi di Implementazione

### Fase 1: Scaffold progetto + menu React — `[ ]`
- Vite + React + TS setup
- `wrangler.toml` per Cloudflare Pages
- `App.tsx` con routing stato (menu/game)
- `MenuScreen.tsx` con 4 bottoni (Sequence, Discovery, Memory, Complementary)
- `DifficultyDialog.tsx` (Beginner/Normal/Hard/Custom)
- CSS mobile-first (fullscreen, no scroll, portrait-optimized)
- **Verifica**: app gira in dev, menu visibile, bottoni cliccabili

### Fase 2: Engine core + canvas — `[ ]`
- `canvas-manager.ts`: setup canvas, DPR, resize listener
- `game-loop.ts`: requestAnimationFrame con delta time
- `input-handler.ts`: touch/mouse → InputEvent (down/move/up)
- `audio-manager.ts`: preload 5 suoni, play con Web Audio API
- `grid-layout.ts`: port dell'algoritmo di sizing da GameView.java
- `renderer.ts`: draw glossy square (gradient), frame, star, ice block
- `types/colors.ts`: palette 27 colori + 3 coppie complementari
- `GameScreen.tsx`: componente React che monta il canvas e il game loop
- Copiare i 5 file audio da `res/raw/` a `public/assets/audio/`
- **Verifica**: canvas renderizza una griglia di quadrati colorati, suoni funzionano al tap

### Fase 3: Sequence Mode (gioco più semplice) — `[ ]`
- `sequence-mode.ts` + `sequence-square.ts`
- Logica: tap per iniziare → sequenza cresce → waterfall animation → ripeti → game over
- Animazioni: alpha fade per waterfall, highlight al tap
- `GameOverDialog.tsx` con score
- Grids: Beginner 2×3, Normal 4×6, Hard 6×9, Custom
- **Verifica**: partita completa giocabile con tutte le difficoltà

### Fase 4: Memory Mode — `[ ]`
- `memory-mode.ts` + `memory-square.ts`
- Logica: flip 2 carte → match = restano scoperte, no match = si richiudono
- Animazione: alpha fade reveal/hide
- Progressione: Beginner → Normal → Hard automatica, timer totale
- **Verifica**: 3 livelli completabili, dialog finale con tempi

### Fase 5: Discovery Mode — `[ ]`
- `discovery-mode.ts` + `discovery-square.ts` + `discovery-levels.ts`
- Logica: tap per scoprire → giusto = ding, sbagliato = errore + perdi vita
- Animazione: flip orizzontale (squeeze → expand) per rivelare colore
- 22 livelli predefiniti (3×3 fino a 5×7), sistema vite (max 20)
- `HUD.tsx`: mostra vite e livello corrente
- **Verifica**: progressione livelli, vite decrementano, game over a 0

### Fase 6: Complementary Mode (il più complesso) — `[ ]`
- `complementary-mode.ts` + `complementary-square.ts` + `wall.ts`
- `complementary-levels.ts`: 13 livelli portati da MainActivity
- Meccaniche: drag per creare percorso, colori ciclano, undo tornando indietro
- Tipi quadrato: EMPTY, COLORED, ICE_BLOCK, ICE_BLOCK_BROKEN, INVISIBLE
- Muri verticali/orizzontali che bloccano il movimento
- Stella collezionabile
- Indicatori ultimo/penultimo quadrato nel percorso
- HUD: "BEST: X" e "DONE: Y", bottoni reset/next level
- `solver.ts`: port di everyWay() per calcolo mosse perfette
- **Verifica**: 13 livelli giocabili, muri bloccano, ghiaccio si rompe, stelle raccoglibili

### Fase 7: Polish + Deploy — `[ ]`
- PWA: `manifest.json`, icone, meta tags viewport
- `localStorage` per salvataggio progressi e high scores
- Transizioni tra scene (fade)
- Test su mobile reale (touch, performance)
- Deploy: `npm run build && wrangler pages deploy dist/`
- Aggiornare CLAUDE.md con nuovi comandi build/dev/deploy

## Comandi di Sviluppo (post-setup)

```bash
npm run dev          # Vite dev server
npm run build        # Build produzione
npm run preview      # Preview build locale
wrangler pages deploy dist/   # Deploy su Cloudflare
```

## Note Tecniche Chiave

- **Grid sizing**: L'algoritmo originale parte da SIZE=100 e riduce finché la griglia entra nello schermo, poi cresce per riempire. Cap a width/5 per Discovery/Complementary. Port diretto.
- **Complementary touch**: usa `move` (drag), non solo `up`. Il percorso è una lista di indici. Tornare al penultimo = undo.
- **Adjacency + walls**: due quadrati adiacenti solo se ortogonali E nessun muro tra loro. Array `verticalWalls`/`horizontalWalls` indicizzati per posizione nella griglia.
- **Colori complementari**: 3 coppie (Yellow-Purple, Blue-Orange, Green-Red) + 3 triple per difficoltà avanzata.
- **Suoni**: il browser blocca AudioContext fino a prima interazione utente → resume on first tap.
