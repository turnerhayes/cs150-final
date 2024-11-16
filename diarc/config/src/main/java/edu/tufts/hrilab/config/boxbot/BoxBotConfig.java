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
              "-config boxbot.simspeech -speaker turner -addressee boxbot");
    createInstance(ListenerComponent.class);
    
    createInstance(BoxBotComponent.class, "-service parseIt");

    String gmArgs = "-beliefinitfile demos.pl agents/twonaoagents.pl " +
            "-asl core.asl vision.asl nao/naodemo.asl dialogue/nlg.asl dialogue/handleSemantics.asl dialogue/nlu.asl " +
            "-goal listen(self)";

    createInstance(GoalManagerComponent.class, gmArgs);
  }
}
