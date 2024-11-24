/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.boxbot.actions;

public class Up extends Active {

    public Up() { this.maxResponseWait = 1000; }

    @Override
    public String getCommand() {
        return "NORTH";
    }
}
