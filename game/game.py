import os
from typing import Union, TypedDict, Tuple
from shapely import Polygon, Point
import pygame

Circle = TypedDict("Circle", {"center": Tuple[int, int], "radius": int})
Rect = TypedDict("Rect", {"topLeft": Tuple[int, int], "width": int, "height": int})


class Robot(pygame.sprite.Sprite):
    def __init__(self) -> None:
        pygame.sprite.Sprite.__init__(self)
        self.image = pygame.transform.scale(
            pygame.image.load(os.path.join(os.path.dirname(__file__), "./robot.png")).convert_alpha(),
            (robot_size, robot_size)
        )
        
        self.rect = self.image.get_rect()
        
RobotShape = TypedDict("RobotShape", {"robot": Robot, "topLeft": Tuple[int, int]})

class Box(pygame.sprite.Sprite):
    def __init__(self, box_size: int) -> None:
        pygame.sprite.Sprite.__init__(self)
        self.image = pygame.transform.scale(
            pygame.image.load(os.path.join(os.path.dirname(__file__), "./box.png")).convert_alpha(),
            (box_size, box_size)
        )
        
        self.rect = self.image.get_rect()
        
RobotShape = TypedDict("RobotShape", {"robot": Robot, "topLeft": Tuple[int, int]})
BoxShape = TypedDict("BoxShape", {"box": Box, "topLeft": Tuple[int, int]})

Shape = Union[Circle, Rect, RobotShape, BoxShape]

# Initialize pygame
pygame.init()

# Set screen dimensions
WIDTH, HEIGHT = 800, 600
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption('Room Simulation')

# Define colors
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
GRAY = (100, 100, 100)
RED = (255, 0, 0)
GREEN = (0, 255, 0)
BLUE = (0, 0, 255)

# Define robot parameters
robot_size = 50
robot_color = GREEN
robot = Robot()
robot_speed = 5
robot_pos: Tuple[int, int] = (WIDTH // 4, HEIGHT // 2)

# Define box parameters
box_size = 40
box_pos = (WIDTH // 2, HEIGHT // 2)
box = Box(box_size)
box_color = RED
box_grabbed = False
box_held_by_robot = False

# Define switch parameters
switch_size = 20
switch = Circle(center=(WIDTH - 100, HEIGHT - 100), radius=switch_size)
switch_pressed = False

# Define light status
light_on = True

# Robot pickup range
pickup_range = 50

# Define doorway parameters
doorway_width = 100
doorway_height = 100
doorway_pos = (0, HEIGHT // 2 - doorway_height // 2)

# Define fonts
font = pygame.font.Font(None, 36)

# Game clock
clock = pygame.time.Clock()

def draw_walls():
    # Draw the walls (except for the doorway)
    pygame.draw.rect(screen, BLACK, (0, 0, WIDTH, 10))  # Top wall
    pygame.draw.rect(screen, BLACK, (0, HEIGHT - 10, WIDTH, 10))  # Bottom wall
    pygame.draw.rect(screen, BLACK, (WIDTH - 10, 0, 10, HEIGHT))  # Right wall
    
    # Draw the doorway
    pygame.draw.rect(screen, BLACK, (0, 0, 10, doorway_pos[1]))  # Left wall above doorway
    pygame.draw.rect(screen, BLACK, (0, doorway_pos[1] + doorway_height, 10, HEIGHT - (doorway_pos[1] + doorway_height)))  # Left wall below doorway


# Helper function to convert shapes to shapely polygons
def shape_to_polygon(shape: Shape):
    if "robot" in shape:
        robot_rect = shape["robot"].rect
        shape = Rect(
            topLeft=shape["topLeft"],
            width=robot_rect.width,
            height=robot_rect.height
        )
    elif "box" in shape:
        box_rect = shape["box"].rect
        shape = Rect(
            topLeft=shape["topLeft"],
            width=box_rect.width,
            height=box_rect.height
        )
    if "radius" in shape:
        # If the shape is a circle
        center = Point(shape['center'])
        return center.buffer(shape['radius'])  # Create circular polygon
    elif "width" in shape and "height" in shape:
        # If the shape is a rectangle
        x, y = shape['topLeft']
        width, height = shape['width'], shape['height']
        return Polygon([(x, y), (x + width, y), (x + width, y + height), (x, y + height)])
    else:
        raise ValueError("Unknown shape type")

# Generalized overlap percentage calculation function
def calculate_overlap_percentage(shape1: Shape, shape2: Shape) -> float:
    # Convert both shapes to shapely polygons
    polygon1 = shape_to_polygon(shape1)
    polygon2 = shape_to_polygon(shape2)
    
    # Calculate the intersection area
    intersection_area = polygon1.intersection(polygon2).area
    
    # Calculate the area of the first shape
    shape1_area = polygon1.area
    
    if shape1_area == 0:
        return 0
     
    # Calculate the percentage of the first shape that overlaps with the second shape
    overlap_percentage = (intersection_area / shape1_area) * 100
    
    return overlap_percentage


# Function to check if the box is on the switch
def is_box_on_switch():
    if box_held_by_robot:
        return False
    overlap = calculate_overlap_percentage(
        switch,
        BoxShape(
            box=box,
            topLeft=box_pos
        )
    ) >= 40
    return overlap

def handle_space_pressed(robot_pos: Tuple[int, int]):
    global box_held_by_robot, switch_pressed, light_on
    
    if box_held_by_robot:
        # Drop the box if spacebar is pressed again
        box_held_by_robot = False        
    else:
        overlap_pickup = calculate_overlap_percentage(
            Circle(
                center=(
                    box_pos[0] + box.rect.width // 2,
                    box_pos[1] + box.rect.height // 2
                ),
                radius=pickup_range
            ),
            RobotShape(
                robot=robot,
                topLeft=robot_pos
            )
        )
        in_range_of_box = overlap_pickup > 0
        # Check if robot is near the box and can pick it up or drop it
        if in_range_of_box:
            box_held_by_robot = True

    # Check if the box is on the switch
    if not box_held_by_robot and is_box_on_switch():
        switch_pressed = True
        light_on = False  # Turn off the lights
    else:
        switch_pressed = False
        light_on = True  # Turn on the lights if the box is not on the switch

# Main loop
running = True

while running:
    # Temporary position to check boundary conditions
    new_robot_pos: list[int] = list(robot_pos)
    
    # Key press logic for robot movement
    keys = pygame.key.get_pressed()
            
    if keys[pygame.K_LEFT]:
        new_robot_pos[0] -= robot_speed
    if keys[pygame.K_RIGHT]:
        new_robot_pos[0] += robot_speed
    if keys[pygame.K_UP]:
        new_robot_pos[1] -= robot_speed
    if keys[pygame.K_DOWN]:
        new_robot_pos[1] += robot_speed

    # Check boundaries (walls)
    # Left wall with doorway
    if (new_robot_pos[0] < 0 and
        not (doorway_pos[1] <= new_robot_pos[1] <= doorway_pos[1] + doorway_height)):
        new_robot_pos[0] = 0

    # Top and bottom walls
    if new_robot_pos[1] < 0:
        new_robot_pos[1] = 0
    if new_robot_pos[1] > HEIGHT - robot_size:
        new_robot_pos[1] = HEIGHT - robot_size

    # Right wall
    if new_robot_pos[0] > WIDTH - robot_size:
        new_robot_pos[0] = WIDTH - robot_size

    # Update robot position after boundary checks
    robot_pos = (new_robot_pos[0], new_robot_pos[1])
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_SPACE:
                handle_space_pressed(robot_pos=robot_pos)

    # Drawing background (change based on light)
    if light_on:
        screen.fill(WHITE)
    else:
        screen.fill(GRAY)

    draw_walls()

    # Draw the switch
    pygame.draw.circle(screen, BLACK, switch["center"], switch["radius"])

    # Draw the robot
    screen.blit(robot.image, (robot_pos[0], robot_pos[1]), robot.rect)

    if box_held_by_robot:
        box_pos = (robot_pos[0] + robot_size, robot_pos[1])
    # Draw the box
    screen.blit(box.image, box_pos, box.rect)

    # Display status
    status_text = font.render(f"Lights {'ON' if light_on else 'OFF'}", True, BLACK if light_on else WHITE)
    screen.blit(status_text, (10, 10))
    # screen.blit(font.render("is holding box? %s" % box_held_by_robot, True, BLACK if light_on else WHITE), (10, 30))

    # Update display
    pygame.display.flip()

    # Frame rate control
    clock.tick(30)

# Quit pygame
pygame.quit()
