# Prototype Test Cases

This folder contains prototype test material for the console program.

## Documented Prototype Protocol

The file `prototype_test_cases.md` lists exactly the ten test cases documented in section 10.2 of `10. Prototype_Program_E5-TeamA.pdf`:

- TC-01 - Successful game initialization
- TC-02 - Invalid team distribution
- TC-03 - Valid player movement
- TC-04 - Invalid movement target
- TC-05 - Saboteur punctures pipe
- TC-06 - Plumber repairs pipe
- TC-07 - Plumber repairs pump
- TC-08 - Change pump direction
- TC-09 - Component generation and pickup
- TC-10 - End game and winner declaration

No extra test cases are added beyond the prototype document.

## Scripted Smoke Test

`start_end_game_input.txt` and `start_end_game_output.txt` are the existing file-input smoke test artifacts for starting a game, progressing turns, reaching the end condition, and exiting the main menu.

Run from the prototype project root after compiling:

```powershell
java -cp build\classes app.Main load test-cases\start_end_game_input.txt save test-cases\start_end_game_output.txt
```

## Automated Self Tests

The self-test suite is implemented in `tests/app/PrototypeTests.java`.
