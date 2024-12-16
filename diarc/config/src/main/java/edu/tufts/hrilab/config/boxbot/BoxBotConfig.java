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

/**
 * Configuration for running the DIARC part of the BoxBot simulation.
 */
public class BoxBotConfig extends DiarcConfiguration {

  protected static Logger log = LoggerFactory.getLogger(BoxBotConfig.class);
    
  // start the configuration
  @Override
  public void runConfiguration() {
    // createInstance(edu.tufts.hrilab.llm.LLMComponent.class, "-endpoint http://vm-llama.eecs.tufts.edu:8080 -service llama");
    createInstance(edu.tufts.hrilab.llm.LLMComponent.class, "-service openai");

    String gmArgs = "-beliefinitfile agents/boxbot.pl " +
            "-selector edu.tufts.hrilab.action.selector.GoalPlanningActionSelector " +
            "-asl domains/boxbot.asl " +
            "-goal goal(self,and(atDoor(),isSwitchPressed()))";

    createInstance(BoxBotComponent.class);
    createInstance(GoalManagerComponent.class, gmArgs);
  }
}
