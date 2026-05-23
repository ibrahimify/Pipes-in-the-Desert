# Final Traceability Report

## AI Tool Findings

The working code is broadly traceable to the previous documents. The strongest links are between the problem specification, requirements, analysis model version 2, detailed prototype plan, prototype program, GUI specification, and the final Java implementation.

The final GUI uses the existing prototype model instead of duplicating rules. This agrees with the GUI specification principle that the prototype classes remain the Model, while the final user interface adds View and Controller behavior. Object and action buttons are placed in a right-side toolbar so the pipe network remains visible during gameplay. The bottom message bar provides contextual instructions for the selected action.

The professor's main traceability concern was the gap between 27 detailed-plan test cases and 10 prototype-program test records. That concern is valid. The final GUI now exposes the missing demonstration cases directly, but the historical prototype document still contains only 10 recorded test protocols.

## Requirements Traceability Matrix

| Requirement / Case | Problem Spec | Requirements | Analysis Model V2 | Skeleton / Prototype Plans | Prototype Code | GUI Spec | Final Code |
|---|---|---|---|---|---|---|---|
| Start game | Game played by two teams | Start / setup game | System.startGame | Initial game setup | `Main.startGame`, `System.startGame` | MainMenuView, GameSetupView | `FinalGuiMain.startGame` |
| End game / winner | Most water wins | End Game / Declare Winner | ScoreBoard, GameTimer | End-game tests | `ScoreBoard.determineWinner` | EndGameView | `FinalGuiMain.finishGame` |
| Move on pipes/pumps | People move only on pipes/pumps | Move Player | Player movement rules | Movement test cases | `Player.moveTo` | GameView actions | GUI `Move` action |
| Pipe occupancy | Single person on pipe | Move Player FRs | Pipe occupant | Movement validation | `Pipe.setOccupant`, `Player.moveTo` | PlayerView | Canvas and move validation |
| Puncture pipe | Saboteurs puncture pipes | Puncture Pipe | Saboteur, Pipe | Puncture tests | `Saboteur.puncturePipe` | ActionPanelView | GUI `Puncture Pipe` |
| Repair pipe | Plumbers fix leaks | Repair Pipe/Pump | Plumber, Pipe | Repair tests | `Plumber.repairPipe` | ActionPanelView | GUI `Repair Pipe` |
| Set pump direction | Pump input/output selectable | Change Pump Direction | Pump, Plumber, Saboteur | Direction tests | `Pump.setDirection` | ActionPanelView | GUI `Change Pump Direction` |
| Pump breakdown | Pump may go out of order | Randomly Break Pump | Pump state | Breakdown tests | `Pump.breakDown`, `System.updateRoundEvents` | PumpView state | GUI `Break Pump` |
| Repair pump | Plumbers fix pumps | Repair Pipe/Pump | Plumber, Pump | Repair pump tests | `Plumber.repairPump` | ActionPanelView | GUI `Repair Pump` |
| Disconnect pipe | Pipe end can be disconnected | Disconnect / free end rules | PipeNetwork, Pipe | Disconnect tests | `PipeNetwork.disconnectElements` | ActionPanelView | GUI `Disconnect Pipe` |
| Connect pipe | Free end can reconnect | Connect / place pipe | PipeNetwork, Pipe | Connect tests | `Pipe.connectFreeEnd` | ActionPanelView | GUI `Connect Pipe` |
| Pick up pump / add pump | Pumps made at cistern | Insert New Pump | Plumber, Cistern | Pickup and insert tests | `Plumber.collectPumpFromCistern`, `PipeNetwork.insertPump` | Right toolbar | GUI `Add Pump` |
| Pick up pipe / add pipe | Pipes made at cistern | Place New Pipe | Plumber, Cistern | Pickup and free-end tests | `Plumber.collectPipeFromCistern`, `PipeNetwork.connectElements` | Right toolbar | GUI `Add Pipe` |
| Insert pump into pipe | Pump inserted into pipe middle | Insert New Pump | PipeNetwork.insertPump | Insert pump tests | `PipeNetwork.insertPump` | Right toolbar | GUI `Add Pump` |
| Leakage scoring | Leaked water scores saboteurs | Water-flow scoring | WaterFlowManager, ScoreBoard | Water-flow tests | `WaterFlowManager.registerLeakedWater` | ScoreBoardView | GUI `Water Flow` |
| Delivered water scoring | Cistern water scores plumbers | Water-flow scoring | WaterFlowManager, ScoreBoard | Water-flow tests | `WaterFlowManager.registerDeliveredWater` | ScoreBoardView | Score label and flow recalculation |

## Bidirectional Traceability Notes

Provable from document to code:
- All minimum final features listed by the professor have a direct code entry point in the model and a GUI action in `FinalGuiMain`.
- The GUI architecture is traceable to the GUI specification: Model is the existing prototype code, View/Controller are represented by the final Swing frame and canvas.
- The role-assignment ambiguity in the GUI specification is resolved by the setup screen: both plumber and saboteur roles are assigned before one shared local multiplayer session starts.
- The final screen layout follows the submitted GUI direction: center game map, left status/info, right add/remove/action buttons, top HUD, and bottom message/instruction log.

Provable from code to document:
- Domain classes map to the analysis object catalog: `System`, `Player`, `Plumber`, `Saboteur`, `PipeNetwork`, `NetworkElement`, `Pipe`, `Pump`, `Spring`, `Cistern`, `ScoreBoard`, `GameTimer`, `WaterFlowManager`, and `Team`.
- GUI actions map to use cases and test cases through the RTM above.

Not fully provable:
- The historical prototype document records 10 test protocols, while the detailed prototype plan listed 27 test cases. Some final GUI actions cover the omitted cases, but the old submitted prototype program document does not prove all 27 individually.
- Random pump breakdown is deterministic in the final GUI through `Break Pump Event` for presentation reliability, while the model still supports automatic random breakdown during round updates.
- Exact visual layout is not fully traceable to the GUI mockups because final Swing layout adapts to the running network and presentation needs.

## Disagreement With AI Findings

The AI finding about sparse comments in `Main.java` was only partly accepted. The console entry point was already documented near the 20 percent threshold, but the final submission now uses `FinalGuiMain` for presentation and keeps the console prototype as a supported alternate entry point. The new GUI code is documented with class, method, and field comments.
