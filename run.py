#!/usr/bin/python3

from simulator import Simulator


def run():
    sim = Simulator()
    
    sim.add_object("light_switch")
    sim.add_object("box")
    print("simulator objects: %s" % sim.list_objects())
    

run()
