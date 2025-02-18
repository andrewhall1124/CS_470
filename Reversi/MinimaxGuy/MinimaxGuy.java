import java.util.*;
import java.io.*;
import java.net.*;

class MinimaxGuy {

    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    Random generator = new Random();

    double t1, t2;
    int me;
    int boardState;
    int state[][] = new int[8][8]; // state[0][0] is the bottom left corner of the board (on the GUI)
    int turn = -1;
    int round;
    
    int validMoves[] = new int[64];
    int numValidMoves;
    
    
    // main function that (1) establishes a connection with the server, and then plays whenever it is this player's turn
    public MinimaxGuy(int _me, String host) {
        me = _me;
        initClient(host);

        int myMove;
        
        while (true) {
            System.out.println("Read");
            readMessage();
            
            if (turn == me) {
                System.out.println("Move");
                getValidMoves(round, state);
                
                myMove = move();
                
                String sel = validMoves[myMove] / 8 + "\n" + validMoves[myMove] % 8;
                
                System.out.println("Selection: " + validMoves[myMove] / 8 + ", " + validMoves[myMove] % 8);
                
                sout.println(sel);
            }
        }
    }
    
    // You should modify this function
    // validMoves is a list of valid locations that you could place your "stone" on this turn
    // Note that "state" is a global variable 2D list that shows the state of the game
    private int move() {
        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < numValidMoves; i++) {
            int row = validMoves[i] / 8;
            int col = validMoves[i] % 8;

            int[][] newState = copyBoard(state);
            applyMove(newState, row, col, me);
            int score = minimax(newState, 3, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove = i;
            }
        }
        
        return bestMove;
    }

    // Copies the board to avoid modifying the original state
    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 8);
        }
        return newBoard;
    }

    // Simulates applying a move
    private void applyMove(int[][] board, int row, int col, int player) {
        board[row][col] = player;
        int opponent = (player == 1) ? 2 : 1;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int r = row + dy, c = col + dx;
                boolean foundOpponent = false;

                while (r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == opponent) {
                    foundOpponent = true;
                    r += dy;
                    c += dx;
                }

                if (foundOpponent && r >= 0 && r < 8 && c >= 0 && c < 8 && board[r][c] == player) {
                    r = row + dy;
                    c = col + dx;
                    while (board[r][c] == opponent) {
                        board[r][c] = player;
                        r += dy;
                        c += dx;
                    }
                }
            }
        }
    }

    // Minimax with Alpha-Beta Pruning
    private int minimax(int[][] board, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0) {
            return evaluateBoard(board);
        }

        List<Integer> moves = getAvailableMoves(board, maximizingPlayer ? me : (me == 1 ? 2 : 1));

        if (moves.isEmpty()) {
            return evaluateBoard(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int move : moves) {
                int row = move / 8;
                int col = move % 8;
                int[][] newState = copyBoard(board);
                applyMove(newState, row, col, me);

                int eval = minimax(newState, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            int opponent = (me == 1) ? 2 : 1;
            for (int move : moves) {
                int row = move / 8;
                int col = move % 8;
                int[][] newState = copyBoard(board);
                applyMove(newState, row, col, opponent);

                int eval = minimax(newState, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    // Evaluates the board state
    private int evaluateBoard(int[][] board) {
        // Weight matrices for different phases of the game
        int[][] weights;

        // Keep control of the middle.
        int[][] earlyWeights = { //earlyWeights
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 10, 10, 10, 10, 1, 1},
            {1, 1, 10, 100, 100, 10, 1, 1},
            {1, 1, 10, 100, 100, 10, 1, 1},
            {1, 1, 10, 10, 10, 10, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
        };
        
        // Capture the edges
        int[][] midWeights = {
            {100, 10, 10, 10, 10, 10, 10, 100},
            {10, 1, 1, 1, 1, 1, 1, 10},
            {10, 1, 1, 1, 1, 1, 1, 10},
            {10, 1, 1, 1, 1, 1, 1, 10},
            {10, 1, 1, 1, 1, 1, 1, 10},
            {10, 1,1, 1, 1, 1, 1, 10},
            {10, 1, 1, 1, 1, 1, 1, 10},
            {100, 10, 10, 10, 10, 10, 10, 100},
        };
        
        // Maximize flips
        int[][] lateWeights = {
            {100, 1, 3, 3, 3, 3, 1, 100},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {3, 1, 1, 1, 1, 1, 1, 3},
            {3, 1, 1, 2, 2, 1, 1, 3},
            {3, 1, 1, 2, 2, 1, 1, 3},
            {3, 1,1, 1, 1, 1, 1, 3},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {100, 1, 3, 3, 3, 3, 1, 100},
        };

        if(round < 24){
            weights = earlyWeights;
        }
        else if(round < 54){
            weights = midWeights;
        }
        else{
            weights = lateWeights;
        }

        int myScore = 0, opponentScore = 0;
        int opponent = (me == 1) ? 2 : 1;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == me) {
                    myScore += weights[i][j];
                } else if (board[i][j] == opponent) {
                    opponentScore += weights[i][j];
                }
            }
        }
        return myScore - opponentScore;
    }

    // Gets a list of available moves for a given player
    private List<Integer> getAvailableMoves(int[][] board, int player) {
        List<Integer> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == 0 && couldBe(board, i, j)) {
                    moves.add(i * 8 + j);
                }
            }
        }
        return moves;
    }
    
    private void getValidMoves(int round, int state[][]) {
        int i, j;
        
        numValidMoves = 0;
        if (round < 4) {
            if (state[3][3] == 0) {
                validMoves[numValidMoves] = 3*8 + 3;
                numValidMoves ++;
            }
            if (state[3][4] == 0) {
                validMoves[numValidMoves] = 3*8 + 4;
                numValidMoves ++;
            }
            if (state[4][3] == 0) {
                validMoves[numValidMoves] = 4*8 + 3;
                numValidMoves ++;
            }
            if (state[4][4] == 0) {
                validMoves[numValidMoves] = 4*8 + 4;
                numValidMoves ++;
            }
            System.out.println("Valid Moves:");
            for (i = 0; i < numValidMoves; i++) {
                System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            }
        }
        else {
            System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                            System.out.println(i + ", " + j);
                        }
                    }
                }
            }
        }
    }
    
    private boolean checkDirection(int state[][], int row, int col, int incx, int incy) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;
        
        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;
        
            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;
        
            sequence[seqLen] = state[r][c];
            seqLen++;
        }
        
        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (me == 1) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        return true;
                    break;
                }
            }
        }
        
        return false;
    }
    
    private boolean couldBe(int state[][], int row, int col) {
        int incx, incy;
        
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;
            
                if (checkDirection(state, row, col, incx, incy))
                    return true;
            }
        }
        
        return false;
    }
    
    public void readMessage() {
        int i, j;
        try {
            turn = Integer.parseInt(sin.readLine());
            
            if (turn == -999) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                
                System.exit(1);
            }
            
            round = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            System.out.println(t1);
            t2 = Double.parseDouble(sin.readLine());
            System.out.println(t2);
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        
        System.out.println("Turn: " + turn);
        System.out.println("Round: " + round);
        for (i = 7; i >= 0; i--) {
            for (j = 0; j < 8; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void initClient(String host) {
        int portNumber = 3333+me;
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            String info = sin.readLine();
            System.out.println(info);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    public static void main(String args[]) {
        new MinimaxGuy(Integer.parseInt(args[1]), args[0]);
    }
    
}
