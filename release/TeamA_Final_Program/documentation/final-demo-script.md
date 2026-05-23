# Final Program Demo Script

Target duration: 10 minutes.

## Startup

1. Run `powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run-final-gui.ps1` from `code`.
2. Keep the default local multiplayer setup: two plumbers and two saboteurs.
3. Start the final program and point out the visible network, player turn, scores, timer, left status panel, right action toolbar, and bottom message log.

## GUI Layout

Object and action buttons are placed in a right-side toolbar. This keeps the pipe network visible while allowing the player to add, remove, repair, and rotate elements during gameplay. The bottom message bar displays contextual instructions for the selected action.

Screen structure:
- Left: status and selected-object information.
- Center: grid-based pipe network map.
- Right: object, pipe, pump, player, and demo action buttons.
- Top: timer, score, and current turn.
- Bottom: contextual instructions and message log.

## Required Cases

1. Start game: show the setup screen and gameplay screen.
2. Stepping on elements: use `Move` to move a player from the pump to a neighboring pipe, then later back to a pump.
3. Puncturing a pipe: move a saboteur to a pipe and click `Puncture Pipe`.
4. Leakage of water: click `Water Flow` and show the saboteur score increasing.
5. Fixing a pipe: move a plumber to the punctured pipe and click `Repair Pipe`.
6. Setting a pump direction: stand on the pump and use `Change Pump Direction`.
7. Pump is broken: click `Break Pump`.
8. Fixing a pump: on a plumber turn at the pump, click `Repair Pump`.
9. Disconnecting a pipe: on a plumber turn, click `Disconnect Pipe`, select a pipe and neighbor, and show the `FREE_END` state.
10. Connecting a pipe: click `Connect Pipe` and reconnect the free pipe end.
11. Adding a pipe: use the right toolbar `Add Pipe` button to create a pipe at a cistern with a free end.
12. Putting a pump into a pipe: select or choose a pipe, then use the right toolbar `Add Pump` button.
13. End game: click `End Game` and show the winner/draw dialog.

## Traceability Talk

Use `documentation/final-traceability-report.md` for the 5-minute traceability presentation. Emphasize that the final GUI keeps the prototype model as the implementation source of truth and that weak traceability is explicitly marked.
