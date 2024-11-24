from enum import Enum, IntEnum


class PlayerAction(IntEnum):
    UP = 1,
    DOWN = 2,
    RIGHT = 3,
    LEFT = 4,
    TOGGLE_HOLD = 5

PlayerActionTable = {
    "UP" : PlayerAction.UP,
    "DOWN" : PlayerAction.DOWN,
    "RIGHT" : PlayerAction.RIGHT,
    "LEFT" : PlayerAction.LEFT,
    "TOGGLE_HOLD": PlayerAction.TOGGLE_HOLD,
}
