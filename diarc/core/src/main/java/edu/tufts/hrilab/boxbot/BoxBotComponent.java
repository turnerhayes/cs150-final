package edu.tufts.hrilab.boxbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.tufts.hrilab.action.justification.ConditionJustification;
import edu.tufts.hrilab.action.justification.Justification;
import edu.tufts.hrilab.diarc.DiarcComponent;
import edu.tufts.hrilab.interfaces.BoxBotSimulatorInterface;
import edu.tufts.hrilab.supermarket.SupermarketObservation;
import edu.tufts.hrilab.util.Util;
import edu.tufts.hrilab.boxbot.actions.Left;
import edu.tufts.hrilab.boxbot.actions.Right;
import edu.tufts.hrilab.boxbot.actions.Up;
import edu.tufts.hrilab.boxbot.actions.Down;
import edu.tufts.hrilab.boxbot.actions.ToggleHold;

public class BoxBotComponent extends DiarcComponent implements BoxBotSimulatorInterface {
    protected int socketPort = 9000;
    protected GamePlay game;
    private static final Logger log = LoggerFactory.getLogger(BoxBotComponent.class);

    public BoxBotComponent() {
    }

    @Override
    protected void init() {
        super.init();
        this.game = new GamePlay(this.socketPort);
        Util.Sleep(500);
    }

    @Override
    public Justification moveLeft() {
        log.info("moveLeft action");
        GameAction action = new Left();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveRight() {
        log.info("moveRight action");
        GameAction action = new Right();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveUp() {
        log.info("moveUp action");
        GameAction action = new Up();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification moveDown() {
        log.info("moveDown action");
        GameAction action = new Down();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }

    @Override
    public Justification toggleHold() {
        log.info("toggleHold action");
        GameAction action = new ToggleHold();
        game.perform(action);
        return new ConditionJustification(action.getSuccess());
    }
}
