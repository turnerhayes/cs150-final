from enum import IntEnum


class PlayerAction(IntEnum):
    NORTH = 1,
    SOUTH = 2,
    EAST = 3,
    WEST = 4,
    TOGGLE_HOLD = 5,
    GET_OBSERVATION = 6,
    RESET = 7,

PlayerActionTable = {
    "NORTH" : PlayerAction.NORTH,
    "SOUTH" : PlayerAction.SOUTH,
    "EAST" : PlayerAction.EAST,
    "WEST" : PlayerAction.WEST,
    "TOGGLE_HOLD": PlayerAction.TOGGLE_HOLD,
    "GET_OBSERVATION": PlayerAction.GET_OBSERVATION,
    "RESET": PlayerAction.RESET,
}
