# Aether (Fabric 1.21.11 client)

Push this repo to GitHub. The workflow in `.github/workflows/build.yml` builds on every push;
download the jar from the run's **Artifacts** section (use the one WITHOUT `-sources`).

Right Shift opens the ClickGUI. Right-click a module to expand its settings.
Config saves to `config/aether.json`.

If the build fails on dependency versions, update `gradle.properties` from https://fabricmc.net/develop.
Files marked `VERSION-SENSITIVE` (EspRenderer, AetherClient, ClickGuiScreen input) may need small mapping tweaks.

## Modules
Combat: Aimbot, Triggerbot, Reach, Velocity, AutoArmor, AutoTotem, CrystalMacro, AnchorMacro, StunSlam. Movement: Freecam, Flight, Sprint, NoFall. World also has FastPlace.
Render: StorageFinder, SpawnerFinder, Visuals (fullbright / FOV / entity ESP). World: TuffChunkFinder, BaseFinder.

**Keybinds:** middle-click a module in the GUI, press a key (Backspace clears, Esc cancels).

## New: HUD + sound
- **SpotifyHud** (HUD panel): shows the song from the Spotify *desktop app*. Settings: Show (While Playing / On Song Change / Always),
  Show Duration, Position + offsets, Scale, Background Opacity, Progress, Equalizer, Accent color.
  Windows reads the Spotify window title (no progress bar). Linux needs `playerctl`. macOS uses AppleScript.
- **Startup sound**: plays on the title screen once per launch (voice + chime). Toggle/volume in ClickGUI module settings.
- Other HUD: ActiveMods, InfoHud, TargetHud, Radar, Keystrokes. More gameplay: AutoClicker, Speed, Step, Zoom.

## More modules
Combat: Hitboxes, AutoEat. Movement: AutoWalk, AntiAFK. World: OreFinder, ChunkBorders, FastBreak.
HUD: Notifications, Watermark, ArmorHud, PotionHud. GUI: just start typing to search modules.

## Keybinds (every module)
Middle-click any module in the ClickGUI, then press a key. A **Key Mode** setting appears on that module:
**Toggle** (press to switch on/off) or **Hold** (active only while the key is held).
Defaults: Freelook = hold Left Alt, Zoom = hold C, Xray = toggle X.
**Xray** swaps terrain rendering for only the blocks you choose (not compatible with Sodium/Iris). **Freelook** lets you look around without turning your character.

## Even more modules
**EndBaseFinder** (World): loops `/rtp end` -> waits for chunks -> scans for player bases (ignores End cities/ships) -> saves coords to `config/aether_bases.txt`
-> stays, keeps going, or disconnects. Also leaves if a player gets close or you take damage. AutoLeave, AutoRespawn, DeathCoords, PlayerAlert, AutoSneak, AutoTool, AutoFish, ChestStealer.
