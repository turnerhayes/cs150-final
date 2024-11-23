import time
import random
from typing import Iterable
from direction import Direction
import gymnasium as gym
from player_action import PlayerAction, PlayerActionTable
from game2 import Game, Observation
# from game import Game

MOVEMENT_ACTIONS = [PlayerAction.UP, PlayerAction.DOWN, PlayerAction.RIGHT, PlayerAction.LEFT]


class SimulatorEnv(gym.Env):

    def __init__(self):

        super(SimulatorEnv, self).__init__()

        self.unwrapped.step_count = 0
        self.unwrapped.num_players = 1
        self.unwrapped.game = None

    def step(self, action: PlayerAction):
        done = False
        
        game: Game = self.unwrapped.game

        if action in MOVEMENT_ACTIONS:
            if action == PlayerAction.UP:
                direction = Direction.UP
            elif action == PlayerAction.DOWN:
                direction = Direction.DOWN
            elif action == PlayerAction.RIGHT:
                direction = Direction.RIGHT
            else:
                direction = Direction.LEFT
            game.player_move(direction)
        elif action == PlayerAction.TOGGLE_HOLD:
            game.toggle_holding_item()
        observation = game.observation()
        self.unwrapped.step_count += 1
        if not game.running:
            done = True
        return observation, 0., done, None, None

    def reset(self) -> Observation:
        game = Game()
        self.unwrapped.game = game
        game.set_up()
        self.unwrapped.step_count = 0
        return game.observation()

    def render(self):
        game: Game = self.unwrapped.game
        game.update()


if __name__ == "__main__":
    env = SimulatorEnv()
    env.reset()

    for i in range(100):
        env.step(PlayerAction.RIGHT)
        env.render()
