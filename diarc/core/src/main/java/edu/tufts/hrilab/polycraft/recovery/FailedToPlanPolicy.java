/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.polycraft.recovery;

import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEService;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import edu.tufts.hrilab.action.annotations.Action;
import edu.tufts.hrilab.action.justification.ConditionJustification;
import edu.tufts.hrilab.action.justification.Justification;
import edu.tufts.hrilab.belief.common.MemoryLevel;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.polycraft.recovery.util.ExploreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FailedToPlanPolicy {
  private static Logger log = LoggerFactory.getLogger(FailedToPlanPolicy.class);
  private List<Predicate> initialExplorationsToTry = new ArrayList<>();
  private List<Predicate> secondaryExplorationsToTry = new ArrayList<>();
  private List<Predicate> tertiaryExplorationsToTry = new ArrayList<>();
  private int initialExplorationIndex = 0;
  private int secondaryExplorationIndex = 0;
  private int tertiaryExplorationIndex = 0;
  private boolean exploredLastAction = false;

  /**
   * Register this class with TRADE.
   */
  public void registerWithTrade(List<String> groups) {
    try {
      TRADE.registerAllServices(this, groups);
    } catch (TRADEException e) {
      log.error("Error trying to register with TRADE.", e);
    }
  }

  /**
   * Main entry point for executing recovery policy.
   *
   * @param actor
   * @param brokenActionSignature
   * @param failureReasons
   * @param goal
   * @return
   * @throws TRADEException
   */
  @TRADEService
  @Action
  public Justification executeFailedToPlanPolicy(Symbol actor, Predicate brokenActionSignature, List<Predicate> failureReasons, Predicate goal) throws TRADEException {
    // explorations should depend on the goal that can't be planned for. Failing to plan for break_and_pickup(diamond)
    // for example, might be because there's no diamond in the world. Failing to plan for crafting a pogostick, however,
    // should generate lots of potential explorations.

    //////////////////// Generate hypothesis action for getting out of failed-to-plan state //////////////////////////
    Predicate pogostickGoal = Factory.createPredicate("fluent_geq(inventory(self,pogo_stick),1)");
    if (goal.equals(pogostickGoal)) {
      // generate/update lists of actions to try
      generateInitialExplorationsToTry(actor);
      generateSecondaryExplorationsToTry(actor);
      generateTertiaryExplorationsToTry(actor);

      // first try to execute ALL remaining initial explorations
      if (initialExplorationIndex < initialExplorationsToTry.size()) {
        while (initialExplorationIndex < initialExplorationsToTry.size()) {
          Predicate exploration = initialExplorationsToTry.get(initialExplorationIndex++);
          executeExploration(exploration);
        }
      } else if (secondaryExplorationIndex < secondaryExplorationsToTry.size()) {
        // after trying all initial explorations, try secondary explorations, one at a time

        // report novelty
        Set<Term> novelties = new HashSet<>();
        novelties.add(Factory.createPredicate("trades(none)")); // TODO: what should this novelty predicate be?
        TRADE.getAvailableService(new TRADEServiceConstraints().name("reportNovelties")).call(Object.class, novelties);

        Predicate exploration = secondaryExplorationsToTry.get(secondaryExplorationIndex++);
        executeExploration(exploration);
      } else if (tertiaryExplorationIndex < tertiaryExplorationsToTry.size()) {
        Predicate exploration = tertiaryExplorationsToTry.get(tertiaryExplorationIndex++);
        TRADE.getAvailableService(new TRADEServiceConstraints().name("executeExploration")).call(Object.class, actor, exploration);
      }
    } else {
      // TODO: what other goals should generate explorations when failing to plan?
      return new ConditionJustification(false);
    }

    return new ConditionJustification(true);
  }

  /**
   * Generate ordered list of actions to try.
   *
   * @param actor
   * @return
   * @throws TRADEException
   */
  private void generateInitialExplorationsToTry(Symbol actor) {
    // get all traders and form get_trades_from action for each
    List<Symbol> traders = ExploreUtils.getObjectsOfType(Factory.createSymbol("trader"));
    for (Symbol trader : traders) {
      Predicate exploration = Factory.createPredicate("get_trades_from", actor, trader);
      if (!initialExplorationsToTry.contains(exploration)) {
        initialExplorationsToTry.add(exploration);
      }
    }
  }

  /**
   * Generate ordered list of actions to try.
   *
   * @param actor
   * @return
   * @throws TRADEException
   */
  private void generateSecondaryExplorationsToTry(Symbol actor) throws TRADEException {
    // get all non-trader, non-self actors and form interact_with action for each
    List<Symbol> actors = ExploreUtils.getObjectsOfType(Factory.createSymbol("agent"));
    for (Symbol otherActor : actors) {
      if (!otherActor.equals(actor) && !TRADE.getAvailableService(new TRADEServiceConstraints().name("querySupport")).call(boolean.class, Factory.createPredicate("typeobject", otherActor, Factory.createSymbol("trader")))) {
        Predicate exploration = Factory.createPredicate("interact_with", actor, otherActor);
        if (!secondaryExplorationsToTry.contains(exploration)) {
          secondaryExplorationsToTry.add(exploration);
        }
      }
    }

    // explore all rooms in environment
    Predicate exploration = Factory.createPredicate("explore_rooms", actor);
    if (!secondaryExplorationsToTry.contains(exploration)) {
      secondaryExplorationsToTry.add(exploration);
    }
  }

  /**
   * Generate ordered list of actions to try.
   *
   * @param actor
   * @return
   * @throws TRADEException
   */
  private void generateTertiaryExplorationsToTry(Symbol actor) throws TRADEException {

    //try to execute any remaining explorations that may have been discovered late through an Event novelty
//    String characterization = (String) TRADE.callThe("getCharacterization");
//    if (characterization.equals("Event")) {
          /*
          Set<Predicate> eventNovelties = ExploreUtils.getDetectedNovelties(Factory.createSymbol("10"));
          eventNovelties.addAll(ExploreUtils.getDetectedNovelties(Factory.createSymbol("0")));
          Set<Predicate> explorations = new HashSet<>();
          for (Predicate p : eventNovelties) {
            //log.info("event novelty: "+p);
            if (p.getName().contains("fluent")){
              //generate explorations for relevant items for next time
              Symbol item = ((Term)p.get(0)).get(1);
              explorations.addAll((Set<Predicate>) TRADE.callThe("generateExplorationsToTry",new Term("constant",item,item)));
              TRADE.callThe("assertBeliefs", explorations, MemoryLevel.UNIVERSAL);
            }
          }*/
    Predicate lastBrokenAction = TRADE.getAvailableService(new TRADEServiceConstraints().name("getLastBrokenAction")).call(Predicate.class);
    if (lastBrokenAction != null) {
      Set<Predicate> explorations = new HashSet<>();
      Predicate exploration = new Predicate("toExplore", lastBrokenAction, lastBrokenAction);
      tertiaryExplorationsToTry.add(exploration);
      //if (!exploredLastAction) {
      //  TRADE.callThe("executeExploration",actor, exploration);
      //} else {
      TRADE.getAvailableService(new TRADEServiceConstraints().name("assertBeliefs")).call(Object.class, explorations, MemoryLevel.UNIVERSAL);
      //}

      Predicate explorationPredicate = Factory.createPredicate("toExplore(X,Y)");
      List<Map<Variable, Symbol>> explorationResults = TRADE.getAvailableService(new TRADEServiceConstraints().name("queryBelief")).call(List.class, explorationPredicate);
      List<Term> explorationsToTry = new ArrayList<>(explorationPredicate.copyWithNewBindings(explorationResults));
      log.debug("explorations: " + explorationsToTry);
      for (Term t : explorationsToTry) {
        if (!tertiaryExplorationsToTry.contains(exploration)) {
          tertiaryExplorationsToTry.add(new Predicate(t));
        }
      }
    }

//    }
  }

  /**
   * Execute single exploration action.
   *
   * @param explorationToTry
   * @return
   */
  private Justification executeExploration(Predicate explorationToTry) {
    Justification goalJustification = ExploreUtils.doGoal(explorationToTry);
    return goalJustification;
  }

  /**
   * Reset policy between games.
   */
  @TRADEService
  public void setGameOver(boolean goalAchieved) {
    initialExplorationsToTry.clear();
    initialExplorationIndex = 0;
    secondaryExplorationsToTry.clear();
    secondaryExplorationIndex = 0;
    exploredLastAction = false;
  }

}
