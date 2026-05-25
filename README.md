# Pipes in the Desert
Software Project Laboratory - BME (BMEVIIIAB02)

## Overview
Pipes in the Desert is a strategy-based simulation game developed as part of the Software Project Laboratory course. The game models a dynamic water distribution system where two opposing teams (Plumbers and Saboteurs) compete to either maximize water delivery or increase water loss.

<p align="center">
  <img src="assets/gamerecord/gamerecord.gif" alt="Pipes in the Desert" width="800"/>
</p>

## Project Structure
```
Pipes in the Desert/
|
|-- pom.xml                         # Maven build file
|-- README.md
|-- src/
|   |-- main/
|   |   `-- java/app/               # Final Java Swing game source
|   `-- test/
|       |-- java/app/               # Prototype test harness
|       `-- resources/test-cases/   # Historical prototype scenarios
|-- assets/
|   |-- images/                     # Pixel-art GUI sprites/backgrounds
|   `-- fonts/                      # Game font
|-- scripts/                        # PowerShell compile/run helpers
|-- documentation/                  # Milestone PDFs and reports
|-- archive/                        # Archived legacy layouts and releases
`-- target/                         # Generated build output
```

The old `code/` layout has been archived under `archive/legacy-code-layout/`. Packaged release output is not required for normal development; use the root Maven project instead.

## Documentation Status (as of May 26)

Completed:
- 00 - Cover
- 02 - Project Definition
- 03 - Analysis Model (First Version)
- 04 - Analysis Model (Final Version)
- 05 - Skeleton Plans
- 06 - Skeleton Program
- 07 - Prototype Concept
- 08 - Detailed Prototype Plans
- 10 - Prototype Program
- 11 - Plans for GUI
- 13 - GUI program

Upcoming:
- 14 - Summary (May 29)

## Build & Run

Requirements:
- Java JDK 11 or newer
- Maven 3.8 or newer

Run the final GUI from the repository root:
```powershell
mvn clean compile
mvn exec:java
```

Alternative PowerShell helper:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run-final-gui.ps1
```

Build only:
```powershell
mvn clean compile
```

Run the historical prototype harness:
```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run-tests.ps1
```

## Preview

The game opens in a 1920x1080 Java Swing window. Use the main menu to start a game, configure four players, then control the map from the right-side toolbar. The spring and cistern exist at the start; plumbers try to deliver water to the cistern, and saboteurs try to leak water into the desert.

<details>
<summary>Game controls and expected results</summary>

- **Start Game**: opens the setup screen. Add four players and keep at least one Plumber and one Saboteur.
- **Move**: select `Move`, then click an adjacent pipe or pump. Players can stand only on pipes and pumps. Multiple players may share a pump, but only one player may stand on a pipe.
- **Add Pipe**: select the cistern, then click an adjacent free tile. A new pipe is created with one end connected to the cistern and the other end free, matching the problem definition.
- **Connect Pipe**: select a pipe that has a free end, then click an adjacent spring, pump, cistern, or pipe endpoint. If the selected pipe has no free end, the game rejects the action.
- **Disconnect Pipe**: select a pipe, then click one connected neighbor. That pipe end becomes free and water sent there can leak into the desert.
- **Add Pump**: select a pipe, then click a valid placement. The pipe is split and a pump is inserted between the pipe sections.
- **Change Pump Direction**: the current player must stand on the pump. The pump direction changes and the pump sprite rotates by 90 degrees.
- **Puncture Pipe**: a Saboteur standing on a pipe can puncture it. The pipe shows a leak and water leakage increases the Saboteur score.
- **Repair Pipe**: a Plumber standing on a punctured pipe can repair it. The leak sprite disappears.
- **Repair Pump**: a Plumber standing on a broken pump can repair it. Water can flow through it again.
- **Break Pump**: marks a pump as broken for demonstration/testing. A broken pump blocks water flow.
- **Water Flow**: runs one water-flow step. Connected, repaired paths score for Plumbers; punctured/free-end paths score for Saboteurs.
- **End Turn**: skips the current player's action and advances to the next player.
- **End Game**: opens the final score screen with Plumber and Saboteur scores.

</details>

## Team
This repository contains the project of Team A under the supervision of Dr. Balla Katalin (balla@iit.bme.hu).

| Name                   | GitHub                                      | Code   | Email |
|------------------------|---------------------------------------------|--------|-------|
| Muhammad Ibrahim Shoeb | [ibrahimify](https://github.com/ibrahimify) | OZLVV3 | muhammadibrahimshoeb@gmail.com |
| Arda Gecegorur         | [Arda-23](https://github.com/ardagecegorur) | EKQNAK | ardagecegorur@gmail.com |
| Ilgin Tunc             | [ilgintunc](https://github.com/ilgintunc)   | TDQWFF | ilgintunc11@gmail.com |
| Muhammad Hameez Khan   | [Hameez1khan](https://github.com/Hameez1khan) | TFBB32 | hameezkhan993@gmail.com |
| Yahya Akhrikhar        | [yahyaakh](https://github.com/yahyaakh)     | K20UXP | yahyaakhrikhar02@gmail.com |
| Aasif Mohd             | [Mohdaasif97](https://github.com/Mohdaasif97)     | OI6VE6 | aasifuk1122@gmail.com |

## Schedule Highlights

Prototype presentation: May 5  
Final presentation: May 26  
Final submission (Summary): May 29  

Weekly deadline: Monday 14:30

## Meetings

| Type       | Day       | Time          | Location         |
|------------|-----------|---------------|------------------|
| Online     | Tuesday   | 18:00         | Discord          |
| In-person  | Saturday  | 09:00 - 18:00 | Corvinus Library |

## Description

The system simulates a water transport network consisting of pipes, pumps, springs, and cisterns.

- Plumbers maintain and extend the system
- Saboteurs disrupt flow and cause leaks
- The game runs in turns
- The winner is determined by comparing delivered vs lost water

## Notes

- All documentation follows course milestone structure
- Both draft and final versions of documents are preserved
- Project is developed incrementally according to course workflow

## Links

- [Course Webpage](https://www.iit.bme.hu/oktatas/tanszeki_targyak/BMEVIIIAB02)
- [Problem Definition](https://www.iit.bme.hu/targyak/BMEVIIIAB02/problem-definition)
- [Deadlines](https://www.iit.bme.hu/targyak/BMEVIIIAB02/schedule)
- [Document Templates](https://www.iit.bme.hu/file/1006/document-templates)
