from typing import Union, Tuple
from sprites import Robot, Box
from shapes import Circle, calculate_overlap_percentage, BoxShape, RobotShape
from colors import RED, BLACK, WHITE, GRAY
import pygame


class Game:
    # Set screen dimensions
    WIDTH, HEIGHT = 800, 600
    
    # Define box parameters
    BOX_SIZE = 40
    BOX_COLOR = RED

    # Define switch parameters
    SWITCH_SIZE = 20
    
    # Robot pickup range
    PICKUP_RANGE = 50

    # Define robot parameters
    ROBOT_SIZE = 50
    ROBOT_SPEED = 5

    # Define doorway parameters
    DOORWAY_WIDTH = 100
    DOORWAY_HEIGHT = 100
    DOORWAY_POS = (0, HEIGHT // 2 - DOORWAY_HEIGHT // 2)
    
    # Define fonts
    FONT = pygame.font.Font(None, 36)
    
    def __init__(self) -> None:
        self.switch_pressed = False
        self.box_held_by_robot = False
        self._box: Union[Box, None] = None
        self._robot: Union[Robot, None] = None
        self.switch = Circle(center=(Game.WIDTH - 100, Game.HEIGHT - 100), radius=Game.SWITCH_SIZE)
        self.screen = pygame.display.set_mode((Game.WIDTH, Game.HEIGHT))
        self.box_pos = (Game.WIDTH // 2, Game.HEIGHT // 2)
        self.robot_pos: Tuple[int, int] = (Game.WIDTH // 4, Game.HEIGHT // 2)

        # Game clock
        self.clock = pygame.time.Clock()
        self.running = False
    
    """
    Getter for the box sprite. This is used in place of directly accessing the field
    because there is a timing issue in constructing the sprite; it seems to depend on some
    state of Pygame being setup so that it can load the image file that does not seem to
    be true when the Game object is being instantiated (or when its static fields are
    instantiated).
    """
    def _get_box(self):
        if (not self._box):
            self._box = Box(Game.BOX_SIZE)
        return self._box
    
    
    """
    See #_get_box() for an explanation of this.
    """
    def _get_robot(self):
        if (not self._robot):
            self._robot = Robot(Game.BOX_SIZE)
        return self._robot
        
    def start(self):
        self.running = True
        while self.running:
            self._run_loop()

    def _draw_walls(self):
        # Draw the walls (except for the doorway)
        pygame.draw.rect(self.screen, BLACK, (0, 0, Game.WIDTH, 10))  # Top wall
        pygame.draw.rect(self.screen, BLACK, (0, Game.HEIGHT - 10, Game.WIDTH, 10))  # Bottom wall
        pygame.draw.rect(self.screen, BLACK, (Game.WIDTH - 10, 0, 10, Game.HEIGHT))  # Right wall
        
        # Draw the doorway
        pygame.draw.rect(self.screen, BLACK, (0, 0, 10, self.DOORWAY_POS[1]))  # Left wall above doorway
        pygame.draw.rect(
            self.screen,
            BLACK,
            (
                0,
                self.DOORWAY_POS[1] + self.DOORWAY_HEIGHT,
                10,
                Game.HEIGHT - (self.DOORWAY_POS[1] + self.DOORWAY_HEIGHT)
            )
        )  # Left wall below doorway

    # Function to check if the box is on the switch
    def _is_box_on_switch(self):
        box = self._get_box()
        if self.box_held_by_robot:
            return False
        overlap = calculate_overlap_percentage(
            self.switch,
            BoxShape(
                box=box,
                topLeft=self.box_pos
            )
        ) >= 40
        return overlap

    def _handle_space_pressed(self):
        box = self._get_box()
        robot = self._get_robot()
        if self.box_held_by_robot:
            # Drop the box if spacebar is pressed again
            self.box_held_by_robot = False        
        else:
            overlap_pickup = calculate_overlap_percentage(
                Circle(
                    center=(
                        self.box_pos[0] + box.rect.width // 2,
                        self.box_pos[1] + box.rect.height // 2
                    ),
                    radius=Game.PICKUP_RANGE
                ),
                RobotShape(
                    robot=robot,
                    topLeft=self.robot_pos
                )
            )
            in_range_of_box = overlap_pickup > 0
            # Check if robot is near the box and can pick it up or drop it
            if in_range_of_box:
                self.box_held_by_robot = True

        # Check if the box is on the switch
        if not self.box_held_by_robot and self._is_box_on_switch():
            self.switch_pressed = True
        else:
            self.switch_pressed = False
    
    def _run_loop(self):
        # Temporary position to check boundary conditions
        new_robot_pos: list[int] = list(self.robot_pos)
        
        # Key press logic for robot movement
        keys = pygame.key.get_pressed()
                
        if keys[pygame.K_LEFT]:
            new_robot_pos[0] -= self.ROBOT_SPEED
        if keys[pygame.K_RIGHT]:
            new_robot_pos[0] += self.ROBOT_SPEED
        if keys[pygame.K_UP]:
            new_robot_pos[1] -= self.ROBOT_SPEED
        if keys[pygame.K_DOWN]:
            new_robot_pos[1] += self.ROBOT_SPEED

        # Check boundaries (walls)
        # Left wall with doorway
        if (new_robot_pos[0] < 0 and
            not (self.DOORWAY_POS[1] <= new_robot_pos[1] <= self.DOORWAY_POS[1] + self.DOORWAY_HEIGHT)):
            new_robot_pos[0] = 0

        # Top and bottom walls
        if new_robot_pos[1] < 0:
            new_robot_pos[1] = 0
        if new_robot_pos[1] > Game.HEIGHT - Game.ROBOT_SIZE:
            new_robot_pos[1] = Game.HEIGHT - Game.ROBOT_SIZE

        # Right wall
        if new_robot_pos[0] > Game.WIDTH - Game.ROBOT_SIZE:
            new_robot_pos[0] = Game.WIDTH - Game.ROBOT_SIZE

        # Update robot position after boundary checks
        self.robot_pos = (new_robot_pos[0], new_robot_pos[1])
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                self.running = False
            
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_SPACE:
                    self._handle_space_pressed()

        # Drawing background (change based on light)
        light_on = not self.switch_pressed
        if light_on:
            self.screen.fill(WHITE)
        else:
            self.screen.fill(GRAY)

        self._draw_walls()

        # Draw the switch
        pygame.draw.circle(self.screen, BLACK, self.switch["center"], self.switch["radius"])

        # Draw the robot
        robot = self._get_robot()
        self.screen.blit(robot.image, (self.robot_pos[0], self.robot_pos[1]), robot.rect)

        if self.box_held_by_robot:
            self.box_pos = (self.robot_pos[0] + Game.ROBOT_SIZE, self.robot_pos[1])
        # Draw the box
        box = self._get_box()
        self.screen.blit(box.image, self.box_pos, box.rect)

        # Display status
        status_text = Game.FONT.render(f"Lights {'ON' if light_on else 'OFF'}", True, BLACK if light_on else WHITE)
        self.screen.blit(status_text, (10, 10))

        # Update display
        pygame.display.flip()

        # Frame rate control
        self.clock.tick(30)

