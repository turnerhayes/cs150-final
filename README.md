# CS-150 Final Project

To run the simulator alone, run `python run.py`.

## Simulator integration

To run the DIARC component with simulator integration and the web UI to trigger actions:
1. Run the simulator socket environment from the root directory, `python sock_env.py`
2. In a separate terminal, run `./gradlew launch -Pmain=edu.tufts.hrilab.config.boxbot.BoxBotConfig --args="-g"` from the diarc/ directory
3. In a third terminal, run `./gradlew launchGui` from the diarc/ directory
4. Go to the URL output in step 3 (probably will be http://localhost:3000/)
5. On the web UI, go to the Goal Manager tab
6. Choose the action you want to execute (e.g. `moveWest()`, `moveEast()`, etc.)
7. In the Submit Action tab, enter `self` for the `actor` argument to the action, then click the Submit button
8. The simulator that should have appeared as a result of step 1 should show the selected action being executed (if possicle)