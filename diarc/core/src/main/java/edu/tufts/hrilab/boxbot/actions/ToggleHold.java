/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.boxbot.actions;

public class ToggleHold extends Active {
    public ToggleHold() { this.maxResponseWait = 1000; }

    @Override
    public String getCommand() {
        return "TOGGLE_HOLD";
    }
}
