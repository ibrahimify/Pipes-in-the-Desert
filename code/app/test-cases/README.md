# Prototype Test Cases

This folder contains input files that can be used with the prototype's file-input mode.

## Start And End Game Smoke Test

Run from the repository root after compiling:

```powershell
java -cp build\classes app.Main load test-cases\start_end_game_input.txt save test-cases\start_end_game_output.txt
```

The script starts a valid four-player game, declines generated events, ends each player turn, reaches the timer end condition, and exits the main menu.

## Automated Self Tests

The main self-test suite is implemented in `tests/app/PrototypeTests.java` and covers:

- Delivered water scoring
- Punctured pipe leakage scoring
- Free-end leakage scoring
- Broken pump leakage scoring
- Wrong pump direction leakage scoring
- Invalid movement to springs/cisterns
- Single-player pipe occupancy
- Multi-player pump occupancy
- Cistern pipe/pump generation
- Pump insertion into an existing pipe
