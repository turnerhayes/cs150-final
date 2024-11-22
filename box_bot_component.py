from jpype import JImplements, JOverride
from edu.tufts.hrilab.interfaces import BoxBotSimulatorInterface

@JImplements(BoxBotSimulatorInterface)
class BoxBotSimulatorInterface:
    @JOverride
    def moveLeft(self):
        pass

    @JOverride
    def moveRight(self):
        pass

    @JOverride
    def moveUp(self):
        pass

    @JOverride
    def moveDown(self):
        pass