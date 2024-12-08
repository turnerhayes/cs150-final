() = moveToBox["?actor moves to the box"]() {
    edu.tufts.hrilab.fol.Predicate !query;
    edu.tufts.hrilab.fol.Predicate !northQuery;
    edu.tufts.hrilab.fol.Predicate !southQuery;
    edu.tufts.hrilab.fol.Predicate !westQuery;
    edu.tufts.hrilab.fol.Predicate !eastQuery;

    conditions : {
    }
    effects : {
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

() = moveToSwitch["?actor moves to the light switch"]() {
    edu.tufts.hrilab.fol.Predicate !query;
    edu.tufts.hrilab.fol.Predicate !northQuery;
    edu.tufts.hrilab.fol.Predicate !southQuery;
    edu.tufts.hrilab.fol.Predicate !westQuery;
    edu.tufts.hrilab.fol.Predicate !eastQuery;

    conditions : {
    }
    effects : {
    }

    op: log(info, ">> moving to light switch");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "isAtSwitch()");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "northOfSwitch()");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "southOfSwitch()");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "westOfSwitch()");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "eastOfSwitch()");

    op: log(info, "about to loop");
    while(~obs:!query) {
        op: log(info, "in loop");
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

() = moveToDoor["?actor moves to the door"]() {
    edu.tufts.hrilab.fol.Predicate !query;
    edu.tufts.hrilab.fol.Predicate !northQuery;
    edu.tufts.hrilab.fol.Predicate !southQuery;
    edu.tufts.hrilab.fol.Predicate !eastQuery;

    conditions : {
    }
    effects : {
    }

    op: log(info, ">> moving to the door");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "isAtDoor()");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "northOfDoorTop()");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "southOfDoorBottom()");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "eastOfDoor()");

    while(~obs:!query) {
        if(obs:!southQuery) {
            act:moveUp();
        }
        elseif(obs:!northQuery) {
            act:moveDown();
        }
        elseif(obs:!eastQuery) {
            act:moveLeft();
        }
    }
}
