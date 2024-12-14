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

public class BoxBotObservation {
  private static final Logger log = LoggerFactory.getLogger(BoxBotObservation.class);

  public boolean isHoldingBox;
  public boolean isSwitchPressed;
  public boolean isInPickupRange;
  public int[] robotPos;
  public int[] switchPos;
  public int[] boxPos;
  public int robotWidth;
  public int robotHeight;
  public int robotSpeed;
  public int boxWidth;
  public int boxHeight;
  public int switchWidth;
  public int switchHeight;
  public int doorTop;
  public int doorBottom;
  public int wallWidth;

  
  public InteractiveObject lightSwitch;
  public InteractiveObject box;
  public InteractiveObject door;
  public Robot boxbot;

  public static boolean overlap(double x1, double y1, double width1, double height1,
                                double x2, double y2, double width2, double height2) {
    return !(x1 > x2 + width2 || x2 > x1 + width1 || y1 > y2 + height2 || y2 > y1 + height1);
  }

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
    builder.append("robotSpeed: ");
    builder.append(this.robotSpeed);
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
            .append(this.robotSpeed, obs.robotSpeed)
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
            .append(this.robotSpeed)
            .append(this.boxWidth)
            .append(this.boxHeight)
            .append(this.switchWidth)
            .append(this.switchHeight)
            .append(this.doorTop)
            .append(this.doorBottom)
            .append(this.wallWidth)
            .hashCode();
  }
  
  public class InteractiveObject {
    public double width;
    public double height;

    public double[] position;

    public boolean collision(InteractiveObject other, double x, double y) {
      return overlap(this.position[0], this.position[1], this.width, this.height,
          x, y, other.width, other.height);
    }
    

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }
  }

  public enum Direction {
    NORTH(0),
    EAST(2),
    SOUTH(1),
    WEST(3);

    public final int index;

    Direction(int index) {
      this.index = index;
    }

    public static Direction fromIndex(int index) {
      for (Direction dir : Direction.values()) {
        if (dir.index == index) {
          return dir;
        }
      }
      return null;
    }

    public static Direction cw(Direction init) {
      return Direction.values()[(init.ordinal() + 1) % 4];
    }

    public static Direction ccw(Direction init) {
      return Direction.values()[(init.ordinal() + 3) % 4];
    }
  }

  public boolean canInteract(InteractiveObject obj) {
    double range = 0.5;
    double x = this.boxbot.position[0];
    double y = this.boxbot.position[1];
    switch (this.boxbot.direction) {
      case 0: // North
        return obj.collision(this.boxbot, x, y - range);
      case 1: // South
        return obj.collision(this.boxbot, x, y + range);
      case 2: // East
        return obj.collision(this.boxbot, x + range, y);
      case 3: // West
        return obj.collision(this.boxbot, x - range, y);
    }
    return false;
  }

  public class Robot extends InteractiveObject {
    public boolean is_holding_box;
    public int direction;
  }
  
  public boolean is_holding_box() {
    return this.boxbot.is_holding_box;
  }
}
