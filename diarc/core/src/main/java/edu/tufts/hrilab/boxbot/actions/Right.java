/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.boxbot.actions;

public class Right extends Active {
    public Right() { this.maxResponseWait = 1000; }

    @Override
    public String getCommand() {
        return "RIGHT";
    }
}
