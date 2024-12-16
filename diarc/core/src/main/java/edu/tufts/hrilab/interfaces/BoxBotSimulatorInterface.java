package edu.tufts.hrilab.interfaces;

import ai.thinkingrobots.trade.TRADEService;

import edu.tufts.hrilab.action.annotations.Action;
import edu.tufts.hrilab.action.annotations.Observes;
import edu.tufts.hrilab.action.justification.Justification;

import edu.tufts.hrilab.fol.Predicate;
import edu.tufts.hrilab.fol.Variable;
import edu.tufts.hrilab.fol.Term;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Symbol;

import java.util.HashMap;
import java.util.List;

public interface BoxBotSimulatorInterface {
    /*************************************
     * OBSERVERS
     *************************************/

    /**
     * Checks whether the robot is currently north of the box.
     * 
     * @param term observer term (unused)
     * @return Empty list if not north of the box, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "northOfBox()" })
    public List<HashMap<Variable, Symbol>> northOfBox(Term term);

    /**
     * Checks whether the robot is currently south of the box.
     * 
     * @param term observer term (unused)
     * @return Empty list if not south of the box, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "southOfBox()" })
    public List<HashMap<Variable, Symbol>> southOfBox(Term term);

    /**
     * Checks whether the robot is currently east of the box.
     * 
     * @param term observer term (unused)
     * @return Empty list if not east of the box, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "eastOfBox()" })
    public List<HashMap<Variable, Symbol>> eastOfBox(Term term);

    /**
     * Checks whether the robot is currently west of the box.
     * 
     * @param term observer term (unused)
     * @return Empty list if not west of the box, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "westOfBox()" })
    public List<HashMap<Variable, Symbol>> westOfBox(Term term);

    /**
     * Checks whether the robot is currently north of the switch.
     * 
     * @param term observer term (unused)
     * @return Empty list if not north of the switch, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "northOfSwitch()" })
    public List<HashMap<Variable, Symbol>> northOfSwitch(Term term);

    /**
     * Checks whether the robot is currently south of the switch.
     * 
     * @param term observer term (unused)
     * @return Empty list if not south of the switch, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "southOfSwitch()" })
    public List<HashMap<Variable, Symbol>> southOfSwitch(Term term);

    /**
     * Checks whether the robot is currently east of the switch.
     * 
     * @param term observer term (unused)
     * @return Empty list if not east of the switch, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "eastOfSwitch()" })
    public List<HashMap<Variable, Symbol>> eastOfSwitch(Term term);

    /**
     * Checks whether the robot is currently west of the switch.
     * 
     * @param term observer term (unused)
     * @return Empty list if not south of the west, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "westOfSwitch()" })
    public List<HashMap<Variable, Symbol>> westOfSwitch(Term term);

    /**
     * Checks whether the robot is currently at the switch.
     * 
     * @param term observer term (unused)
     * @return Empty list if not at the switch, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "isAtSwitch()" })
    public List<HashMap<Variable, Symbol>> isAtSwitch(Term term);

    /**
     * Checks whether the robot is currently in range of where it can pick up
     * the box.
     * 
     * @param term observer term (unused)
     * @return Empty list if can't pick up the box, non-empty list if it can.
     */
    @TRADEService
    @Observes({ "isInPickupRange()" })
    public List<HashMap<Variable, Symbol>> isInPickupRange(Term term);

    /**
     * Checks whether the robot is currently north of the center of the door.
     * 
     * @param term observer term (unused)
     * @return Empty list if not north of the center of the door, non-empty
     * list if it is.
     */
    @TRADEService
    @Observes({ "northOfDoorCenter()" })
    public List<HashMap<Variable, Symbol>> northOfDoorCenter(Term term);

    /**
     * Checks whether the robot is currently south of the center of the door.
     * 
     * @param term observer term (unused)
     * @return Empty list if not south of the center of the door, non-empty
     * list if it is.
     */
    @TRADEService
    @Observes({ "southOfDoorCenter()" })
    public List<HashMap<Variable, Symbol>> southOfDoorCenter(Term term);

   /**
     * Checks whether the robot is currently east of the door.
     * 
     * @param term observer term (unused)
     * @return Empty list if not east of the door, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "eastOfDoor()" })
    public List<HashMap<Variable, Symbol>> eastOfDoor(Term term);

    /**
     * Checks whether the robot is currently at the door.
     * 
     * @param term observer term (unused)
     * @return Empty list if not at the door, non-empty list if it is.
     */
    @TRADEService
    @Observes({ "isAtDoor()" })
    public List<HashMap<Variable, Symbol>> isAtDoor(Term term);

    /**
     * Checks whether the robot is capable of moving west.
     * 
     * @param term observer term (unused)
     * @return Empty list if robot can't move west, non-empty list if it can.
     */
    @TRADEService
    @Observes({ "canMoveWest()" })
    public List<HashMap<Variable, Symbol>> canMoveWest(Term term);

    /*************************************
     * ACTIONS
     *************************************/

    /**
     * Action to request the current state of the simulation.
     * 
     * @return justification describing the result of the request.
     */
    @TRADEService
    @Action
    Justification getObservation();
    
    /**
     * Action to request to move the robot left.
     * 
     * @return justification describing the result of the request.
     */
    @TRADEService
    @Action
    Justification moveLeft();
    
    /**
     * Action to request to move the robot right.
     * 
     * @return justification describing the result of the request.
     */
    @TRADEService
    @Action
    Justification moveRight();
    
    /**
     * Action to request to move the robot up.
     * 
     * @return justification describing the result of the request.
     */
    @TRADEService
    @Action
    Justification moveUp();
    
    /**
     * Action to request to move the robot down.
     * 
     * @return justification describing the result of the request.
     */
    @TRADEService
    @Action
    Justification moveDown();
    
    /**
     * Action to request the robot to release or pick up an object.
     * 
     * @return justification describing the result of the request.
     */
    @TRADEService
    @Action
    Justification toggleHold();
}
