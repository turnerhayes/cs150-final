import edu.tufts.hrilab.boxbot.Direction;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Predicate;
import java.lang.String;


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

    Symbol !up = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "UP");
    Symbol !down = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "DOWN");
    Symbol !left = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "LEFT");
    Symbol !right = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "RIGHT");
    Predicate !collideUp = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!up)");
    Predicate !collideDown = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!down)");
    Predicate !collideLeft = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!left)");
    Predicate !collideRight = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!right)");


    op: log(info, ">> moving to box");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "isInPickupRange()");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "northOfBox()");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "southOfBox()");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "westOfBox()");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "eastOfBox()");

    while(~obs:!query) {
        if(obs:!westQuery && ~obs:!collideRight) {
            act:moveRight();
        }
        elseif(obs:!northQuery && ~obs:collideDown) {
            act:moveDown();
        }
        elseif(obs:!eastQuery && ~obs:collideLeft) {
            act:moveLeft();
        }
        elseif(obs:!southQuery && ~obs:collideUp) {
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
        success: isSwitchPressed();
    }

    act: toggleHold;
}

() = fetchBox["?actor goes to box and picks it up"]() {
    conditions: {
        pre: ~isHoldingBox();
        pre: ~atBox();
    }
    effects: {
        success: isHoldingBox();
        success: atBox();
        success: ~atSwitch();
        success: ~atDoor();
    }

    act: moveToBox();
    act: pickUpBox();
}

() = putDownBox["?actor puts down the box"]() {
    conditions: {
        pre: isHoldingBox();
    }
    effects: {
        success: ~isHoldingBox();
        success: isSwitchPressed();
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

    Symbol !up = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "UP");
    Symbol !down = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "DOWN");
    Symbol !left = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "LEFT");
    Symbol !right = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createSymbol", "RIGHT");
    Predicate !collideUp = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!up)");
    Predicate !collideDown = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!down)");
    Predicate !collideLeft = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!left)");
    Predicate !collideRight = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "willCollide(!right)");

    while(~obs:!query) {
        if(obs:!westQuery && ~obs:!collideRight) {
            act:moveRight();
        }
        elseif(obs:!northQuery && ~obs:!collideDown) {
            act:moveDown();
        }
        elseif(obs:!eastQuery && ~obs:!collideLeft) {
            act:moveLeft();
        }
        elseif(obs:!southQuery && ~obs:!collideUp) {
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
