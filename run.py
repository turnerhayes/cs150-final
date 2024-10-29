#!/usr/bin/python3

from simulator import Simulator


def run():
    sim = Simulator()
    
    sim.add_object("light_switch")
    sim.add_object("box", can_pick_up=False)
    print("simulator objects: %s" % sim.list_objects())
    

run()
