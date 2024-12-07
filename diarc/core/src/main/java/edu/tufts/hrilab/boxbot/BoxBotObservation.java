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

  
  @TRADEService
  @Observes({"isInPickupRangeObs()"})
  public boolean isInPickupRangeObs() {
    log.info("IS IN PICKUP RANGE+++++++++++++++++++++++");
    return false;
    // return this.isInPickupRange;
  }
  
  @TRADEService
  @Observes({"westOfBox()"})
  public boolean westOfBox() {
    return this.boxbot.position[0] <= this.box.position[0];
  }
  
  
  @TRADEService
  @Observes({"eastOfBox()"})
  public boolean eastOfBox() {
    return this.boxbot.position[0] + this.boxbot.width >= this.box.position[0] + this.box.width;
  }
  
  @TRADEService
  @Observes({"northOfBox()"})
  public List<HashMap<Variable, Symbol>> northOfBox() {
    log.info("called northOfBox");
    List<HashMap<Variable, Symbol>> list = new java.util.ArrayList<>();
    list.add(new HashMap<>());
    return list;
    // return this.boxbot.position[1] <= this.box.position[1];
  }
  
  @TRADEService
  @Observes({"southOfBox()"})
  public boolean southOfBox() {
    return this.boxbot.position[1] + this.boxbot.height >= this.box.position[1] + this.box.height;
  }
}
