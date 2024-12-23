from typing import Tuple, Union, TypedDict
import math

from direction import Direction
from sprites import Robot, Box
from shapes import Circle, calculate_overlap_percentage, BoxShape, RobotShape
from colors import RED, BLACK, WHITE, GRAY

import pygame

Position = Tuple[int, int]

class Observation(TypedDict):
    isHoldingBox: bool
    robotPos: Position
    switchPos: Position
    isSwitchPressed: bool
    boxPos: Union[Position, None]
    isInPickupRange: bool
    robotWidth: int
    robotHeight: int
    boxWidth: int
    boxHeight: int
    switchWidth: int
    switchHeight: int
    doorTop: int
    doorBottom: int
    wallWidth: int

DIRECTION_VECTOR = {
    Direction.NORTH: (0, -1),
    Direction.SOUTH: (0, 1),
    Direction.EAST: (1, 0),
    Direction.WEST: (-1, 0)
}

class Game:
    def __init__(self) -> None:
        self.robot_speed = 5
        self.pickup_range = 50
        self.wall_width = 10
        self.box_width = 40
        self.box_height = math.ceil(59 * (self.box_width/54))
        self.robot_height = 40
        self.robot_width = math.ceil(206 * (self.robot_height/188))
        self.switch_width = 20
        self.switch_height = self.switch_width
        self._box: Union[Box, None] = None
        self._robot: Union[Robot, None] = None
        
        self.is_holding_box = False
        
        # Set screen dimensions
        self.width, self.height = 800, 600
        self.robot_pos: Position = (self.width // 4, self.height // 2)
        self.switch_pos = (self.width - 100, self.height - 100)
        self.box_pos = (self.width // 2, self.height // 2)
        
        self.switch = Circle(center=self.switch_pos, radius=self.switch_width)
        
        # Define doorway parameters
        self.doorway_height = 100
        self.doorway_pos = (0, self.height // 2 - self.height // 2)
        
        self.running = False
        
        pygame.init()
        pygame.display.set_caption("BoxBot Environment")
        self.screen = pygame.display.set_mode((self.width, self.height))
        self.clock = pygame.time.Clock()
        # Define fonts
        self.font = pygame.font.Font(None, 36)
        
    def reset(self) -> None:
        self.robot_pos: Position = (self.width // 4, self.height // 2)
        self.box_pos = (self.width // 2, self.height // 2)
        self.is_holding_box = False
        
    def set_up(self) -> None:
        self.running = True
        
    def update(self) -> None:
        # Frame rate control
        self.clock.tick(30)

        self._draw_background()

        self._draw_walls()
        
        self._draw_switch()
        
        self._draw_robot()

        self._draw_box()

        self._draw_status_text()
        

        # Update display
        pygame.display.flip()
        
    def _draw_background(self) -> None:
        # Drawing background (change based on light)
        light_on = not self.is_box_on_switch()
        if light_on:
            self.screen.fill(WHITE)
        else:
            self.screen.fill(GRAY)

    def _draw_walls(self) -> None:
        # Draw the walls (except for the doorway)
        pygame.draw.rect(self.screen, BLACK, (0, 0, self.width, self.wall_width))  # Top wall
        pygame.draw.rect(self.screen, BLACK, (0, self.height - self.wall_width, self.width, self.wall_width))  # Bottom wall
        pygame.draw.rect(self.screen, BLACK, (self.width - self.wall_width, 0, self.wall_width, self.height))  # Right wall
        
        # Draw the doorway
        pygame.draw.rect(self.screen, BLACK, (0, 0, self.wall_width, self.doorway_pos[1]))  # Left wall above doorway
        pygame.draw.rect(
            self.screen,
            BLACK,
            (
                0,
                self.doorway_pos[1] + self.doorway_height,
                self.wall_width,
                self.height - (self.doorway_pos[1] + self.doorway_height)
            )
        )  # Left wall below doorway
    
    def _draw_robot(self) -> None:
        # Draw the robot
        robot = self._get_robot()
        self.screen.blit(robot.image, (self.robot_pos[0], self.robot_pos[1]), robot.rect)
        
    def _draw_box(self) -> None:
        if self.is_holding_box:
            self.box_pos = (self.robot_pos[0] + self.robot_width, self.robot_pos[1])
        # Draw the box
        box = self._get_box()
        self.screen.blit(box.image, self.box_pos, box.rect)
        
    def _draw_switch(self) -> None:
        # Draw the switch
        pygame.draw.circle(self.screen, BLACK, self.switch["center"], self.switch["radius"])
    
    def _draw_status_text(self) -> None:
        light_on = not self.is_box_on_switch()
        # Display status
        status_text = self.font.render(f"Lights {'ON' if light_on else 'OFF'}", True, BLACK if light_on else WHITE)
        self.screen.blit(status_text, (10, 10))

    def _can_pickup_box(self) -> bool:
        if self.is_holding_box:
            return False
        
        box = self._get_box()
        robot = self._get_robot()
        
        overlap_pickup = calculate_overlap_percentage(
            Circle(
                center=(
                    self.box_pos[0] + box.rect.width // 2,
                    self.box_pos[1] + box.rect.height // 2
                ),
                radius=self.pickup_range
            ),
            RobotShape(
                robot=robot,
                topLeft=self.robot_pos
            )
        )
        return overlap_pickup > 0
    
    def player_move(self, action: Direction) -> None:
        (x1, y1) = DIRECTION_VECTOR[action]

        return self.move_robot([self.robot_speed * x1, self.robot_speed * y1])
        
    """
    Makes the robot pick up the box if it can.
    
    Returns:
        True if the robot picked up the box, False if it was unable to pick it
        up for any reason
    """
    def grab_item(self) -> bool:
        if not self._can_pickup_box():
            return False
        
        self.is_holding_box = True
        return True
    
    def release_item(self) -> bool:
        if not self.is_holding_box:
            return False
        
        self.is_holding_box = False
        return True
    
    def toggle_holding_item(self) -> bool:
        if self.is_holding_box:
            return self.release_item()
        else:
            return self.grab_item()
    
    def collide(self, pos: Position) -> bool:
        robot = self._get_robot()
        box = self._get_box()
        
        if not self.is_holding_box:
            if calculate_overlap_percentage(
                RobotShape(
                    robot=robot,
                    topLeft=pos
                ),
                BoxShape(
                    box=box,
                    topLeft=self.box_pos
                )
            ) > 0:
                return True
        return False
    
    def hits_wall(self, pos: Position) -> bool:
        robot_left = self.robot_pos[0]
        robot_right = self.robot_pos[0] + self.robot_width
        
        if self.is_holding_box:
            robot_right += self.box_width
            

        if robot_left <= self.wall_width:
            # Hits west wall
            return True
        if robot_right >= (self.width - self.wall_width):
            # Hits east wall
            return True
        if pos[1] <= self.wall_width:
            # Hits north wall
            return True
        if pos[1] + self.robot_height >= (self.height - self.wall_width):
            # Hits south wall
            return True
        
        return False
    
    # moves robot
    def move_robot(self, position_change) -> bool:
        new_position = (self.robot_pos[0] + position_change[0], self.robot_pos[1] + position_change[1])

        if self.collide(new_position) or self.hits_wall(new_position):
            return False

        self.robot_pos = new_position
        return True
    
    """
    Getter for the box sprite. This is used in place of directly accessing the field
    because there is a timing issue in constructing the sprite; it seems to depend on some
    state of Pygame being setup so that it can load the image file that does not seem to
    be true when the Game object is being instantiated (or when its static fields are
    instantiated).
    """
    def _get_box(self) -> Box:
        if (not self._box):
            self._box = Box((self.box_width, self.box_height))
        return self._box
    
    """
    See #_get_box() for an explanation of this.
    """
    def _get_robot(self) -> Robot:
        if (not self._robot):
            self._robot = Robot((self.robot_width, self.robot_height))
        return self._robot

    """
    Determines whether the box is currently on top of the switch (and not
    being held by the robot)
    
    Returns:
        True if the box is on top of the switch and not being held, False
        otherwise
    """
    def is_box_on_switch(self) -> bool:
        box = self._get_box()
        if self.is_holding_box:
            return False
        overlap = calculate_overlap_percentage(
            self.switch,
            BoxShape(
                box=box,
                topLeft=self.box_pos
            )
        ) >= 40
        return overlap
    
    def observation(self) -> Observation:
        # self.switch_pos is the center of the switch circle; to get the top
        # left corner coordinates, we subtract half of each dimension
        switch_pos = (
            self.switch_pos[0] - math.ceil(self.switch_width/2),
            self.switch_pos[1] - math.ceil(self.switch_height/2)
        )
        return Observation({
            "isHoldingBox": self.is_holding_box,
            "robotPos": self.robot_pos,
            "switchPos": switch_pos,
            "boxPos": None if self.is_holding_box else self.box_pos,
            "isSwitchPressed": self.is_box_on_switch(),
            "isInPickupRange": self._can_pickup_box(),
            "robotWidth": self.robot_width,
            "robotHeight": self.robot_height,
            "switchWidth": self.switch_width,
            "switchHeight": self.switch_height,
            "boxWidth": self.box_width,
            "boxHeight": self.box_height,
            "doorTop": self.doorway_pos[1],
            "doorBottom": self.doorway_pos[1] + self.doorway_height,
            "wallWidth": self.wall_width,
        })