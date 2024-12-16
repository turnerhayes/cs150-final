package edu.tufts.hrilab.boxbot;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.thinkingrobots.trade.TRADEService;
import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEException;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import ai.thinkingrobots.trade.TRADEService;
import edu.tufts.hrilab.action.annotations.Observes;
import edu.tufts.hrilab.action.justification.ConditionJustification;
import edu.tufts.hrilab.action.justification.Justification;
import edu.tufts.hrilab.action.annotations.Observes;
import edu.tufts.hrilab.diarc.DiarcComponent;
import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.interfaces.BoxBotSimulatorInterface;
import edu.tufts.hrilab.util.Util;
import edu.tufts.hrilab.boxbot.actions.East;
import edu.tufts.hrilab.boxbot.actions.North;
import edu.tufts.hrilab.boxbot.actions.West;
import edu.tufts.hrilab.boxbot.actions.South;
import edu.tufts.hrilab.boxbot.actions.ToggleHold;
import edu.tufts.hrilab.boxbot.actions.GetObservation;

import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Component facilitating interaction with a BoxBot simulation.
 */
public class BoxBotComponent extends DiarcComponent implements BoxBotSimulatorInterface {
    protected int socketPort = 9000;
    protected GamePlay game;
    private static final Logger log = LoggerFactory.getLogger(BoxBotComponent.class);

    /**
     * This number represents how far from the target position the robot can be and be
     * considered "at the target".
     */
    private static final int POSITION_TOLERANCE = 5;

    public BoxBotComponent() {
    }

    @Override
    protected void init() {
        super.init();
        this.game = new GamePlay(this.socketPort);
        Util.Sleep(500);
        this.game.perform(new GetObservation());
    }

    /*************************************
     * OBSERVERS
     *************************************/


    @Override
    public List<HashMap<Variable, Symbol>> northOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] <= this.game.observation.boxPos[1]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> southOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] + this.game.observation.robotHeight >= this.game.observation.boxPos[1] + this.game.observation.boxHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> eastOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.game.observation.robotWidth >= this.game.observation.boxPos[0] + this.game.observation.boxHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> westOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.game.observation.robotWidth <= this.game.observation.boxPos[0]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> northOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] <= this.game.observation.switchPos[1]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> southOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] >= this.game.observation.switchPos[1] + this.game.observation.switchHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> eastOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] >= this.game.observation.switchPos[0] + this.game.observation.switchWidth) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> westOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.game.observation.robotWidth <= this.game.observation.switchPos[0]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> isAtSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (
            isApproximatelyAt(this.game.observation.robotPos[0] + this.game.observation.robotWidth, this.game.observation.switchPos[0]) &&
            isApproximatelyAt(this.game.observation.robotPos[1], this.game.observation.switchPos[1])
        ) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> isInPickupRange(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        if (this.game.observation.isInPickupRange) {
            list.add(new HashMap<>());
        }
        return list;
    }
    
    @Override
    public List<HashMap<Variable, Symbol>> northOfDoorCenter(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        
        if (
            this.game.observation.robotPos[1] <= this.game.observation.doorTop +
                Math.floor((this.game.observation.doorBottom - this.game.observation.doorTop)/2)
        ) {
            list.add(new HashMap<>());
        }
        return list;
    }
    
    @Override
    public List<HashMap<Variable, Symbol>> southOfDoorCenter(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        
        if (
            this.game.observation.robotPos[1] >= this.game.observation.doorTop +
                Math.floor((this.game.observation.doorBottom - this.game.observation.doorTop)/2)
        ) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> eastOfDoor(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] > 0) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> isAtDoor(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (
            this.game.observation.robotPos[0] == this.game.observation.wallWidth &&
            this.game.observation.robotPos[1] <= this.game.observation.doorBottom &&
            this.game.observation.robotPos[1] >= this.game.observation.doorTop
        ) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public List<HashMap<Variable, Symbol>> canMoveWest(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        if (
            this.game.observation.robotPos[0] > this.game.observation.wallWidth
        ) {
            list.add(new HashMap<>());
        }
        return list;
    }

    /*************************************
     * UTILITY FUNCTIONS
     *************************************/

    private boolean isApproximatelyAt(int robotPos, int targetPos) {
        if (robotPos < targetPos - POSITION_TOLERANCE) {
            return false;
        }

        if (robotPos > targetPos + POSITION_TOLERANCE) {
            return false;
        }

        return true;
    }

    /*************************************
     * ACTION IMPLEMENTATIONS
     *************************************/

    @Override
    public Justification getObservation() {
        GameAction action = new GetObservation();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveWest() {
        GameAction action = new West();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveEast() {
        GameAction action = new East();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveNorth() {
        GameAction action = new North();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveSouth() {
        GameAction action = new South();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification toggleHold() {
        GameAction action = new ToggleHold();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
}
