/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.boxbot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.thinkingrobots.trade.TRADEService;
import edu.tufts.hrilab.action.annotations.Observes;
import edu.tufts.hrilab.fol.Symbol;
import edu.tufts.hrilab.fol.Variable;
import java.lang.StringBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Contains information about the state of the simulation at a point in time.
 */
public class BoxBotObservation {
  private static final Logger log = LoggerFactory.getLogger(BoxBotObservation.class);

  
  /*************************************
   * FIELDS
   *************************************/

  /**
   * If true, the robot is holding the box.
   */
  public boolean isHoldingBox;
  /**
   * If true, the switch is pressed down.
   */
  public boolean isSwitchPressed;
  /**
   * If true, the box is close enough to the robot to be picked up.
   */
  public boolean isInPickupRange;
  /**
   * The position of the robot, as an (x, y) tuple.
   */
  public int[] robotPos;
  /**
   * The position of the switch, as an (x, y) tuple.
   */
  public int[] switchPos;
  /**
   * The position of the box, as an (x, y) tuple.
   */
  public int[] boxPos;
  /**
   * The width of the robot, in pixels.
   */
  public int robotWidth;
  /**
   * The height of the robot, in pixels.
   */
  public int robotHeight;
  /**
   * The width of the box, in pixels.
   */
  public int boxWidth;
  /**
   * The height of the box, in pixels.
   */
  public int boxHeight;
  /**
   * The width of the switch, in pixels.
   */
  public int switchWidth;
  /**
   * The height of the switch, in pixels.
   */
  public int switchHeight;
  /**
   * The y coordinate of the top of the door.
   */
  public int doorTop;
  /**
   * The y coordinate of the bottom of the door.
   */
  public int doorBottom;
  /**
   * The width of each wall.
   */
  public int wallWidth;

  /*************************************
   * UTILITY FUNCTIONS
   *************************************/

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append('{');
    builder.append("isHoldingBox: ");
    builder.append(this.isHoldingBox);
    builder.append(", ");
    builder.append("isInPickupRange: ");
    builder.append(this.isInPickupRange);
    builder.append(", ");
    builder.append("isSwitchPressed: ");
    builder.append(this.isSwitchPressed);
    builder.append(", ");
    builder.append("robotPos: [");
    builder.append(this.robotPos[0]);
    builder.append(", ");
    builder.append(this.robotPos[1]);
    builder.append("]");
    builder.append(", ");
    builder.append("switchPos: [");
    builder.append(this.switchPos[0]);
    builder.append(", ");
    builder.append(this.switchPos[1]);
    builder.append("]");
    builder.append(", ");
    builder.append("boxPos: [");
    builder.append(this.boxPos[0]);
    builder.append(", ");
    builder.append(this.boxPos[1]);
    builder.append("]");
    builder.append(", ");
    builder.append("robotWidth: ");
    builder.append(this.robotWidth);
    builder.append(", ");
    builder.append("robotHeight: ");
    builder.append(this.robotHeight);
    builder.append(", ");
    builder.append("boxWidth: ");
    builder.append(this.boxWidth);
    builder.append(", ");
    builder.append("boxHeight: ");
    builder.append(this.boxHeight);
    builder.append(", ");
    builder.append("switchWidth: ");
    builder.append(this.switchWidth);
    builder.append(", ");
    builder.append("switchHeight: ");
    builder.append(this.switchHeight);
    builder.append(", ");
    builder.append("doorTop: ");
    builder.append(this.doorTop);
    builder.append(", ");
    builder.append("doorBottom: ");
    builder.append(this.doorBottom);
    builder.append(", ");
    builder.append("wallWidth: ");
    builder.append(this.wallWidth);
    builder.append(", ");
    builder.append('}');

    return builder.toString();
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }
    if (other.getClass() != getClass()) {
      return false;
    }
    BoxBotObservation obs = (BoxBotObservation) other;
    return new EqualsBuilder().append(this.isHoldingBox, obs.isHoldingBox)
            .append(this.isSwitchPressed, obs.isSwitchPressed)
            .append(this.isInPickupRange, obs.isInPickupRange)
            .append(this.robotPos, obs.robotPos)
            .append(this.switchPos, obs.switchPos)
            .append(this.boxPos, obs.boxPos)
            .append(this.robotWidth, obs.robotWidth)
            .append(this.robotHeight, obs.robotHeight)
            .append(this.boxWidth, obs.boxWidth)
            .append(this.boxHeight, obs.boxHeight)
            .append(this.switchWidth, obs.switchWidth)
            .append(this.switchHeight, obs.switchHeight)
            .append(this.doorTop, obs.doorTop)
            .append(this.doorBottom, obs.doorBottom)
            .append(this.wallWidth, obs.wallWidth)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.isHoldingBox)
            .append(this.isSwitchPressed)
            .append(this.isInPickupRange)
            .append(this.robotPos)
            .append(this.switchPos)
            .append(this.boxPos)
            .append(this.robotWidth)
            .append(this.robotHeight)
            .append(this.boxWidth)
            .append(this.boxHeight)
            .append(this.switchWidth)
            .append(this.switchHeight)
            .append(this.doorTop)
            .append(this.doorBottom)
            .append(this.wallWidth)
            .hashCode();
  }
}
