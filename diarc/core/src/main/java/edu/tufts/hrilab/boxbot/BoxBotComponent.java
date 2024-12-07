package edu.tufts.hrilab.boxbot;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.thinkingrobots.trade.TRADEService;
import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import edu.tufts.hrilab.action.justification.ConditionJustification;
import edu.tufts.hrilab.action.justification.Justification;
import edu.tufts.hrilab.action.annotations.Observes;
import edu.tufts.hrilab.diarc.DiarcComponent;
import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.interfaces.BoxBotSimulatorInterface;
import edu.tufts.hrilab.supermarket.SupermarketObservation;
import edu.tufts.hrilab.util.Util;
import edu.tufts.hrilab.boxbot.actions.Left;
import edu.tufts.hrilab.boxbot.actions.Right;
import edu.tufts.hrilab.boxbot.actions.Up;
import edu.tufts.hrilab.boxbot.actions.Down;
import edu.tufts.hrilab.boxbot.actions.ToggleHold;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxBotComponent extends DiarcComponent implements BoxBotSimulatorInterface {
    protected int socketPort = 9000;
    protected GamePlay game;
    private static final Logger log = LoggerFactory.getLogger(BoxBotComponent.class);

    public BoxBotComponent() {
    }

    @Override
    protected void init() {
        super.init();
        this.game = new GamePlay(this.socketPort);
        Util.Sleep(500);
    }

    protected void updateBeliefs() {
        BoxBotObservation obs = this.game.observation;

        if (obs == null) {
            return;
        }

        Set<Term> toSubmit = new HashSet<>();

        if (obs.isHoldingBox) {
            toSubmit.add(Factory.createPredicate("isHoldingBox(self)"));
        }
        else {
            toSubmit.add(Factory.createNegatedPredicate("isHoldingBox(self)"));
        }

        if (obs.isInPickupRange) {
            toSubmit.add(Factory.createPredicate("isInPickupRange(self)"));
        }
        else {
            toSubmit.add(Factory.createNegatedPredicate("isInPickupRange(self)"));
        }

        if (obs.isSwitchPressed) {
            toSubmit.add(Factory.createPredicate("isSwitchPressed(self)"));
        }
        else {
            toSubmit.add(Factory.createNegatedPredicate("isSwitchPressed(self)"));
        }

        // toSubmit.add(Factory.createVariable("ROBOTPOS", "location"));

        if (!toSubmit.isEmpty()) {
            try {
                TRADE.getAvailableService(new TRADEServiceConstraints().name("assertBeliefs").argTypes(Set.class))
                        .call(void.class, toSubmit);
            } catch (TRADEException e) {
                log.error("assertBeliefs not found", e);
            }
        }
    }

    @Override
    public Justification moveLeft() {
        GameAction action = new Left();
        game.perform(action);
        this.updateBeliefs();
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveRight() {
        GameAction action = new Right();
        game.perform(action);
        this.updateBeliefs();
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveUp() {
        GameAction action = new Up();
        game.perform(action);
        this.updateBeliefs();
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveDown() {
        GameAction action = new Down();
        game.perform(action);
        this.updateBeliefs();
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification toggleHold() {
        GameAction action = new ToggleHold();
        game.perform(action);
        this.updateBeliefs();
        return new ConditionJustification(action.getSuccess());
    }

    @TRADEService
    @Observes({"isInPickupRangeObs(?actor)"})
    public boolean isInPickupRangeObs(Term t) {
        log.info("IS IN PICKUP RANGE+++++++++++++++++++++++");
        return false;
        // return this.isInPickupRange;
    }

    @TRADEService
    @Observes({"westOfBox(?actor)"})
    public boolean westOfBox(Term t) {
        log.info("WEST OF BOX");
        return this.boxbot.position[0] <= this.box.position[0];
    }

    @TRADEService
    @Observes({"eastOfBox(?actor)"})
    public boolean eastOfBox(Term t) {
        log.info("EAST OF BOX");
        return this.boxbot.position[0] + this.boxbot.width >= this.box.position[0] + this.box.width;
    }

    @TRADEService
    @Observes({"northOfBox(?actor)"})
    public List<HashMap<Variable, Symbol>> northOfBox(Term t) {
        log.info("NORTH OF BOX");
        List<HashMap<Variable, Symbol>> list = new java.util.ArrayList<>();
        list.add(new HashMap<>());
        return list;
        // return this.boxbot.position[1] <= this.box.position[1];
    }

    @TRADEService
    @Observes({"southOfBox(?actor)"})
    public boolean southOfBox(Term t) {
        log.info("SOUTH OF BOX");
        return this.boxbot.position[1] + this.boxbot.height >= this.box.position[1] + this.box.height;
    }
}
