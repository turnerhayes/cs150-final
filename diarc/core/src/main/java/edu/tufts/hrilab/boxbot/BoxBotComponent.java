package edu.tufts.hrilab.boxbot;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import ai.thinkingrobots.trade.TRADEService;
import edu.tufts.hrilab.action.annotations.Observes;
import edu.tufts.hrilab.action.justification.ConditionJustification;
import edu.tufts.hrilab.action.justification.Justification;
import edu.tufts.hrilab.diarc.DiarcComponent;
import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.fol.Symbol;
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
import edu.tufts.hrilab.boxbot.actions.GetObservation;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
        this.game.perform(new GetObservation());
    }

    protected void updateBeliefs() {
        BoxBotObservation obs = this.game.observation;

        if (obs == null) {
            return;
        }

        Set<Term> toSubmit = new HashSet<>();

        if (obs.isHoldingBox) {
            toSubmit.add(Factory.createPredicate("isHoldingBox(self)"));
        } else {
            toSubmit.add(Factory.createNegatedPredicate("isHoldingBox(self)"));
        }

        if (obs.isInPickupRange) {
            toSubmit.add(Factory.createPredicate("isInPickupRange(self)"));
        } else {
            toSubmit.add(Factory.createNegatedPredicate("isInPickupRange(self)"));
        }

        if (obs.isSwitchPressed) {
            toSubmit.add(Factory.createPredicate("isSwitchPressed(self)"));
        } else {
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

    private int boxWidth = 40;
    private int boxHeight = 40;
    private int robotWidth = 40;
    private int robotHeight = 40;

    @TRADEService
    @Observes({ "northOfBox()" })
    public List<HashMap<Variable, Symbol>> northOfBox(Term term) {
        log.info("called northOfBox");
        log.info("term: {}", term);
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] <= this.game.observation.boxPos[1]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "southOfBox()" })
    public List<HashMap<Variable, Symbol>> southOfBox(Term term) {
        log.info("called southOfBox");
        log.info("term: {}", term);
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] + this.robotHeight >= this.game.observation.boxPos[1] + this.boxHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "eastOfBox()" })
    public List<HashMap<Variable, Symbol>> eastOfBox(Term term) {
        log.info("called eastOfBox");
        log.info("term: {}", term);
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.robotWidth >= this.game.observation.boxPos[0] + this.boxHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "westOfBox()" })
    public List<HashMap<Variable, Symbol>> westOfBox(Term term) {
        log.info("called westOfBox");
        log.info("term: {}", term);
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] <= this.game.observation.boxPos[0]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "isInPickupRange()" })
    public List<HashMap<Variable, Symbol>> isInPickupRange(Term term) {
        log.info("called isInPickupRange");
        log.info("term:", term);
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        if (this.game.observation.isInPickupRange) {
            list.add(new HashMap<>());
        }
        return list;
        // return this.game.observation.robotPos[1] <= this.game.observation.boxPos[1];
    }

    @Override
    public Justification getObservation() {
        log.info("observation action");
        GameAction action = new GetObservation();
        game.perform(action);
        this.updateBeliefs();
        log.info("Action success: {}", action.getSuccess());
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveLeft() {
        log.info("moveLeft action");
        GameAction action = new Left();
        game.perform(action);
        this.updateBeliefs();
        log.info("Action success: {}", action.getSuccess());
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveRight() {
        log.info("moveRight action");
        GameAction action = new Right();
        game.perform(action);
        this.updateBeliefs();
        log.info("Action success: {}", action.getSuccess());
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveUp() {
        log.info("moveUp action");
        GameAction action = new Up();
        game.perform(action);
        this.updateBeliefs();
        log.info("Action success: {}", action.getSuccess());
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveDown() {
        log.info("moveDown action");
        GameAction action = new Down();
        game.perform(action);
        this.updateBeliefs();
        log.info("Action success: {}", action.getSuccess());
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification toggleHold() {
        log.info("toggleHold action");
        GameAction action = new ToggleHold();
        game.perform(action);
        this.updateBeliefs();
        log.info("Action success: {}", action.getSuccess());
        return new ConditionJustification(action.getSuccess());
    }
}
