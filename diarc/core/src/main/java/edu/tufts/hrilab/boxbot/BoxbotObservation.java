/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.boxbot;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BoxbotObservation {
  public boolean is_holding_box;
  public boolean is_switch_pressed;
  public boolean is_in_pickup_range;
  public int[] robot_pos;
  public int[] switch_pos;
  public int[] box_pos;
  public int interactive_stage;
  public int total_stages;

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
    BoxbotObservation obs = (BoxbotObservation) other;
    return new EqualsBuilder().append(this.is_holding_box, obs.is_holding_box)
            .append(this.is_switch_pressed, obs.is_switch_pressed)
            .append(this.is_in_pickup_range, obs.is_in_pickup_range)
            .append(this.robot_pos, obs.robot_pos)
            .append(this.switch_pos, obs.switch_pos)
            .append(this.box_pos, obs.box_pos)
            .append(this.interactive_stage, obs.interactive_stage)
            .append(this.total_stages, obs.total_stages)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.is_holding_box)
            .append(this.is_switch_pressed)
            .append(this.is_in_pickup_range)
            .append(this.robot_pos)
            .append(this.switch_pos)
            .append(this.box_pos)
            .append(this.interactive_stage)
            .append(this.total_stages).hashCode();
  }
}
