/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */
package edu.tufts.hrilab.boxbot;

import edu.tufts.hrilab.action.state.StateMachine;
import edu.tufts.hrilab.action.annotations.Action;
import edu.tufts.hrilab.action.justification.Justification;

import ai.thinkingrobots.trade.*;
import edu.tufts.hrilab.fol.Symbol;

public interface BoxbotInterface {
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
