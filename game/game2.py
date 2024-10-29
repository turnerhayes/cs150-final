import os
import pygame

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

# Define robot parameters
robot_size = 50
robot_color = GREEN
robot_speed = 5
robot_img = pygame.transform.scale(
    pygame.image.load(os.path.join(os.path.dirname(__file__), "./robot.jpg")).convert(),
    (robot_size, robot_size)
)
robot_pos = [WIDTH // 4, HEIGHT // 2]

# Define box parameters
box_size = 40
box_color = RED
box_pos = [WIDTH // 2, HEIGHT // 2]
box_grabbed = False
box_held_by_robot = False

# Define switch parameters
switch_size = 30
switch_pos = [WIDTH - 100, HEIGHT - 100]
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

# Function to check if the box is on the switch
def is_box_on_switch(box_pos, switch_pos):
    return (switch_pos[0] <= box_pos[0] <= switch_pos[0] + switch_size and
            switch_pos[1] <= box_pos[1] <= switch_pos[1] + switch_size)

# Main loop
running = True
while running:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    # Key press logic for robot movement
    keys = pygame.key.get_pressed()

    # Temporary position to check boundary conditions
    new_robot_pos = robot_pos.copy()

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
    robot_pos = new_robot_pos

    # Check if robot is near the box and can pick it up or drop it
    if not box_held_by_robot:
        if abs(robot_pos[0] - box_pos[0]) < pickup_range and abs(robot_pos[1] - box_pos[1]) < pickup_range:
            if keys[pygame.K_SPACE]:  # Spacebar to pick up the box
                box_held_by_robot = True
    else:
        # Box follows robot while held
        box_pos[0], box_pos[1] = robot_pos[0], robot_pos[1]

        # Drop the box if spacebar is pressed again
        if keys[pygame.K_SPACE]:
            box_held_by_robot = False

    # Check if the box is on the switch
    if is_box_on_switch(box_pos, switch_pos):
        switch_pressed = True
        light_on = False  # Turn off the lights
    else:
        switch_pressed = False
        light_on = True  # Turn on the lights if the box is not on the switch

    # Drawing background (change based on light)
    if light_on:
        screen.fill(WHITE)
    else:
        screen.fill(GRAY)

    # Draw the walls (except for the doorway)
    pygame.draw.rect(screen, BLACK, (0, 0, WIDTH, 10))  # Top wall
    pygame.draw.rect(screen, BLACK, (0, HEIGHT - 10, WIDTH, 10))  # Bottom wall
    pygame.draw.rect(screen, BLACK, (WIDTH - 10, 0, 10, HEIGHT))  # Right wall

    # Draw the doorway
    pygame.draw.rect(screen, BLACK, (0, 0, 10, doorway_pos[1]))  # Left wall above doorway
    pygame.draw.rect(screen, BLACK, (0, doorway_pos[1] + doorway_height, 10, HEIGHT - (doorway_pos[1] + doorway_height)))  # Left wall below doorway

    # Draw the robot
    # pygame.draw.rect(screen, robot_color, (robot_pos[0], robot_pos[1], robot_size, robot_size))
    screen.blit(robot_img, robot_pos, robot_img.get_rect())

    # Draw the box
    pygame.draw.rect(screen, box_color, (box_pos[0], box_pos[1], box_size, box_size))

    # Draw the switch
    pygame.draw.rect(screen, BLACK, (switch_pos[0], switch_pos[1], switch_size, switch_size))

    # Display status
    status_text = font.render(f"Lights {'ON' if light_on else 'OFF'}", True, BLACK if light_on else WHITE)
    screen.blit(status_text, (10, 10))

    # Update display
    pygame.display.flip()

    # Frame rate control
    clock.tick(30)

# Quit pygame
pygame.quit()
