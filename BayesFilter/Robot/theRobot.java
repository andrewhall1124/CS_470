
import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.net.*;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;
    
    // store your probability map (for position of the robot in this array
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }

    public static void printArray(double[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println(); // New line after each row
        }
    }
    
    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        double[][] newProbs = new double[mundo.width][mundo.height];
    
        int dx = 0, dy = 0;
        switch(action) {
            case NORTH: dy = -1; break;
            case SOUTH: dy = 1;  break;
            case EAST:  dx = 1;  break;
            case WEST:  dx = -1; break;
            case STAY:  dx = 0;  dy = 0; break;
        }
        
        // ----- Motion Update -----
        // For every open cell in the current belief, propagate the probability.
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                // Only consider open spaces (where grid value is 0)
                if (mundo.grid[x][y] != 0)
                    continue;
                double prob = probs[x][y];
                if (prob == 0)
                    continue;
                
                // Compute intended new location from (x, y)
                int newX = x + dx;
                int newY = y + dy;
                // Check if the intended move is valid (within bounds and not a wall)
                if (newX >= 0 && newX < mundo.width &&
                    newY >= 0 && newY < mundo.height &&
                    mundo.grid[newX][newY] == 0) {
                    // With probability moveProb, the robot goes to (newX, newY)
                    newProbs[newX][newY] += moveProb * prob;
                    // With probability (1 - moveProb), it stays in (x, y)
                    newProbs[x][y] += (1.0 - moveProb) * prob;
                } else {
                    // If the move is invalid, the robot stays in place with probability 1.
                    newProbs[x][y] += prob;
                }
            }
        }
        
        // ----- Sensor Update -----
        // Sonars string order: index 0: North, 1: South, 2: East, 3: West.
        // For each open cell, update its probability based on the sensor likelihood.
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                double sensorLikelihood = 1.0;
                sensorLikelihood *= getSensorLikelihood(x, y-1, sonars.charAt(0)); // North
                sensorLikelihood *= getSensorLikelihood(x, y+1, sonars.charAt(1)); // South
                sensorLikelihood *= getSensorLikelihood(x+1, y, sonars.charAt(2)); // East
                sensorLikelihood *= getSensorLikelihood(x-1, y, sonars.charAt(3)); // West
                
                newProbs[x][y] *= sensorLikelihood;
            }
        }
        
        // ----- Normalization -----
        double sum = 0.0;
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                sum += newProbs[x][y];
            }
        }
        if (sum > 0) {
            for (int x = 0; x < mundo.width; x++) {
                for (int y = 0; y < mundo.height; y++) {
                    if (mundo.grid[x][y] != 0)
                        continue;
                    newProbs[x][y] /= sum;
                }
            }
        }
        
        probs = newProbs;
        myMaps.updateProbs(probs);
    }
    
    // Helper method: Given a cell (x, y) adjacent to the current cell and the corresponding sensor reading,
    // return the probability of that reading according to our sensor model.
    // If the cell is out-of-bounds or is a wall, we expect a wall ('1'); otherwise, we expect an open space ('0').
    private double getSensorLikelihood(int x, int y, char sensorReading) {
        char expected;
        if (x < 0 || x >= mundo.width || y < 0 || y >= mundo.height || mundo.grid[x][y] == 1) {
            expected = '1';
        } else {
            expected = '0';
        }
        
        // If the sensor reading matches what we expect, return sensorAccuracy;
        // otherwise, return (1 - sensorAccuracy).
        if (sensorReading == expected)
            return sensorAccuracy;
        else
            return 1.0 - sensorAccuracy;
    }
    
    void valueIteration() {
        final double DISCOUNT_FACTOR = 0.9;
        final double CONVERGENCE_THRESHOLD = 0.001;
        final int MAX_ITERATIONS = 100;
        
        // Initialize the value function
        Vs = new double[mundo.width][mundo.height];
        // Initialize Q-values for each state-action pair
        double[][][] Q = new double[mundo.width][mundo.height][5]; // 5 actions: NORTH, SOUTH, EAST, WEST, STAY
        
        // Initialize statePolicy to store the best action for each state
        int[][] statePolicy = new int[mundo.width][mundo.height];
        
        // Reward values
        final double REWARD_GOAL = 100.0;    // Goal state reward
        final double REWARD_FALL = -100.0;   // Falling down stairs penalty
        final double REWARD_STEP = -1.0;     // Cost of a step (to encourage shorter paths)
        
        // Value iteration
        int iterations = 0;
        double maxDelta;
        do {
            maxDelta = 0.0;
            
            // For each state
            for (int x = 0; x < mundo.width; x++) {
                for (int y = 0; y < mundo.height; y++) {
                    // Skip walls
                    if (mundo.grid[x][y] == 1)
                        continue;
                    
                    double oldValue = Vs[x][y];
                    
                    // For goal and pit states, values are fixed
                    if (mundo.grid[x][y] == 3) { // Goal state
                        Vs[x][y] = REWARD_GOAL;
                        continue;
                    } else if (mundo.grid[x][y] == 2) { // Pit state
                        Vs[x][y] = REWARD_FALL;
                        continue;
                    }
                    
                    // Calculate Q-values for each action
                    double maxQ = Double.NEGATIVE_INFINITY;
                    int bestAction = STAY;
                    
                    for (int action = 0; action < 5; action++) {
                        Q[x][y][action] = calculateQ(x, y, action, Vs, REWARD_STEP, DISCOUNT_FACTOR);
                        if (Q[x][y][action] > maxQ) {
                            maxQ = Q[x][y][action];
                            bestAction = action;
                        }
                    }
                    
                    // Update the value with the best Q-value
                    Vs[x][y] = maxQ;
                    statePolicy[x][y] = bestAction;
                    
                    // Update the maximum delta
                    double delta = Math.abs(Vs[x][y] - oldValue);
                    if (delta > maxDelta) {
                        maxDelta = delta;
                    }
                }
            }
            
            iterations++;
        } while (maxDelta > CONVERGENCE_THRESHOLD && iterations < MAX_ITERATIONS);
        
        System.out.println("Value iteration converged after " + iterations + " iterations.");
        
        // Update the value map display
        myMaps.updateValues(Vs);
    }

    // Calculate the Q-value for a state-action pair
    double calculateQ(int x, int y, int action, double[][] values, double stepCost, double discountFactor) {
        // Get the intended direction based on the action
        int dx = 0, dy = 0;
        switch (action) {
            case NORTH: dy = -1; break;
            case SOUTH: dy = 1;  break;
            case EAST:  dx = 1;  break;
            case WEST:  dx = -1; break;
            case STAY:  dx = 0; dy = 0; break;
        }
        
        // Calculate the next state
        int nx = x + dx;
        int ny = y + dy;
        
        // Check if the next state is valid
        boolean validMove = (nx >= 0 && nx < mundo.width && 
                            ny >= 0 && ny < mundo.height && 
                            mundo.grid[nx][ny] != 1);
        
        double qValue = 0.0;
        
        if (validMove) {
            // Probability of moving as intended
            qValue += moveProb * (stepCost + discountFactor * values[nx][ny]);
            
            // Probability of staying in place
            qValue += (1.0 - moveProb) * (stepCost + discountFactor * values[x][y]);
        } else {
            // If move not valid, stay in place with probability 1
            qValue += stepCost + discountFactor * values[x][y];
        }
        
        return qValue;
    }

    // Implement the automaticAction method to choose the best action based on the current belief state
    int automaticAction() {
        // Ensure the value function has been computed
        if (Vs == null) {
            valueIteration();
        }
        
        // Find the most likely position of the robot
        int maxX = 0, maxY = 0;
        double maxProb = 0.0;
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (probs[x][y] > maxProb) {
                    maxProb = probs[x][y];
                    maxX = x;
                    maxY = y;
                }
            }
        }
        
        // Find the action with the highest expected value
        int bestAction = STAY;
        double maxExpectedValue = Double.NEGATIVE_INFINITY;
        
        for (int action = 0; action < 5; action++) {
            // Get the intended direction based on the action
            int dx = 0, dy = 0;
            switch (action) {
                case NORTH: dy = -1; break;
                case SOUTH: dy = 1;  break;
                case EAST:  dx = 1;  break;
                case WEST:  dx = -1; break;
                case STAY:  dx = 0; dy = 0; break;
            }
            
            // Calculate the next state
            int nx = maxX + dx;
            int ny = maxY + dy;
            
            // Check if the next state is valid
            if (nx >= 0 && nx < mundo.width && ny >= 0 && ny < mundo.height && mundo.grid[nx][ny] != 1) {
                // The expected value of taking this action is the value of the next state
                double expectedValue = Vs[nx][ny];
                
                if (expectedValue > maxExpectedValue) {
                    maxExpectedValue = expectedValue;
                    bestAction = action;
                }
            }
        }
        
        // Debug printout
        System.out.println("Most likely position: (" + maxX + ", " + maxY + ") with probability " + maxProb);
        System.out.println("Choosing action: " + bestAction);
        
        return bestAction;
    }
    
    void doStuff() {
        int action;
        
        valueIteration();  // TODO: function you will write in Part II of the lab
        initializeProbabilities();  // Initializes the location (probability) map
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // TODO: get the action selected by your AI;
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        new theRobot(args[0], Integer.parseInt(args[1]));
    }
}