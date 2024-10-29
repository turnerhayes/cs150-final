from typing import Dict

class Object:
    def __init__(self, name: str, can_pick_up: bool = False) -> None:
        self.name = name
        self.can_pick_up = can_pick_up
    
    def __str__(self) -> str:
        return self.name
    
    def __repr__(self) -> str:
        return "Object(name=\"%s\", can_pick_up=%s)" % (
            self.name,
            self.can_pick_up
        )

class Environment:
    def __init__(self) -> None:
        self.objects: Dict[str, Object] = {}
        
    def add_object(self, object: Object) -> None:
        self.objects[object.name] = object
    
    def list_objects(self) -> list[Object]:
        return [obj for obj in self.objects.values()]
    
    def get_object(self, name: str) -> Object|None:
        return self.objects.get(name)
    
class ActionError(Exception):
    def __init__(self, msg: str) -> None:
        self.__init__(msg=msg)


class Simulator:
    def __init__(self) -> None:
        self.environment = Environment()
    
    def add_object(self, name: str, can_pick_up: bool = False) -> None:
        self.environment.add_object(Object(
            name=name,
            can_pick_up=can_pick_up
        ))
    
    def list_objects(self) -> list[Object]:
        return self.environment.list_objects()
    
    def pick_up_object(self, name: str) -> None:
        obj = self.environment.get_object(name)
        if obj is None:
            raise ActionError("Cannot pick up %s: does not exist" % name)
        
        if not obj.can_pick_up:
            raise ActionError("Cannot pick up %s: can't be picked up")
        

