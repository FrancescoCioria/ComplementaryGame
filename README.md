# ComplementaryGame

A color-based puzzle game built with TypeScript, React 19, HTML5 Canvas, and deployed on Cloudflare Pages.

Navigate a grid of colored tiles — each touch toggles a tile between two complementary colors. Move to adjacent tiles and turn them all to the target color in as few moves as possible. Collect stars for bonus points.

## Game Modes

| Mode | Description |
|------|-------------|
| **Complementary** | Toggle adjacent tiles to target color; minimize moves, collect stars |
| **Sequence** | Repeat growing color sequences (Simon Says) |
| **Discovery** | Find target squares among decoys |
| **Memory** | Match card pairs with 3-stage progression |

## Development

```bash
npm install
npm run dev      # Vite dev server
npm run build    # Production build
npm run deploy   # Build + deploy to Cloudflare Pages
```
