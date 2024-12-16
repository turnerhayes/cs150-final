import edu.tufts.hrilab.fol.Predicate;
import java.lang.Boolean;

() = moveToBox["?actor moves to the box"]() {
    Predicate !query;
    Predicate !northQuery;
    Predicate !southQuery;
    Predicate !westQuery;
    Predicate !eastQuery;

    conditions : {
        pre infer: ~atBox();
    }
    effects : {
        success: atBox();
        success: isInPickupRange();
        success: ~atSwitch();
        success: ~atDoor();
    }

    op: log(info, ">> moving to box");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "isInPickupRange()");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "northOfBox()");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "southOfBox()");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "westOfBox()");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "eastOfBox()");

    while(~obs:!query) {
        if(obs:!westQuery) {
            act:moveRight();
        }
        elseif(obs:!northQuery) {
            act:moveDown();
        }
        elseif(obs:!eastQuery) {
            act:moveLeft();
        }
        elseif(obs:!southQuery) {
            act:moveUp();
        }
    }
}

() = pickUpBox["?actor picks up the box"]() {
    conditions: {
        pre: isInPickupRange();
        pre: ~isHoldingBox();
    }
    effects: {
        success: isHoldingBox();
    }

    act: toggleHold;
}

() = putDownBox["?actor puts down the box"]() {
    conditions: {
        pre: isHoldingBox();
    }
    effects: {
        success: ~isHoldingBox();
    }

    act: toggleHold;
}

() = moveToSwitch["?actor moves to the light switch"]() {
    Predicate !query;
    Predicate !northQuery;
    Predicate !southQuery;
    Predicate !westQuery;
    Predicate !eastQuery;

    conditions : {
        pre : ~atSwitch();
    }
    effects : {
        success : atSwitch();
        success : ~atBox();
        success : ~atDoor();
    }

    op: log(info, ">> moving to light switch");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "isAtSwitch()");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "northOfSwitch()");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "southOfSwitch()");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "westOfSwitch()");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "eastOfSwitch()");

    while(~obs:!query) {
        if(obs:!westQuery) {
            try {
                op:log(info, "MOVED RIGHT");
                act:moveRight();
            }
            catch(FAIL) {
                op:log(info, "FAILED TO MOVE RIGHT");
                if(obs:!northQuery) {
                    try {
                        act:moveDown();
                    }
                    catch(FAIL) {
                        op:log(info, "    FAILED TO MOVE DOWN");
                        if(obs:!eastQuery) {
                            try {
                                act:moveLeft();
                            }
                            catch(FAIL) {
                                op:log(info, "        FAILED TO MOVE LEFT");
                                if(obs:!southQuery) {
                                    act:moveUp();
                                }
                            }
                        }
                    }
                }
            }
        }
        elseif(obs:!northQuery) {
            try {
                act:moveDown();
            }
            catch(FAIL) {
                if(obs:!eastQuery) {
                    try {
                        act:moveLeft();
                    }
                    catch(FAIL) {
                        if(obs:!southQuery) {
                            act:moveUp();
                        }
                    }
                }
            }
        }
        elseif(obs:!eastQuery) {
            try {
                act:moveLeft();
            }
            catch(FAIL) {
                if(obs:!southQuery) {
                    act:moveUp();
                }
            }
        }
        elseif(obs:!southQuery) {
            act:moveUp();
        }
    }
}

() = moveToDoor["?actor moves to the door"]() {
    Predicate !query;
    Predicate !northQuery;
    Predicate !southQuery;
    Predicate !eastQuery;
    Predicate !canMoveWestQuery;

    conditions : {
        pre : ~atDoor();
    }
    effects : {
        success : atDoor();
        success : ~atSwitch();
        success : ~atBox();
    }

    op: log(info, ">> moving to the door");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "isAtDoor()");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "northOfDoorCenter()");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "southOfDoorCenter()");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "eastOfDoor()");
    !canMoveWestQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "canMoveWest()");

    while(~obs:!query) {
        if(obs:!southQuery) {
            act:moveUp();
        }
        elseif(obs:!eastQuery) {
            if(obs:!canMoveWestQuery) {
                act:moveLeft();
            }
        }
        elseif(obs:!northQuery) {
            act:moveDown();
        }
    }
}
