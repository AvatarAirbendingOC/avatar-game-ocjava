# Avatar MAS — Four Nations

A discrete-step multi-agent simulation built in Java for the "Concept Objet" mini-project. Four factions inspired by *Avatar: The Last Airbender* — Fire Nation, Earth Kingdom, Water Tribe, Air Nomads — move autonomously across a 2D map, trade or steal messages when they cross paths, and race their stationary Master to collect every distinct message first.

## How to run

```
javac *.java
java MainClass
```

Two standalone test entry points exist for checking individual pieces without running the full game:

```
java MapTest         # verifies the grid, SafeZone placement, and tile adjacency
java EncounterTest    # verifies the three encounter rules in isolation, deterministically
```

## Controls

| Control | Effect |
|---|---|
| **Next Step** button / **Space** | Advances one full step — every individual (all 16) takes exactly one turn, in a freshly randomized order |
| **Stop & Show Results** | Ends the run early and scores whatever has been collected so far |

Nothing advances on its own — the game waits for you to trigger each step, which makes it usable for walking an audience through what's happening.

## The rules

**Factions and alliances.** Each faction has 1 stationary Master (`M`) and 3 mobile individuals (`A`, `B`, `C`). Fire Nation and Earth Kingdom are allied as "direct" benders (straight-line movement); Water Tribe and Air Nomads are allied as "flow" benders (diagonal movement).

**Movement.** Every mobile individual moves 1–3 tiles per turn. Above 20% EP, direction is random within its alliance's style. Below 20% EP, it heads straight for its own SafeZone instead and recovers there. Hitting 0 EP turns it into a permanent obstacle and wipes its messages. A rival faction's SafeZone blocks like a wall — only its own members can enter.

**Encounters.** A move that's blocked specifically by another individual (not a plain obstacle) triggers a Meeting, resolved one of three ways:
- **Same faction** (including its own Master): knowledge pools — both sides gain everything.
- **Same alliance, different faction**: a friendly partial trade.
- **Rival alliance**: a coin-flip confrontation — the winner steals a random subset of what the loser has.

**Winning.** A Master wins outright the instant it holds every distinct message in the game. Otherwise, after the step limit, whichever Master holds the most distinct messages wins (ties are a draw).

**Obstacles.** 4 blockers are placed on random empty tiles at the start of every run.

## Java / OOP concepts used

This project was built specifically to put the course's core ideas into practice, not just to produce a working game. Here's what's demonstrated and where:

**Encapsulation** — `Individual`'s state (`ep`, `messages`, position) is `protected`, never touched directly from outside the class; all access goes through controlled methods (`receiveMessage`, `loseEP`, `gainEP`). `getMessages()` returns a defensive copy so callers can't mutate internal state through the reference.

**Inheritance** — a four-level hierarchy: `Individual` (abstract) → `IndustrialAlliance` / `HarmoniousAlliance` (abstract, shared alliance behavior) → `FireNation` / `EarthKingdom` / `WaterTribe` / `AirNomad` → `MasterFireNation` / etc. (Singleton specializations). Each level adds exactly the behavior that belongs at that level, nothing duplicated.

**Polymorphism** — `move()` is called identically on every individual in the shuffled roster regardless of concrete type; a Master's overridden empty body *is* the polymorphism (same call site, different behavior by runtime type). `getSymbol()` works the same way for rendering.

**Interfaces** — `Occupant` is the shared contract between `Individual` and `Obstacle`, so `Tile` and `BoardPanel` can treat either uniformly without knowing which one they're holding.

**Singleton pattern** — all four `Master*` classes: a private constructor plus a static `getInstance()` guarantee exactly one instance per faction, enforced at compile time.

**Static attributes** — each faction class keeps a static `instanceCount`, incremented in its constructor, satisfying the "track how many of each exist" requirement.

**A dedicated randomness class** — every call to `Math.random()`-equivalent logic in the whole project funnels through `RandomGenerator`, a single swappable point for all pseudo-randomness (direction choice, distance, exchange size, coin flips).

**Single-responsibility separation** — `EncounterResolver` owns the three message-sharing rules and nothing else; `SimulationEngine` owns the step loop and termination logic; `EventLog` owns the console/UI text feed; `BoardPanel` owns rendering; `SoundEngine` owns audio. None of these classes know how to do each other's job.

**Enums** — `Direction` models the 8 compass directions as a closed, type-safe set instead of magic strings or ints.

**Records** — `Individual.TravelResult` is a small immutable record bundling "how far did it get" and "what blocked it" from one method call.

**Pattern-matching `instanceof`** — used throughout (`EncounterResolver`, `BoardPanel`) to branch on concrete type without manual casting.

**Threading** — `SimulationEngine.run()` executes on its own background thread, never the Swing Event Dispatch Thread, and bridges back to it safely via `SwingUtilities.invokeAndWait()` whenever it needs to animate, repaint, or show a dialog.

**A semaphore as a step gate** — `java.util.concurrent.Semaphore` implements "wait for the Next Step button" cleanly, without hand-rolled wait/notify.

## Project structure

| File | Role |
|---|---|
| `Individual`, `IndustrialAlliance`, `HarmoniousAlliance` | The abstract hierarchy — shared state and movement template |
| `FireNation`, `EarthKingdom`, `WaterTribe`, `AirNomad` | The four concrete factions |
| `MasterFireNation`, `MasterEarthKingdom`, `MasterWaterTribe`, `MasterAirNomad` | Singleton Master specializations |
| `Direction` | The 8-direction enum |
| `RandomGenerator` | Centralized pseudo-randomness |
| `Occupant`, `Obstacle`, `Tile`, `GameMap` | The board model |
| `EncounterResolver` | The three message-sharing rules |
| `EventLog` | Console + on-screen text feed |
| `SimulationEngine` | The step loop, termination, and threading |
| `BoardPanel`, `GameFrame`, `ScoreboardPanel` | Graphics2D rendering and the window |
| `SoundEngine` | Procedurally synthesized audio cues |
| `MainClass` | Wires everything together and starts the run |
| `MapTest`, `EncounterTest` | Standalone, deterministic tests for the board and the encounter rules |

## Known simplifications

- Confrontations are a 50/50 coin flip, not Rock-Paper-Scissors — allowed explicitly by the spec's "keep it simple" option.
- Sound is procedurally synthesized at runtime (sine waves), not a real soundtrack — the actual *Avatar* music is copyrighted and couldn't be included.
- Movement is straight-line only, no pathfinding around obstacles.
- The step limit and starting parameters (EP, map size, obstacle count) are hardcoded in `MainClass` rather than configurable through a setup screen.

## Ideas not yet built

- A `MovementStrategy` interface (Strategy pattern) as the project's second design pattern.
- Headless multi-run statistics mode for measuring faction win rates.
- Random mid-game events (the spec's meteor/virus bonus).
- A configurable setup screen instead of hardcoded constants.

## Team

*(fill in names and roles per the assignment's anonymous role-assignment requirement)*
