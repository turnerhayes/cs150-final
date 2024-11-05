import os
import pygame

class _SimulationSprite(pygame.sprite.Sprite):
    def __init__(self, img_path: str, size: int) -> None:
        pygame.sprite.Sprite.__init__(self)
        img = pygame.image.load(
                img_path
            )
        print("img: %r" % img)
        self.image = pygame.transform.scale(
            img.convert_alpha(),
            (size, size)
        )
        
        self.rect = self.image.get_rect()

class Robot(_SimulationSprite):
    def __init__(self, robot_size: int) -> None:
        _SimulationSprite.__init__(
            self,
            img_path=os.path.join(os.path.dirname(__file__), "./robot.png"),
            size=robot_size
        )

class Box(_SimulationSprite):
    def __init__(self, box_size: int) -> None:
        _SimulationSprite.__init__(
            self,
            img_path=os.path.join(os.path.dirname(__file__), "./box.png"),
            size=box_size
        )