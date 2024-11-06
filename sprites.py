import os
import pygame
import math
from typing import Union, Tuple

class _SimulationSprite(pygame.sprite.Sprite):
    def __init__(self, img_path: str, size: Union[int,Tuple[int,int]]) -> None:
        if isinstance(size, int):
            size = (size, size)
            
        pygame.sprite.Sprite.__init__(self)
        self.image = pygame.transform.scale(
            pygame.image.load(
                img_path
            ).convert_alpha(),
            size
        )
        
        self.rect = self.image.get_rect()

class Robot(_SimulationSprite):
    def __init__(self, robot_size: int) -> None:
        _SimulationSprite.__init__(
            self,
            img_path=os.path.join(os.path.dirname(__file__), "./robot.png"),
            # Image is 188px x 206px; maintain aspect ratio with sortest side
            # set to robot_size 
            size=(math.ceil(206 * (robot_size/188)), robot_size)
        )

class Box(_SimulationSprite):
    def __init__(self, box_size: int) -> None:
        _SimulationSprite.__init__(
            self,
            img_path=os.path.join(os.path.dirname(__file__), "./box.png"),
            # Image is 59px x 54px; maintain aspect ratio with sortest side
            # set to box_size
            size=(box_size, math.ceil(59 * (box_size/54)))
        )