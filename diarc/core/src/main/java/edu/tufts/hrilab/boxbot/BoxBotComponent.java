package edu.tufts.hrilab.boxbot;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import edu.tufts.hrilab.action.justification.ConditionJustification;
import edu.tufts.hrilab.action.justification.Justification;
import edu.tufts.hrilab.diarc.DiarcComponent;
import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.interfaces.BoxBotSimulatorInterface;
import edu.tufts.hrilab.supermarket.SupermarketObservation;
import edu.tufts.hrilab.util.Util;
import edu.tufts.hrilab.boxbot.actions.Left;
import edu.tufts.hrilab.boxbot.actions.Right;
import edu.tufts.hrilab.boxbot.actions.Up;
import edu.tufts.hrilab.boxbot.actions.Down;
import edu.tufts.hrilab.boxbot.actions.ToggleHold;

import java.util.HashSet;

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
}
