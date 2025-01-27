import numpy as np
import math

from .baseagent import Agent

class PFAgent(Agent):
    def __init__(self):
        super().__init__()
        self.obst_threshold = 7  # Threshold for obstacle detection
        self.goal_radius = 2  # Radius of the goal
        self.spread = 2  # Spread of the field
        self.alpha = 1  # Strength of the attractive field
        self.beta = .4  # Strength of the repulsive field
        self.sensor_angle_inc = 2 * math.pi / 16  # Increment for sensor angles


    def act(self, robot_pos, goal_pos, dist_sensors):

        attractive_field = self._attractive_field(robot_pos, goal_pos)
        repulsive_field = self._repulsive_field(robot_pos, dist_sensors)
        
        return attractive_field + repulsive_field

    def _attractive_field(self, robot_pos, goal_pos):
        dx = goal_pos[0] - robot_pos[0]
        dy = goal_pos[1] - robot_pos[1]

        distance = np.linalg.norm([dx, dy])  # Distance to the goal
        theta = math.atan2(dy, dx)  # Angle to the goal

        # Goal reached
        if distance <= self.goal_radius:
            delta_x, delta_y = 0, 0  

        # Within extent of goal
        elif self.goal_radius < distance <= self.goal_radius + self.spread:
            delta_x = self.alpha * (distance - self.spread) * math.cos(theta)
            delta_y = self.alpha * (distance - self.spread) * math.sin(theta)
        
        # Outside extent of goal
        else:  
            delta_x = self.alpha * self.goal_radius * math.cos(theta)
            delta_y = self.alpha * self.goal_radius * math.sin(theta)

        return np.array([delta_x, delta_y])

    def _repulsive_field(self, robot_pos, dist_sensors):

        heading_offset = (robot_pos[2] - 90) * math.pi / 180
        thetas = [i * self.sensor_angle_inc + heading_offset for i in range(len(dist_sensors))]
        
        delta_x, delta_y = 0, 0

        for theta, distance in zip(thetas, dist_sensors):
            
            # Strong repulsion for obstacles very close
            if distance < self.obst_threshold:
                repulsion = -self.beta * self.obst_threshold / (distance + 1e-6)
                delta_x += repulsion * math.cos(theta)
                delta_y += repulsion * math.sin(theta)

            # Gradual repulsion within the influence range
            elif self.obst_threshold <= distance <= self.goal_radius + self.spread:
                repulsion = -self.beta * (self.spread + self.obst_threshold - distance)
                delta_x += repulsion * math.cos(theta)
                delta_y += repulsion * math.sin(theta)

        return np.array([delta_x, delta_y])