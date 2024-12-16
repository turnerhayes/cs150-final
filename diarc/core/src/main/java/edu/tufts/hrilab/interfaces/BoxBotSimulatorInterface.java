package edu.tufts.hrilab.interfaces;

import ai.thinkingrobots.trade.TRADEService;
import edu.tufts.hrilab.action.annotations.Action;
import edu.tufts.hrilab.action.justification.Justification;

public interface BoxBotSimulatorInterface {
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
