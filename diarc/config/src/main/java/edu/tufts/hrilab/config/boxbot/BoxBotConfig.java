package edu.tufts.hrilab.config.boxbot;

import edu.tufts.hrilab.diarc.DiarcConfiguration;
import edu.tufts.hrilab.action.GoalManagerComponent;
import edu.tufts.hrilab.boxbot.BoxBotComponent;

public class BoxBotConfig extends DiarcConfiguration {
    
  // start the configuration
  @Override
  public void runConfiguration() {
    createInstance(BoxBotComponent.class);

    String[] aslFiles = new String[]{
      "core.asl",
      "vision.asl",
    };

    String gmArgs = String.format("-beliefinitfile demos.pl agents/boxbot.pl " +
            "-asl %s " +
            "-goal listen(self)", String.join(" ", aslFiles));

    createInstance(GoalManagerComponent.class, gmArgs);
  }
}
