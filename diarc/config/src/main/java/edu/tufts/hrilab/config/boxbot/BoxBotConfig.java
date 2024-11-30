package edu.tufts.hrilab.config.boxbot;

import edu.tufts.hrilab.diarc.DiarcConfiguration;
import edu.tufts.hrilab.action.GoalManagerComponent;
import edu.tufts.hrilab.boxbot.BoxBotComponent;

import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;

import edu.tufts.hrilab.simspeech.SimSpeechRecognitionComponent;
import edu.tufts.hrilab.simspeech.SimSpeechProductionComponent;
import edu.tufts.hrilab.slug.nlg.SimpleNLGComponent;
import edu.tufts.hrilab.slug.parsing.tldl.TLDLParserComponent;
import edu.tufts.hrilab.slug.pragmatics.PragmaticsComponent;
import edu.tufts.hrilab.slug.refResolution.ReferenceResolutionComponent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxBotConfig extends DiarcConfiguration {

  protected static Logger log = LoggerFactory.getLogger(BoxBotConfig.class);
    
  // start the configuration
  @Override
  public void runConfiguration() {
    createInstance(BoxBotComponent.class);
    createInstance(edu.tufts.hrilab.llm.LLMComponent.class, "-endpoint http://vm-llama.eecs.tufts.edu:8080");
    //createInstance(SimSpeechRecognitionComponent.class, "-config speechinput.simspeech -speaker amitis -addressee boxdropper");
    //createInstance(SimSpeechProductionComponent.class);

    createInstance(edu.tufts.hrilab.slug.listen.ListenerComponent.class);
    createInstance(TLDLParserComponent.class, "-dict templatedict.dict templatedictLearned.dict");
    createInstance(PragmaticsComponent.class, "-pragrules demos.prag");
    createInstance(ReferenceResolutionComponent.class);
    createInstance(edu.tufts.hrilab.slug.dialogue.DialogueComponent.class);
    createInstance(SimpleNLGComponent.class);
      
    String[] aslFiles = new String[]{
      "core.asl",
      "vision.asl",
      "boxbot.asl",
    };

    String gmArgs = String.format("-beliefinitfile demos.pl agents/boxbot.pl " +
            "-selector edu.tufts.hrilab.action.selector.GoalPlanningActionSelector " +
            "-asl %s " +
            "-goal listen(self)", String.join(" ", aslFiles));

    createInstance(GoalManagerComponent.class, gmArgs);
    log.info("BoxBotConfig successfully loaded with Planner integration.");
  }
}
