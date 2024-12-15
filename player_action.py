from enum import IntEnum


class PlayerAction(IntEnum):
    UP = 1,
    DOWN = 2,
    RIGHT = 3,
    LEFT = 4,
    TOGGLE_HOLD = 5,
    GET_OBSERVATION = 6,
    RESET = 7,

PlayerActionTable = {
    "UP" : PlayerAction.UP,
    "DOWN" : PlayerAction.DOWN,
    "RIGHT" : PlayerAction.RIGHT,
    "LEFT" : PlayerAction.LEFT,
    "TOGGLE_HOLD": PlayerAction.TOGGLE_HOLD,
    "GET_OBSERVATION": PlayerAction.GET_OBSERVATION,
    "RESET": PlayerAction.RESET,
}
