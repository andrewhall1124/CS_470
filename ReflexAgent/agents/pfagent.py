import numpy as np
import math

from .baseagent import Agent

class PFAgent(Agent):
    def __init__(self):
        super().__init__()
        self.alpha = 1
        self.beta = 1
        self.gamma = 1
        self.threshold = 5

    def act(self, robot_pos, goal_pos, dist_sensors):
        
        # Goal distance and theta
        dx = goal_pos[0] - robot_pos[0]
        dy = goal_pos[1] - robot_pos[1]
        distance = np.linalg.norm([dx, dy])
        theta = math.atan2(dy, dx)  # Angle to the goal

        # End state
        if distance <= 1:
            return np.array([0, 0])

        # Thetas of obstacles
        sensor_angle_inc = 2 * math.pi / 16
        heading_offset = (robot_pos[2] - 90) * math.pi / 180
        thetas = [i * sensor_angle_inc + heading_offset for i in range(len(dist_sensors))]

        # Potential fields
        attractive_field = self._attractive_field(theta)
        repulsive_field = self._repulsive_field(dist_sensors, thetas)
        tangential_field = self._tangential_field(dist_sensors, thetas)
        
        return self.alpha * attractive_field + self.beta * repulsive_field + self.gamma * tangential_field

    def _attractive_field(self, theta):
        delta_x = math.cos(theta)
        delta_y = math.sin(theta)

        return np.array([delta_x, delta_y])

    def _repulsive_field(self, distances, thetas):
        dx, dy = 0,0
        for distance, theta in zip(distances, thetas):
            if distance < self.threshold:
                dx += -np.sign(math.cos(theta)) * (1 / distance)
                dy += -np.sign(math.sin(theta)) * (1 / distance)
        
        return np.array([dx, dy])

    def _tangential_field(self, distances, thetas):
        dx, dy = 0,0
        for distance, theta in zip(distances, thetas):
            if distance < self.threshold:
                dx += -np.sign(math.cos(theta)) * (1 / distance)
                dy += -np.sign(math.sin(theta)) * (1 / distance)

        R = np.array([[0,-1],[1,0]])

        return R @ np.array([dx, dy])