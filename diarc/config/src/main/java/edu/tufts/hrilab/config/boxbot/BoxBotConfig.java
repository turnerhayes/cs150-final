package edu.tufts.hrilab.config.boxbot;

import edu.tufts.hrilab.diarc.DiarcConfiguration;
import edu.tufts.hrilab.simspeech.SimSpeechRecognitionComponent;
import edu.tufts.hrilab.slug.listen.ListenerComponent;
import edu.tufts.hrilab.action.GoalManagerComponent;
import edu.tufts.hrilab.boxbot.BoxBotComponent;

public class BoxBotConfig extends DiarcConfiguration {
    
  // start the configuration
  @Override
  public void runConfiguration() {
    createInstance(SimSpeechRecognitionComponent.class,
              "-config boxbot.simspeech -speaker admin -addressee boxbot");
    createInstance(ListenerComponent.class);
    
    createInstance(BoxBotComponent.class, "-service parseIt");

    String[] aslFiles = new String[]{
      "core.asl",
      "vision.asl",
      "nao/naodemo.asl",
      "dialogue/nlg.asl",
      "dialogue/handleSemantics.asl",
      "dialogue/nlu.asl",
    };

    String gmArgs = String.format("-beliefinitfile demos.pl agents/boxbot.pl " +
            "-asl %s " +
            "-goal listen(self)", String.join(" ", aslFiles));

    createInstance(GoalManagerComponent.class, gmArgs);
  }
}
