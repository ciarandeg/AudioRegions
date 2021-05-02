# Audio Regions
AudioRegions is a soundtracking mod prototype created for *Crypts & Creepers*.
It blocks all of Minecraft's vanilla music, and replaces it with a customizable region-based soundtrack.

**NOTE:** You probably shouldn't actually use this mod; it's fairly fully-featured, but it's a prototype with
several major bugs that will not be fixed. I'm currently working on the production version, SoundBounds,
which will be stable and built with Kotlin+Architectury rather than Java+Forge

## Features
- Smooth, customizable-length fades between tracks
- Fully-featured CLI and wand system for creating and editing regions
  - Includes a "now playing" command with optional Bandcamp/SoundCloud artist links
- Dimension-specific regions
- Volume control using vanilla options screen
- Swappable resource packs
- Music streams from disk
- Playlist system allows for multiple playback configurations:
    - Sequential playlists
    - Shuffled playlists
    - Simple looping songs (one part)
    - Looping songs with non-looping introductions/pickups (two parts)
- Low-pass filter effect on music when player is underwater

## How does it work?
AudioRegions is, in essence, a custom OpenAL soundtracking engine, hooked into minecraft through a CLI.
It runs largely independent of the game itself, other than to keep track of player position and serialize data.
Since Minecraft and AudioRegions both use LWJGL+OpenAL for their audio engines, hardware issues and other incongruities
are minimized.

## Setup
I'll fix this soon. Your sounds should be contained in a resource pack, at path `assets/cnc/sounds/music/sound.ogg`.
You do not need a `sounds.json` file, as AudioRegions doesn't rely on Minecraft's `ResourceLocation` objects for
music playback. Instead, song metadata is stored in `songs.jon` on the dedicated server.

The region bounds themselves are stored in a server config file at `<path_to_server>/config/audio_regions.json`.
The file must consist of an array named `regionData`, specified as so:

```
{
  "regionData": [
    {
      "name": "citadel",
      "resourceName": "was_walkthedinosaur",
      "loop": true,
      "boundingBoxes": [
        {
          "corner1": [ 532, 71, 1283 ],
          "corner2": [ 32, 78, 1475 ]
        }
      ]
    },
    {
      "name": "tavern",
      "resourceName": "chumbawumba_tubthumping",
      "loop": false,
      "boundingBoxes": [
        {
          "corner1": [ -2556, 66, -1839 ],
          "corner2": [ -2552, 71, -1836 ]
        }
      ]
    },
    {
      "name": "clocktower",
      "resourceName": "viper_maybeoneday",
      "loop": true,
      "boundingBoxes": [
        {
          "corner1": [ -2572, -1834 ],
          "corner2": [ -2568, -1830 ]
        },
        {
          "corner1": [ -2573, 72, -2535 ],
          "corner2": [ -2582, 79, -2536 ]
        }
      ]
    }
  ]
}
```

Likewise, song metadata is stored in the server config folder, at `config/songs.json`. This file follows the format
specified in `SongMeta.java`.

## A note on Y coordinates
The player's Y position in-game refers to the position of their feet, not their head. When assigning regions, keep in
mind that in order for the sound not to fade out when the player jumps, the bounding box must be a minimum of three
blocks tall. Additionally, you can omit the y coordinate entirely from a region's coordinate arrays in order to have
that axis be unbounded (as in the third example above).
## Another note, this time about the config files
There should be no overlap between any of the regions in the `audio_regions.json` file. I do not know what happens when
regions overlap, so don't do it -- the behaviour is undefined. Preferably stick to discrete, contiguous regions.

## Todos/planned features
### Before initial release
- Subregions (either through hierarchy/composite or through a priority system)
- An API for use with tools like CryptMaster
#### Bugfixes
- Generate blank config on unconfigured servers
- Fix single-player to multiplayer bug
- Don't spend time fading out from an idling playlist
### After initial release
- Linear/adaptive song mode for dungeons
- Multi-world support
- Improve region search algorithm
- A validator, to prevent overlaps/undefined behaviour
