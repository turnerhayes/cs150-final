package edu.tufts.hrilab.interfaces;

import ai.thinkingrobots.trade.TRADEService;
import edu.tufts.hrilab.action.annotations.Action;
import edu.tufts.hrilab.action.justification.Justification;

public interface BoxBotSimulatorInterface {
    @TRADEService
    @Action
    Justification moveLeft();

    @TRADEService
    @Action
    Justification moveRight();

    @TRADEService
    @Action
    Justification moveUp();

    @TRADEService
    @Action
    Justification moveDown();

    @TRADEService
    @Action
    Justification toggleHold();
}
