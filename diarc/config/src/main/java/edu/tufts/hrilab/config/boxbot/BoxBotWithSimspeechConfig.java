package edu.tufts.hrilab.config.boxbot;

import edu.tufts.hrilab.diarc.DiarcConfiguration;
import edu.tufts.hrilab.action.GoalManagerComponent;
import edu.tufts.hrilab.boxbot.BoxBotComponent;
import edu.tufts.hrilab.boxbot.BoxBotSimspeechComponent;
import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;

import edu.tufts.hrilab.simspeech.SimSpeechRecognitionComponent;
import edu.tufts.hrilab.simspeech.SimSpeechProductionComponent;
import edu.tufts.hrilab.slug.nlg.SimpleNLGComponent;
import edu.tufts.hrilab.slug.parsing.tldl.TLDLParserComponent;
import edu.tufts.hrilab.slug.pragmatics.PragmaticsComponent;
import edu.tufts.hrilab.slug.refResolution.ReferenceResolutionComponent;
import edu.tufts.hrilab.slug.listen.ListenerComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoxBotWithSimspeechConfig extends DiarcConfiguration {

  protected static Logger log = LoggerFactory.getLogger(BoxBotWithSimspeechConfig.class);
    
  // start the configuration
  @Override
  public void runConfiguration() {
    createInstance(edu.tufts.hrilab.llm.LLMComponent.class, "-endpoint http://vm-llama.eecs.tufts.edu:8080 -service llama");
    //createInstance(edu.tufts.hrilab.llm.LLMComponent.class, "-service openai");
    createInstance(SimSpeechRecognitionComponent.class, "-config boxbot.simspeech -speaker amitis -addressee boxbot");
    createInstance(SimSpeechProductionComponent.class);

    //createInstance(ListenerComponent.class);
    createInstance(edu.tufts.hrilab.slug.listen.ListenerComponent.class);

    createInstance(TLDLParserComponent.class, "-dict boxbot.dict templatedict.dict templatedictLearned.dict");
    createInstance(PragmaticsComponent.class, "-pragrules demos.prag");
    createInstance(ReferenceResolutionComponent.class);
    createInstance(edu.tufts.hrilab.slug.dialogue.DialogueComponent.class);
    createInstance(SimpleNLGComponent.class);
      
    String gmArgs = "-beliefinitfile agents/boxbot.pl demos.pl " +
        "-selector edu.tufts.hrilab.action.selector.GoalPlanningActionSelector " +
        "-asl domains/boxbot.asl core.asl vision.asl dialogue/nlg.asl dialogue/handleSemantics.asl dialogue/nlu.asl " +
        "-goal listen(self)";

    createInstance(GoalManagerComponent.class, gmArgs);
    log.info("BoxBotConfig successfully loaded with Planner integration.");
    createInstance(BoxBotSimspeechComponent.class, "-service parseIt");
  }
}
