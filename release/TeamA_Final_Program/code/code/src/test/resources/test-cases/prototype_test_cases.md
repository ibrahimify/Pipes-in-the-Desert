# Prototype Program Test Cases

These test cases are taken from section 10.2 of `10. Prototype_Program_E5-TeamA.pdf`.
No additional test cases are included beyond the documented prototype protocol.

## TC-01 - Successful Game Initialization

Tester: Yahya
Date and time: 04.05.2026, 14:00
Documented result: Pass

Precondition:
- The program is compiled successfully.

Steps:
1. Start the program.
2. Select `Start Game`.
3. Enter four players.
4. Assign two players to the Plumber team and two players to the Saboteur team.
5. Enter a valid game duration.

Expected result:
- The game session starts.
- The initial network is created with a spring, cistern, pump, and pipes.
- Players are placed in the playable network.
- The first player's turn is shown.

## TC-02 - Invalid Team Distribution

Tester: Yahya
Date and time: 04.05.2026, 14:00
Documented result: Pass

Precondition:
- The program is compiled successfully.

Steps:
1. Start the program.
2. Select `Start Game`.
3. Enter four players.
4. Assign fewer than two players to one of the two teams.

Expected result:
- The game does not start.
- The program reports that each team must have at least two players.
- The reported team counts match the entered distribution.

## TC-03 - Valid Player Movement

Tester: Yahya
Date and time: 04.05.2026, 14:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- The active player is standing on a pump or pipe with at least one reachable pipe or pump.

Steps:
1. Select the `Move` action.
2. Choose a listed reachable pipe or pump as the target.

Expected result:
- The player moves to the selected target.
- The player's turn is completed.
- The next turn can proceed.

## TC-04 - Invalid Movement Target

Tester: Yahya
Date and time: 04.05.2026, 14:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- The active player is standing on a playable network element.

Steps:
1. Select the `Move` action.
2. Enter an invalid target, such as a non-existent element or a non-playable spring/cistern.

Expected result:
- The player is not moved to the invalid target.
- The action is rejected.
- The player is asked to choose another action.

## TC-05 - Saboteur Punctures Pipe

Tester: Yahya
Date and time: 04.05.2026, 14:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- A Saboteur is standing on an unpunctured pipe.

Steps:
1. On the Saboteur's turn, select `Puncture current pipe`.

Expected result:
- The current pipe becomes punctured.
- Water leaking through the punctured pipe is counted for the Saboteurs.
- The Saboteur's turn is completed.

## TC-06 - Plumber Repairs Pipe

Tester: Aasif
Date and time: 04.05.2026, 22:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- A pipe has been punctured.
- A Plumber is standing on that punctured pipe.

Steps:
1. On the Plumber's turn, select `Repair current pipe`.

Expected result:
- The punctured pipe is repaired.
- The pipe no longer has the punctured state.
- The Plumber's turn is completed.

## TC-07 - Plumber Repairs Pump

Tester: Aasif
Date and time: 04.05.2026, 22:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- A pump has broken.
- A Plumber is standing on the broken pump.

Steps:
1. On the Plumber's turn, select `Repair current pump`.

Expected result:
- The broken pump is repaired.
- The pump returns to working state.
- The Plumber's turn is completed.

## TC-08 - Change Pump Direction

Tester: Aasif
Date and time: 04.05.2026, 22:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- The active player is standing on a working pump.
- The pump has at least two connected pipes.

Steps:
1. Select `Change current pump direction`.
2. Select one connected pipe as the input.
3. Select a different connected pipe as the output.

Expected result:
- The pump direction is updated to the selected input and output pipes.
- Water flow uses the new pump direction during recalculation.
- The player's turn is completed.

## TC-09 - Component Generation And Pickup

Tester: Aasif
Date and time: 04.05.2026, 22:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- A cistern has generated at least one component.
- A Plumber is close enough to the cistern to pick up the generated component.

Steps:
1. On the Plumber's turn, select the pickup action for the generated component.
2. Select the reachable cistern.

Expected result:
- The generated component count at the cistern decreases.
- The Plumber carries the selected component.
- The pickup follows the component type selected by the player.

## TC-10 - End Game And Winner Declaration

Tester: Aasif
Date and time: 04.05.2026, 22:00
Documented result: Pass

Precondition:
- A valid game has been initialized.
- The game timer reaches the end condition.

Steps:
1. Continue turns until the timer expires.
2. Allow the system to run the end-game check.

Expected result:
- The game ends.
- The final scoreboard is evaluated.
- The program declares the winning team or a draw.
