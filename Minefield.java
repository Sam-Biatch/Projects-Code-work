//written by: Samarth Nand(samar080) and Ebrahim Sofi(sofi0008)
import java.util.Random;

public class Minefield {

    private int rows;
    private int columns;
    private int flags;
    private Cell[][] field;
    private boolean revealed;
    private int mines;
    /**
    Global Section
    */
    public static final String ANSI_DARK_RED = "\u001b[38;5;52m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_ORANGE = "\u001b[38;5;209m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_YELLOW_BRIGHT = "\u001b[38;5;11m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_LIGHT_BLUE = "\u001b[38;5;14m";
    public static final String ANSI_PURPLE = "\u001b[38;5;54m";
    public static final String ANSI_PINK = "\u001b[38;5;219m";
    public static final String ANSI_GREY_BG = "\u001b[0m";
    public static final String ANSI_DARK_GREY = "\u001b[38;5;243m";
    /**
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.flags = flags;
        this.mines = flags;
        this.field = new Cell[rows][columns];
        //set everting to a default value of "-"
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                field[i][j] = new Cell(false, "-");  
            }
        }
    }
    /**
     * evaluateField
     *
     * @function When a mine is found in the field, calculate the surrounding 9x9 tiles values. If a mine is found, increase the count for the square.
     */
    public void evaluateField() {
        //possible locations to check
        int[][] checkLocations = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
        //loop through the field
        for(int i = 0; i < field.length; i++) {
            for(int j = 0; j < field[i].length; j++) {
                if(field[i][j].getStatus().equals("-")) {
                    int adjacentMines = 0;
                    for(int[] check : checkLocations) {
                        int row = i + check[0], col = j + check[1];
                        //check if the location is a not mine
                        if(row >= 0 && row < field.length && col >= 0 && col < field[row].length && field[row][col].getStatus().equals("M")) {
                            adjacentMines++;
                        }
                    }
                    
                    if(adjacentMines == 0) {
                        //if there are no mines, set the status to 0
                        field[i][j].setStatus("0");
                    } else {
                        field[i][j].setStatus(String.valueOf(adjacentMines));
                    }
                }
            }
        }
    }
    
    
    /**
     * createMines
     *
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random rand = new Random();
        int count = 0;
        while(count < mines){
            int i = rand.nextInt(rows);
            int j = rand.nextInt(columns);
            //check if the location is not the start location and if the location is not a mine
            if((i != x && j != y) && (!field[i][j].getStatus().equals("M")) && field[i][j].getStatus().equals("-")){
                field[i][j].setStatus("M");
                count++;
            }
        }
        evaluateField();

    }

    /**
     * guess
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean placeFlag) {
        if (placeFlag) {
            // User wants to place a flag
            if (flags > 0 && !field[y][x].getRevealed()) {
                // Place flag
                field[y][x].setStatus("F");
                flags--;
                field[y][x].setRevealed(true);
                return false;
            } else {
                // No more flags left to place or the cell is already revealed
                return true;
            }
        } else {
            // User guesses without placing flag
            if (!field[y][x].getRevealed()) {
                String status = field[y][x].getStatus();
                if (status.equals("M")) {
                    // Hits a mine so game is over
                    revealed = true;
                    return true;
                } else if (status.equals("0")) {
                    // Hits a space that is "0"
                    revealZeroes(x, y);
                    return false;
                } else {
                    // User guessed an open spot (number 1 - 8)
                    field[y][x].setRevealed(true);
                    return false;
                }
            } else {
                // The cell is already revealed
                return false;
            }
        }
    }
    
    /**
     * gameOver
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        if(revealed){
            return true;
        }
        //loop through the field and check if there are any unrevealed squares
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                if(!field[i][j].getRevealed()){
                    return false;
                }
            }
        }
        return true;

    }
    public int getFlags() {
        return flags;
    }
    /**
     * revealField
     *
     * This method should follow the psuedocode given.
     * Why might a stack be useful here rather than a queue?
     * A stack is useful here because it is a LIFO data structure. This means that the last item that was added to the stack will be the first item to be removed. This is useful because we want to reveal the squares that are closest to the square that the user clicked on.
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        //follows the psuedocode given
        int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        int[] startLoc = {x, y};
        stack.push(startLoc);
        //loop through the stack and check if the square is a 0 or not
        while (!stack.isEmpty()) {
            int[] loc = stack.pop();
            
            if (field[loc[1]][loc[0]].getStatus().equals("0") && !field[loc[1]][loc[0]].getRevealed()) {
                field[loc[1]][loc[0]].setRevealed(true);
                for (int i = 0; i < neighbors.length; i++) {
                    int newX = loc[0] + neighbors[i][0];
                    int newY = loc[1] + neighbors[i][1];
                    if (newX >= 0 && newX < field[0].length && newY >= 0 && newY < field.length) {
                        int[] newLoc = {newX, newY};
                        stack.push(newLoc);
                    }
                }
            } else if (!field[loc[1]][loc[0]].getStatus().equals("M")) {
                field[loc[1]][loc[0]].setRevealed(true);
            }
        }
    }
    
    /**
     * revealMines
     *
     * This method should follow the psuedocode given.
     * Why might a queue be useful for this function?
     * A queue is useful here because it is a FIFO data structure. This means that the first item that was added to the queue will be the first item to be removed. This is useful because we want to reveal the squares that are furthest from the square that the user clicked on.
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealMines(int x, int y) {
        //follows the psuedocode given
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[]{x, y});
    
        while(queue.length() != 0) {
            int[] loc = queue.remove();
            int currX = loc[0], currY = loc[1];
            if (field[currY][currX].getRevealed()) continue;
            field[currY][currX].setRevealed(true);
    
            if (field[currY][currX].getStatus().equals("M")) return;
    
            // Add all unrevealed neighbors to the queue
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = currX + i, newY = currY + j;
                    if (newX >= 0 && newY >= 0 && newX < field[0].length && newY < field.length &&
                        !field[newY][newX].getRevealed()) {
                        queue.add(new int[]{newX, newY});
                    }
                }
            }
        }
    }
    

    /**
     * revealStart
     *
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     */
    public void revealStart(int x, int y) {
        // reveal the starting location
        field[y][x].setRevealed(true);
    
        // reveal adjacent cells if they are empty
        int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};
        boolean done = false;
        while (!done) {
            done = true;
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field[0].length; j++) {
                    Cell cell = field[i][j];
                    // if the cell is revealed and has no adjacent mines, reveal all adjacent cells
                    if (cell.getRevealed() && cell.getStatus().equals("0")) {
                        for (int[] neighbor : neighbors) {
                            int ni = i + neighbor[1];
                            int nj = j + neighbor[0];
                            if (ni >= 0 && ni < field.length && nj >= 0 && nj < field[0].length) {
                                Cell ncell = field[ni][nj];
                                if (!ncell.getRevealed() && ncell.getStatus().equals(" ")) {
                                    ncell.setRevealed(true);
                                    done = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * printMinefield
     *
     * @function Prints the entire minefield regardless if the user has guessed a square.
     * used when debug mode is selected
     */
    public void printMinefield() {
        String string = "";
        int spacing = Math.max(3, String.valueOf(field[0].length - 1).length() + 1);
    
        if (field.length > 9) {
            string += "    ";
        } else {
            string += "   ";
        }
    
        for (int i = 0; i < field[0].length; i++) {
            if (i >= 10) {
                string += i + " ";
            } else {
                string += i + "  ";
            }
        }
        string += "\n";
    
        for (int i = 0; i < field.length; i++) {
            if (field.length > 9 && i < 10) {
                string += i + "   ";
            } else {
                string += i + "  ";
            }
    
            for (int j = 0; j < field[0].length; j++) {
                String status = field[i][j].getStatus();
    
                if (status.equals("M")) {
                    string += ANSI_RED + status + ANSI_GREY_BG;
                } else if (status.equals("F")) {
                    string += ANSI_YELLOW_BRIGHT + status + ANSI_GREY_BG;
                } else if (status.equals("0")) {
                    string += ANSI_DARK_GREY + status + ANSI_GREY_BG;
                } else if (status.equals("1")) {
                    string += ANSI_BLUE + status + ANSI_GREY_BG;
                } else if (status.equals("2")) {
                    string += ANSI_GREEN + status + ANSI_GREY_BG;
                } else if (status.equals("3")) {
                    string += ANSI_ORANGE + status + ANSI_GREY_BG;
                } else if (status.equals("4")) {
                    string += ANSI_PINK + status + ANSI_GREY_BG;
                } else if (status.equals("5")) {
                    string += ANSI_YELLOW + status + ANSI_GREY_BG;
                } else if (status.equals("6")) {
                    string += ANSI_PURPLE + status + ANSI_GREY_BG;
                } else if (status.equals("7")) {
                    string += ANSI_LIGHT_BLUE + status + ANSI_GREY_BG;
                } else if (status.equals("8")) {
                    string += ANSI_DARK_RED + status + ANSI_GREY_BG;
                } else {
                    string += status;
                }
    
                string += " ".repeat(spacing - status.length());
            }
    
            string += "\n";
        }
    
        System.out.println(string);
    }
    

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        String string = "";
        // The spacing is the length of the longest number in the field plus 1
        if (field.length > 9) {
            string += "    ";
        } else {
            string += "   ";
        }
    
        for (int i = 0; i < field[0].length; i++) {
            // if the number is greater than 10, then the spacing is 1 less than the length of the number
            if (i >= 10) {
                string += i + " ";
            } else {
                string += i + "  ";
            }
        }
        string += "\n";
    
        for (int i = 0; i < field.length; i++) {
            // if the number is less than 10, then the spacing is 1 less than the length of the number
            if (field.length > 9 && i < 10) {
                string += i + "   ";
            } else {
                string += i + "  ";
            }
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j].getRevealed()) {
                    String status = field[i][j].getStatus();
                    if (status.equals("M")) {
                        string += ANSI_RED + status + ANSI_GREY_BG;
                    } else if (status.equals("F")) {
                        string += ANSI_YELLOW_BRIGHT + status + ANSI_GREY_BG;
                    } else if (status.equals("0")) {
                        string += ANSI_DARK_GREY + status + ANSI_GREY_BG;
                    } else if (status.equals("1")) {
                        string += ANSI_BLUE + status + ANSI_GREY_BG;
                    } else if (status.equals("2")) {
                        string += ANSI_GREEN + status + ANSI_GREY_BG;
                    } else if (status.equals("3")) {
                        string += ANSI_ORANGE + status + ANSI_GREY_BG;
                    } else if (status.equals("4")) {
                        string += ANSI_PINK + status + ANSI_GREY_BG;
                    } else if (status.equals("5")) {
                        string += ANSI_YELLOW + status + ANSI_GREY_BG;
                    } else if (status.equals("6")) {
                        string += ANSI_PURPLE + status + ANSI_GREY_BG;
                    } else if (status.equals("7")) {
                        string += ANSI_LIGHT_BLUE + status + ANSI_GREY_BG;
                    } else if (status.equals("8")) {
                        string += ANSI_DARK_RED + status + ANSI_GREY_BG;
                    }
                } else {
                    string += "-";
                }
                string += "  ";
            }
            string += "\n";
        }
        return string;
    }
    public boolean isGamewon()
    {
        //checks if the game is won by comparing the number of mines to the number of mines that have been flagged
        int count = 0;
        for (int i = 0; i < field.length; i++) 
        {
            for (int j = 0; j < field[0].length; j++) 
            {
                if (field[i][j].getRevealed() && field[i][j].getStatus().equals("M"))
                {
                    count++;
                }
            }
        }
        if (count == mines)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

    