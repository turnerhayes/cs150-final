class Object:
    def __init__(self, name: str) -> None:
        self.name = name
    
    def __str__(self) -> str:
        return self.name

class Environment:
    def __init__(self) -> None:
        self.objects: list[Object] = []
        
    def add_object(self, object: Object) -> None:
        self.objects.append(object)
    
    def list_objects(self) -> list[Object]:
        return self.objects

class Simulator:
    def __init__(self) -> None:
        self.environment = Environment()
    
    def add_object(self, name: str) -> None:
        self.environment.add_object(Object(name))
    
    def list_objects(self) -> list[Object]:
        return self.environment.list_objects()

