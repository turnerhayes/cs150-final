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
    
    """
    Begin running the simulation (does the simulation loop). Exits once the
    simulation is done.
    """
    def start(self):
        self.running = True
        while self.running:
            self._run_loop()
    
    """
    Moves the robot left one unit (exact distance determined by
    Game.ROBOT_SPEED).
    """
    def move_left(self):
        self.robot_pos = (self.robot_pos[0] - Game.ROBOT_SPEED, self.robot_pos[1])
    
    """
    Moves the robot right one unit (exact distance determined by
    Game.ROBOT_SPEED).
    """
    def move_right(self):
        self.robot_pos = (self.robot_pos[0] + Game.ROBOT_SPEED, self.robot_pos[1])
    
    """
    Moves the robot up one unit (exact distance determined by
    Game.ROBOT_SPEED).
    """
    def move_up(self):
        self.robot_pos = (self.robot_pos[0], self.robot_pos[1] - Game.ROBOT_SPEED)
    
    """
    Moves the robot down one unit (exact distance determined by
    Game.ROBOT_SPEED).
    """
    def move_down(self):
        self.robot_pos = (self.robot_pos[0], self.robot_pos[1] + Game.ROBOT_SPEED)
    
    """
    Makes the robot pick up the box if it can.
    
    Returns:
        True if the robot picked up the box, False if it was unable to pick it
        up for any reason
    """
    def grab_item(self):
        if not self._can_pickup_box():
            return False
        
        self.box_held_by_robot = True
    
    def release_item(self):
        if not self.box_held_by_robot:
            return False
        
        self.box_held_by_robot = False
        return True

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

    """
    Determines whether the box is currently on top of the switch (and not
    being held by the robot)
    
    Returns:
        True if the box is on top of the switch and not being held, False
        otherwise
    """
    def is_box_on_switch(self):
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

    def _can_pickup_box(self):
        if self.box_held_by_robot:
            return False
        
        box = self._get_box()
        robot = self._get_robot()
        
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
        return overlap_pickup > 0

    def _handle_space_pressed(self):
        if self.box_held_by_robot:
            # Drop the box if spacebar is pressed again
            self.release_item()     
        else:
            # Check if robot is near the box and can pick it up or drop it
            self.grab_item()

        # Check if the box is on the switch
        if not self.box_held_by_robot and self.is_box_on_switch():
            self.switch_pressed = True
        else:
            self.switch_pressed = False
    
    def _run_loop(self):
        # Key press logic for robot movement
        keys = pygame.key.get_pressed()
                
        if keys[pygame.K_LEFT]:
            self.move_left()
        if keys[pygame.K_RIGHT]:
            self.move_right()
        if keys[pygame.K_UP]:
            self.move_up()
        if keys[pygame.K_DOWN]:
            self.move_down()

        # Check boundaries (walls)
        # Left wall with doorway
        if (self.robot_pos[0] < 0 and
            not (self.DOORWAY_POS[1] <= self.robot_pos[1] <= self.DOORWAY_POS[1] + self.DOORWAY_HEIGHT)):
            self.robot_pos = (0, self.robot_pos[1])

        # Top and bottom walls
        if self.robot_pos[1] < 0:
            self.robot_pos = (self.robot_pos[0], 0)
        if self.robot_pos[1] > Game.HEIGHT - Game.ROBOT_SIZE:
            self.robot_pos = (self.robot_pos[0], Game.HEIGHT - Game.ROBOT_SIZE)

        # Right wall
        if self.robot_pos[0] > Game.WIDTH - Game.ROBOT_SIZE:
            self.robot_pos = (Game.WIDTH - Game.ROBOT_SIZE, self.robot_pos[1])

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

