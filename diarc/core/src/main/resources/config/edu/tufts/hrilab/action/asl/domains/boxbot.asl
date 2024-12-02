
() = initializeagent[""]() {
    edu.tufts.hrilab.fol.Variable !x;
    edu.tufts.hrilab.fol.Predicate !pred;

    obs:is_holding_box(?actor);
    obs:can_grab_box(?actor);

    op:log(info, "Finished observing for ?actor");

}

() = moveToBox["?actor moves to the box"]() {
    edu.tufts.hrilab.fol.Predicate !query;

    conditions : {
        pre obs : not(is_holding_box(?actor));
        pre obs : not(can_grab_box(?actor));
    }
    effects : {
        success obs : can_grab_box(?actor);
    }
    op: log(info, ">> moving to box");

    !query = op:invokeStaticMethod("edu.tufts.hrilab.fol.Factory", "createPredicate", "can_grab_box(?actor)");

    while(~obs:!query) {
        act:moveRight();
    }
}