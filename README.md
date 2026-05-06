# MultiHotbar

A NeoForge mod for Minecraft 1.21.1 that adds multiple switchable hotbars.

## Features

- Switch between 2 to 5 independent hotbars using keybinds
- Hotbar indicator displayed to the left of the vanilla hotbar showing the current index and navigation arrows
- Hotbars persist across sessions, deaths, and respawns
- Configurable hotbar count via server config

## Keybinds

| Key | Action |
|-----|--------|
| `R` | Next hotbar |
| `F` | Previous hotbar |

Both keys can be rebound in Options → Controls.

## Installation

1. Install [NeoForge 21.1.228](https://neoforged.net/) or later for Minecraft 1.21.1
2. Drop `multihotbar-1.0.0-1.21.1.jar` into your `mods/` folder
3. Launch the game

## Configuration

The config file is generated on first launch at:

```
config/multihotbar-server.toml
```

```toml
[hotbars]
    # number of hotbars available to the player (min 2, max 5)
    hotbarCount = 3
```

Change the value and restart the world for it to take effect. Existing hotbar data is preserved when changing the count.

## Compatibility

- Minecraft 1.21.1
- NeoForge 21.1.228+
- Server-side config (clients connecting to a server use the server's configured value)

## Building from source

```bash
./gradlew build
```

Output: `build/libs/multihotbar-1.0.0-1.21.1.jar`

## License

MIT