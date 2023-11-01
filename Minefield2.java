import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Minefield2 {
    private int flags;
    private Cell[][] field;
    private Boolean gameOver = false;

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
     * @param flags      Number of flags.
     */
    public Minefield2(int rows, int columns, int flags) {
        this.flags = flags;
        field = new Cell[rows][columns];

        //Sets all cells to blank "-"
        for(int i = 0; i < field.length; i++) {
            for(int j = 0; j < field[i].length; j++) {
                field[i][j] = new Cell(false, "-");
            }
        }
    }

    /**
     * GetNumFlags
     * Returns the number of flags left
     */
    public int getNumFlags() {
        return flags;
    }
    /**
     * evaluateField
     * Finds all the mines and calculates the surrounding 9x9 tiles values.
     */
    public void evaluateField() {
        int[][] checkLocations = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
        //Each sub-list correlates to a direction to an adjacent square of a mine. Ex: [1, -1] would move up one row and down one column
        for(int i = 0; i < field.length; i++) {
            for(int j = 0; j < field[i].length; j++) {
                if(field[i][j].getStatus().equals("M")) {  //Only want to do something if there is a mine at the spot
                    for(int k = 0; k < checkLocations.length; k++) {  //Checks each adjacent square
                        if(!(i + checkLocations[k][0] < 0 || i + checkLocations[k][0] >= field.length ||
                                j + checkLocations[k][1] < 0 || j + checkLocations[k][1] >= field[0].length ||
                                field[i + checkLocations[k][0]][j + checkLocations[k][1]].getStatus().equals("M"))) {
                                //Changes the field if there is no mine at new location and the new location is in bounds

                            if(field[i + checkLocations[k][0]][j + checkLocations[k][1]].getStatus().equals("-")) {
                                    //Field location is blank so change it to 1
                                field[i + checkLocations[k][0]][j + checkLocations[k][1]].setStatus("1");
                            } else {  //Add one to the field location
                                int num = Integer.parseInt(field[i + checkLocations[k][0]][j + checkLocations[k][1]].getStatus());
                                num++;

                                String newNum = "";
                                newNum += num;
                                field[i + checkLocations[k][0]][j + checkLocations[k][1]].setStatus(newNum);
                            }
                        }
                    }
                } else if(field[i][j].getStatus().equals("-")){  //Don't want to overwrite a number, so only set location to 0 if it is blank
                    field[i][j].setStatus("0");
                }
            }
        }
    }

    /**
     * Randomly places mines on field without putting them at the given x,y location
     *
     * @param x       x location on field
     * @param y        y location on field
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random random = new Random();
        while(mines > 0) {
            int xLoc = random.nextInt(field[0].length);
            int yLoc = random.nextInt(field.length);

            if(!(field[yLoc][xLoc].getRevealed() || (x == xLoc && y == yLoc) || field[yLoc][xLoc].getStatus().equals("M"))) {
                //Checks if array at the location is not at given x and y location, not revealed, and not a mine
                field[yLoc][xLoc].setStatus("M");
                mines--;
            }
        }
    }

    /**
     * guess
     *
     * @param x         The x value the user entered.
     * @param y         The y value the user entered.
     * @param flag      A boolean value representing if the user wants to place a flag
     * @return boolean  Return false if guess did not hit mine or if flag was placed, true if mine found, or out of flags.
     */
    public boolean guess(int x, int y, boolean flag) {
        if(flag) {  //User wants to place a flag
            if(flags <= 0) {  //No more flags left to place
                return true;
            } else {  //Place flag
                field[y][x].setStatus("F");
                flags--;
                field[y][x].setRevealed(true);
                return false;
            }
        } else {  //User guesses without placing flag
            if(field[y][x].getStatus().equals("0")) {  //Hits a space that is "0"
                revealZeroes(x, y);
                return false;
            } else if(field[y][x].getStatus().equals("M")) {  //Hits a mine so game is over
                gameOver = true;
                return true;
            } else {  //User guessed an open spot (number 1 - 8)
                field[y][x].setRevealed(true);
                return false;
            }
        }
    }
    /**
     * gameOver
     *
     * @return boolean    Return false if game is not over and squares have yet to be revealed, otherwise return true.
     */
    public boolean gameOver() {
        if(gameOver) {
            return true;
        } else {  //Checks squares for any un-revealed spaces
            boolean revealedSpaces = true;
            for (int i = 0; i < field.length; i++){
                for (int j = 0; j < field[0].length; j++){
                    if (!field[i][j].getRevealed()){
                        revealedSpaces = false;
                    }
                }
            }
            return revealedSpaces;
        }
    }

    /**
     * revealZeroes
     * Reveals all zeroes connected to the given x,y location. Also reveals all the neighboring non-zero values to the zeroes.
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        int[] startLoc = new int[2];
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        startLoc[0] = x;
        startLoc[1] = y;
        stack.push(startLoc);
        revealZeroesHelper(startLoc, stack);
    }
    /**
     * revealZeroesHelper
     * Reveals all zeroes connected to the given x,y location using a depth-first search.
     *
     * @param loc     The x,y location of the cell we are checking.
     * @param stack    linked list of all locations to search
     */
    private void revealZeroesHelper(int[] loc, Stack1Gen<int[]> stack) {
        if(!field[loc[1]][loc[0]].getStatus().equals("0") || field[loc[1]][loc[0]].getRevealed()) {  //Reveals numbered cells
            //NOTE: Don't have to check for mines because a cell with "0" has no neighboring mines
            field[loc[1]][loc[0]].setRevealed(true);
        } else {  //Location is a zero
            field[loc[1]][loc[0]].setRevealed(true);
            int[][] neighbors = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 1}};
            for(int i = 0; i < neighbors.length; i++) {
                //Looks at all neighbors of the zero and adds them to the stack if they are in bounds
                if (!(loc[0] + neighbors[i][0] < 0 || loc[0] + neighbors[i][0] >= field[0].length ||
                        loc[1] + neighbors[i][1] < 0 || loc[1] + neighbors[i][1] >= field.length)) {
                    int[] newLoc = {loc[0] + neighbors[i][0], loc[1] + neighbors[i][1]};
                    stack.push(newLoc);
                    revealZeroesHelper(newLoc, stack);
                }
            }
            stack.pop();  //No more available directions so the item is removed from the stack
        }
    }

    /**
     * revealMines
     *
     * Reveals all the spaces between the given start location and the closest mine by using a breadth-first search
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealMines(int x, int y) {
        Q1Gen<int[]> queue = new Q1Gen<>();
        int[] startLoc = new int[2];
        boolean mineFound = false;
        startLoc[0] = x;
        startLoc[1] = y;
        queue.add(startLoc);

        while(!mineFound && queue.length() != 0) {  //loops till a mind is found
            int[] loc = queue.remove();
            if ((loc[1] - 1 >= 0) && !field[loc[1] - 1][0].getRevealed()) {  //checking 1 location above loc
                int[] newLoc = new int[2];
                newLoc[0] = loc[0];
                newLoc[1] = loc[1] - 1;
                field[loc[1]][loc[0]].setRevealed(true);

                //If new location is a mine, reveal it and stop the while loop
                if(field[loc[1] - 1][loc[0]].getStatus().equals("M")) {
                    field[loc[1] - 1][loc[0]].setRevealed(true);
                    mineFound = true;
                    continue;
                }
                queue.add(newLoc);
            }
            if ((loc[1] + 1 < field.length) && !field[loc[1] + 1][loc[0]].getRevealed()) {  //checking 1 location below loc
                int[] newLoc = new int[2];
                newLoc[0] = loc[0];
                newLoc[1] = loc[1] + 1;
                field[newLoc[1]][newLoc[0]].setRevealed(true);

                //If new location is a mine, reveal it and stop the while loop
                if(field[loc[1] + 1][loc[0]].getStatus().equals("M")) {
                    field[loc[1] + 1][loc[0]].setRevealed(true);
                    mineFound = true;
                    continue;
                }
                queue.add(newLoc);
            }
            if ((loc[0] - 1 >= 0) && !field[loc[1]][loc[0] - 1].getRevealed()) {  //checking 1 location left of loc
                int[] newLoc = new int[2];
                newLoc[0] = loc[0] - 1;
                newLoc[1] = loc[1];
                field[newLoc[0]][newLoc[1]].setRevealed(true);

                //If new location is a mine, reveal it and stop the while loop
                if(field[loc[1]][loc[0] - 1].getStatus().equals("M")) {
                    field[loc[1]][loc[0] - 1].setRevealed(true);
                    mineFound = true;
                    continue;
                }
                queue.add(newLoc);
            }
            if ((loc[0] + 1 < field[0].length) && !field[loc[1]][loc[0] + 1].getRevealed()) {  //checking 1 location right of loc
                int[] newLoc = new int[2];
                newLoc[0] = loc[0] + 1;
                newLoc[1] = loc[1];
                field[newLoc[0]][newLoc[1]].setRevealed(true);

                //If new location is a mine, reveal it and stop the while loop
                if(field[loc[1]][loc[0] + 1].getStatus().equals("M")) {
                    field[loc[1]][loc[0] + 1].setRevealed(true);
                    mineFound = true;
                    continue;
                }
                queue.add(newLoc);
            }
        }
    }

/**
 * revealStart
 *
 * Reveals information around the starting location for user to see
 *
 * @param x       The x value the user entered.
 * @param y       The y value the user entered.
 */
public void revealStart(int x, int y) {
    this.revealCell(x, y);
}

/**
 * revealCell
 * Recursively reveals all connected zeros and neighboring cells with non-zero numbers.
 *
 * @param x       The x value of a cell on the field.
 * @param y       The y value of a cell on the field.
 */
private void revealCell(int x, int y) {
    if(x < 0 || x >= field[0].length || y < 0 || y >= field.length) {
        return; // Out of bounds
    }
    Cell cell = field[y][x];
    if(cell.getRevealed()) {
        return; // Cell already revealed
    }
    cell.setRevealed(true);
    if(cell.getStatus().equals("0")) {
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i == 0 && j == 0) {
                    continue; // Skip current cell
                }
                revealCell(x + i, y + j);
            }
        }
    } else if(!cell.getStatus().equals("M")) {
        return; // Stop recursion if cell has non-zero number
    }
}

    /**
     * printMinefield
     *
     * @function Prints the entire minefield regardless if the user has guessed a square.
     * used when debug mode is selected
     */
    public void printMinefield() {
        String string = new String();
        if(field.length > 9)  //different spacing for larger fields
            string += "    ";
        else
            string += "   ";

        for(int i = 0; i < field[0].length; i++) {  //adds the column numbers to string
            if(i >= 10)  //Less spacing for larger column numbers
                string += i + " ";
            else
                string += i + "  ";
        }
        string += "\n";
        for(int i = 0; i < field.length; i++) {
            if (field.length > 9 && i < 10)  //Need more space for larger fields
                string += i + "   "; //row number indicator
            else
                string += i + "  ";
            for (int j = 0; j < field[0].length; j++) {
                String status = field[i][j].getStatus();
                switch (status) {
                    case "M":
                        string += ANSI_RED + status + ANSI_GREY_BG;
                        break;
                    case "F":
                        string += ANSI_YELLOW_BRIGHT + status + ANSI_GREY_BG;
                        break;
                    case "0":
                        string += ANSI_DARK_GREY + status + ANSI_GREY_BG;
                        break;
                    case "1":
                        string += ANSI_BLUE + status + ANSI_GREY_BG;
                        break;
                    case "2":
                        string += ANSI_GREEN + status + ANSI_GREY_BG;
                        break;
                    case "3":
                        string += ANSI_ORANGE + status + ANSI_GREY_BG;
                        break;
                    case "4":
                        string += ANSI_PINK + status + ANSI_GREY_BG;
                        break;
                    case "5":
                        string += ANSI_YELLOW + status + ANSI_GREY_BG;
                        break;
                    case "6":
                        string += ANSI_PURPLE + status + ANSI_GREY_BG;
                        break;
                    case "7":
                        string += ANSI_LIGHT_BLUE + status + ANSI_GREY_BG;
                        break;
                    case "8":
                        string += ANSI_DARK_RED + status + ANSI_GREY_BG;
                        break;
                    default:  //Status is blank "-"
                        string += status;
                }
                string += "  ";
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
        String string = new String();
        if(field.length > 9)  //different spacing for larger fields
            string += "    ";
        else
            string += "   ";

        for(int i = 0; i < field[0].length; i++) {  //adds the column numbers to string
            if(i >= 10)  //Less spacing for larger column numbers
                string += i + " ";
            else
                string += i + "  ";
        }
        string += "\n";
        for(int i = 0; i < field.length; i++) {
            if(field.length > 9 && i <10)  //row number indicator
                string += i + "   ";
            else
                string += i + "  ";
            for(int j = 0; j < field[0].length; j++) {
                if(field[i][j].getRevealed()) {  //Only want to print revealed spaces
                    switch (field[i][j].getStatus()) {
                        case "M":
                            string += ANSI_RED + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "F":
                            string += ANSI_YELLOW_BRIGHT + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "0":
                            string += ANSI_DARK_GREY + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "1":
                            string += ANSI_BLUE + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "2":
                            string += ANSI_GREEN + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "3":
                            string += ANSI_ORANGE + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "4":
                            string += ANSI_PINK + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "5":
                            string += ANSI_YELLOW + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "6":
                            string += ANSI_PURPLE + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "7":
                            string += ANSI_LIGHT_BLUE + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
                        case "8":
                            string += ANSI_DARK_RED + field[i][j].getStatus() + ANSI_GREY_BG;
                            break;
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
}

