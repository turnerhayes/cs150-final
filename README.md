# CS-150 Final Project

To run the simulator, run `python run.py`.

To run the DIARC configuration, run `./gradlew launch -Pmain=edu.tufts.hrilab.config.boxbot.BoxBotConfig` from within the `/diarc/` directory.

You will need to add configs to your system's home directory (not to the files in this directory):


- In some file on your system (`trade.properties`):

```
STARTDISCOVERY=true
```

- In `~/.gradle/gradle.properties`:

```
diarc.planner.ff=<path/to/this/directory>/ff
diarc.tradePeropertiesFile=<path/to/trade.properties>
```