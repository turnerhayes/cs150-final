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
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.interfaces.BoxBotSimulatorInterface;
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

    private boolean isNorthOfSwitchCenter() {
        return this.game.observation.robotPos[1] <= this.game.observation.switchPos[1] +
            Math.floor(this.game.observation.switchHeight/2);
    }

    private boolean isSouthOfSwitchCenter() {
        return this.game.observation.robotPos[1] >= this.game.observation.switchPos[1] +
            Math.floor(this.game.observation.switchHeight/2);
    }

    @TRADEService
    @Observes({ "northOfBox()" })
    public List<HashMap<Variable, Symbol>> northOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] <= this.game.observation.boxPos[1]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "southOfBox()" })
    public List<HashMap<Variable, Symbol>> southOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] + this.game.observation.robotHeight >= this.game.observation.boxPos[1] + this.game.observation.boxHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "eastOfBox()" })
    public List<HashMap<Variable, Symbol>> eastOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.game.observation.robotWidth >= this.game.observation.boxPos[0] + this.game.observation.boxHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "westOfBox()" })
    public List<HashMap<Variable, Symbol>> westOfBox(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.game.observation.robotWidth <= this.game.observation.boxPos[0]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "northOfSwitch()" })
    public List<HashMap<Variable, Symbol>> northOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] <= this.game.observation.switchPos[1]) {
            list.add(new HashMap<>());
        }
        return list;
    }
    
    @TRADEService
    @Observes({ "northOfSwitchCenter()" })
    public List<HashMap<Variable, Symbol>> northOfSwitchCenter(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (isNorthOfSwitchCenter()) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "southOfSwitch()" })
    public List<HashMap<Variable, Symbol>> southOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[1] >= this.game.observation.switchPos[1] + this.game.observation.switchHeight) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "southOfSwitchCenter()" })
    public List<HashMap<Variable, Symbol>> southOfSwitchCenter(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (isSouthOfSwitchCenter()) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "eastOfSwitch()" })
    public List<HashMap<Variable, Symbol>> eastOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] >= this.game.observation.switchPos[0] + this.game.observation.switchWidth) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "westOfSwitch()" })
    public List<HashMap<Variable, Symbol>> westOfSwitch(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] + this.game.observation.robotWidth <= this.game.observation.switchPos[0]) {
            list.add(new HashMap<>());
        }
        return list;
    }

    private boolean pointIsInArea(int x, int y, int left, int top, int right, int bottom) {
        if (x >= left && x <= right && y >= top && y <= bottom) {
            return true;
        }

        return false;
    }

    private boolean checkCollision(int xOffset, int yOffset) {
        BoxBotObservation observation = this.game.observation;
        int robotX = observation.robotPos[0];
        int robotY = observation.robotPos[1];
        int newLeftX = robotX + xOffset;
        int newTopY = robotY + yOffset;
        int newRightX = newLeftX + observation.robotWidth;
        int newBottomY = newTopY + observation.robotHeight;

        int boxLeftX = observation.boxPos[0];
        int boxTopY = observation.boxPos[1];
        int boxRightX = boxLeftX + observation.boxWidth;
        int boxBottomY = boxTopY + observation.boxHeight;

        int switchLeftX = observation.switchPos[0];
        int switchTopY = observation.switchPos[1];
        int switchRightX = switchLeftX + observation.switchWidth;
        int switchBottomY = switchTopY + observation.switchHeight;

        // check box collision
        if (!observation.isHoldingBox) { // can't collide with box if we're holding it
            if (
                pointIsInArea(newLeftX, newTopY, boxLeftX, boxTopY, boxRightX, boxBottomY) ||
                pointIsInArea(newLeftX, newBottomY, boxLeftX, boxTopY, boxRightX, boxBottomY) ||
                pointIsInArea(newRightX, newTopY, boxLeftX, boxTopY, boxRightX, boxBottomY) ||
                pointIsInArea(newRightX, newBottomY, boxLeftX, boxTopY, boxRightX, boxBottomY)
            ) {
                return true;
            }
        }

        // check switch collision
        if (
            pointIsInArea(newLeftX, newTopY, switchLeftX, switchTopY, switchRightX, switchBottomY) ||
            pointIsInArea(newLeftX, newBottomY, switchLeftX, switchTopY, switchRightX, switchBottomY) ||
            pointIsInArea(newRightX, newTopY, switchLeftX, switchTopY, switchRightX, switchBottomY) ||
            pointIsInArea(newRightX, newBottomY, switchLeftX, switchTopY, switchRightX, switchBottomY)
        ) {
            return true;
        }

        // check wall collision
        if (
            newTopY <= observation.wallWidth ||
            newBottomY >= observation.wallWidth ||
            newRightX >= observation.wallWidth ||
            newRightX >= observation.wallWidth || (
                newLeftX <= observation.wallWidth &&
                (
                    newBottomY > observation.doorBottom ||
                    newTopY <= observation.doorTop
                )
            )
        ) {
            return true;
        }

        return false;
    }

    @TRADEService
    @Observes({ "willCollide(?direction)" })
    public List<HashMap<Variable, Symbol>> willCollide(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        Symbol directionSymbol = term.getArgs().get(0);
        String direction = directionSymbol.getName();

        if (direction == "UP") {
            if (checkCollision(0, -1)) {
                list.add(new HashMap<>());
            }
        }
        else if (direction == "DOWN") {
            if (checkCollision(0, 1)) {
                list.add(new HashMap<>());
            }
        }
        else if (direction == "LEFT") {
            if (checkCollision(-1, 0)) {
                list.add(new HashMap<>());
            }
        }
        else if (direction == "RIGHT") {
            if (checkCollision(1, 0)) {
                list.add(new HashMap<>());
            }
        }

        return list;
    }

    private boolean isApproximatelyAt(int robotPos, int targetPos) {
        if (robotPos < targetPos - POSITION_TOLERANCE) {
            return false;
        }

        if (robotPos > targetPos + POSITION_TOLERANCE) {
            return false;
        }

        return true;
    }

    @TRADEService
    @Observes({ "isAtSwitch()" })
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

    @TRADEService
    @Observes({ "isInPickupRange()" })
    public List<HashMap<Variable, Symbol>> isInPickupRange(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        if (this.game.observation.isInPickupRange) {
            list.add(new HashMap<>());
        }
        return list;
    }
    
    @TRADEService
    @Observes({ "northOfDoorCenter()" })
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
    
    @TRADEService
    @Observes({ "southOfDoorCenter()" })
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

    @TRADEService
    @Observes({ "eastOfDoor()" })
    public List<HashMap<Variable, Symbol>> eastOfDoor(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();

        if (this.game.observation.robotPos[0] > 0) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @TRADEService
    @Observes({ "isAtDoor()" })
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

    @TRADEService
    @Observes({ "canMoveWest()" })
    public List<HashMap<Variable, Symbol>> canMoveWest(Term term) {
        List<HashMap<Variable, Symbol>> list = new ArrayList<>();
        if (
            this.game.observation.robotPos[0] > this.game.observation.wallWidth
        ) {
            list.add(new HashMap<>());
        }
        return list;
    }

    @Override
    public Justification getObservation() {
        GameAction action = new GetObservation();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveLeft() {
        GameAction action = new Left();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveRight() {
        GameAction action = new Right();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveUp() {
        GameAction action = new Up();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
    
    @Override
    public Justification moveDown() {
        GameAction action = new Down();
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
