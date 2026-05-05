# Prototype Test Cases

This folder contains input and expected-output files for the ten prototype test cases documented in section 10.2 of `10. Prototype_Program_E5-TeamA.pdf`.

No additional test cases are included beyond the prototype document.

## Documented Input/Output Pairs

- `tc01_successful_game_initialization_input.txt`
- `tc01_successful_game_initialization_output.txt`
- `tc02_invalid_team_distribution_input.txt`
- `tc02_invalid_team_distribution_output.txt`
- `tc03_valid_player_movement_input.txt`
- `tc03_valid_player_movement_output.txt`
- `tc04_invalid_movement_target_input.txt`
- `tc04_invalid_movement_target_output.txt`
- `tc05_saboteur_punctures_pipe_input.txt`
- `tc05_saboteur_punctures_pipe_output.txt`
- `tc06_plumber_repairs_pipe_input.txt`
- `tc06_plumber_repairs_pipe_output.txt`
- `tc07_plumber_repairs_pump_input.txt`
- `tc07_plumber_repairs_pump_output.txt`
- `tc08_change_pump_direction_input.txt`
- `tc08_change_pump_direction_output.txt`
- `tc09_component_generation_and_pickup_input.txt`
- `tc09_component_generation_and_pickup_output.txt`
- `tc10_end_game_and_winner_declaration_input.txt`
- `tc10_end_game_and_winner_declaration_output.txt`

## Existing Smoke Test

The original smoke test files are still present:

- `start_end_game_input.txt`
- `start_end_game_output.txt`

Run from the prototype project root after compiling:

```powershell
java -cp build\classes app.Main load test-cases\start_end_game_input.txt save test-cases\start_end_game_output.txt
```

## Automated Self Tests

The self-test suite is implemented in `tests/app/PrototypeTests.java`.
