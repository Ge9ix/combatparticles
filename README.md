# Combat Particles (Fabric 1.21.11)

Per-kit particle profiles with their own settings screen and hotkeys. No Sodium needed (works fine alongside it).
Needs: Minecraft 1.21.11, Fabric Loader, Fabric API.

## Hotkeys (change them in Controls, category "Combat Particles")

- `;` Open the settings screen
- `'` Switch to the next profile instantly (Spear Mace -> Mace -> Crystal -> SMP), shows the profile name above your hotbar

## Settings screen

- **Combat Particles: ON/OFF** master switch (OFF = vanilla particles and totem animation)
- **Profile** the active kit; the toggles below belong to this profile
- **My totem** Vanilla (big) / Small / Removed, for when YOUR totem pops
- **Small totem size** 50% to 300% (100% = hotbar item size)
- Particle toggles: ON = kept, OFF = removed

## Default profiles

| Profile    | Kept particles                                   | Totem animation (your own pop) |
|------------|--------------------------------------------------|--------------------------------|
| Spear Mace | totem pop + crit                                 | small                          |
| Mace       | totem pop + crit                                 | removed                        |
| Crystal    | totem pop                                        | small                          |
| SMP        | totem pop (everyone) + only MY crits + sweep arc | small                          |

Everything not listed is removed (other particles, explosions, damage hearts, block break, etc.).

"Crit: only my hits" works by remembering what you attacked: crit particles on that target within 0.6 seconds
of your hit count as yours.

## Build

Push to GitHub and the Actions tab builds it (see .github/workflows/build.yml). The jar is the
`combatparticles-jar` artifact. Local build: JDK 25 + Gradle 9.8.2, run `gradle build`.

Config file: `config/combatparticles.json`
