
() = initializeagent[""]() {
    edu.tufts.hrilab.fol.Variable !x;
    edu.tufts.hrilab.fol.Predicate !pred;

    obs:is_holding_box(?actor);
    obs:can_grab_box(?actor);

    op:log(info, "Finished observing for ?actor");

}

() = moveToBox["?actor moves to the box"]() {
    edu.tufts.hrilab.fol.Predicate !query;
    edu.tufts.hrilab.fol.Predicate !northQuery;
    edu.tufts.hrilab.fol.Predicate !southQuery;
    edu.tufts.hrilab.fol.Predicate !westQuery;
    edu.tufts.hrilab.fol.Predicate !eastQuery;
    

    conditions : {
        pre obs : not(is_holding_box(?actor));
        pre obs : not(can_grab_box(?actor));
    }
    effects : {
        success obs : can_grab_box(?actor);
    }
    op: log(info, ">> moving to box");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "can_grab_box(?actor)");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "north_of(?actor, ?box)");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "south_of(?actor, ?box)");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "west_of(?actor, ?box)");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "east_of(?actor, ?box)");

    while(~obs:!query) {
        if(obs:!northQuery) {
            act:moveDown();
        }
        elseif(obs:!southQuery) {
            act:moveUp();
        }
        elseif(obs:!westQuery) {
            act:moveRight();
        }
        elseif(obs:!eastQuery) {
            act:moveLeft();
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
        pre obs : not(is_at_switch(?actor);
    }
    effects : {
        success obs : is_at_switch(?actor);
    }
    op: log(info, ">> moving to light switch");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "is_at_switch(?actor)");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "north_of(?actor, ?switch)");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "south_of(?actor, ?switch)");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "west_of(?actor, ?switch)");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "east_of(?actor, ?switch)");

    while(~obs:!query) {
        if(obs:!northQuery) {
            act:moveDown();
        }
        elseif(obs:!southQuery) {
            act:moveUp();
        }
        elseif(obs:!westQuery) {
            act:moveRight();
        }
        elseif(obs:!eastQuery) {
            act:moveLeft();
        }
    }
}

() = moveToDoor["?actor moves to the door"]() {
    edu.tufts.hrilab.fol.Predicate !query;
    edu.tufts.hrilab.fol.Predicate !northQuery;
    edu.tufts.hrilab.fol.Predicate !southQuery;
    edu.tufts.hrilab.fol.Predicate !westQuery;
    edu.tufts.hrilab.fol.Predicate !eastQuery;

    conditions : {
        pre obs : not(is_at_door(?actor);
    }
    effects : {
        success obs : is_at_door(?actor);
    }
    op: log(info, ">> moving to the door");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "is_at_door(?actor)");
    !northQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "north_of(?actor, ?door)");
    !southQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "south_of(?actor, ?door)");
    !westQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "west_of(?actor, ?door)");
    !eastQuery = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "east_of(?actor, ?door)");

    while(~obs:!query) {
        if(obs:!northQuery) {
            act:moveDown();
        }
        elseif(obs:!southQuery) {
            act:moveUp();
        }
        elseif(obs:!westQuery) {
            act:moveRight();
        }
        elseif(obs:!eastQuery) {
            act:moveLeft();
        }
    }
}
