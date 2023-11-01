////written by: Samarth Nand(samar080) and Ebrahim Sofi(sofi0008)
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Minefield!");
        System.out.println("Choose your difficulty level:");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Hard");

        int level = scanner.nextInt();
        int rows, columns, mines, flags;

        switch (level) {
            case 1:
                rows = 5;
                columns = 5;
                mines = 5;
                flags = 5;
                break;
            case 2:
                rows = 9;
                columns = 9;
                mines = 12;
                flags = 12;
                break;
            case 3:
                rows = 20;
                columns = 20;
                mines = 40;
                flags = 40;
                break;
            default:
                System.out.println("Invalid level selected. Exiting...");
                return;
        }
        //Game starts here
        // GAME GUI
        System.out.println("Do you want to play in debug mode? (y/n)");
        boolean debug = scanner.next().equals("y");
        System.out.println("Enter starting coordinates (column, row):");
        int column = scanner.nextInt();
        int row = scanner.nextInt();
        Minefield game = new Minefield(rows, columns, flags);
        game.createMines(column,row, mines);
        game.evaluateField();
        game.revealMines(column, row);
        game.revealStart(column, row);
        boolean gameOver = false;

        // GAME LOOP
        while (gameOver == false) {
            if (debug) {
                game.printMinefield();
                gameOver = true;
                
            }
            else{
            // Main actions that can be performed are outlined and executed here
            System.out.println(game);
            System.out.println("Choose your action:");
            System.out.println("1. Reveal a cell");
            System.out.println("2. Flag a cell, you have "+ game.getFlags() + " flags left");
            int action = scanner.nextInt();

            if (action == 1) {
                System.out.println("Enter the column and row numbers of the cell you want to reveal:");
                int Row = scanner.nextInt();
                int Column = scanner.nextInt();
                game.guess(Row,Column,false);
                game.revealZeroes(Row, Column);
                game.revealMines(Row, Column);
                gameOver = game.gameOver();     
            } else if (action == 2) {
                System.out.println("Enter the column and row numbers of the cell you want to flag:");
                int Row = scanner.nextInt();
                int Column = scanner.nextInt();
                game.guess(Row,Column,true);
                game.revealZeroes(Row, Column);
                game.revealMines(Row, Column);
                gameOver = game.gameOver();
            }
            }
        }
        if(game.isGamewon())
        System.out.println("Congratulations! You won!");
        else
        {
            System.out.println(game);
            System.out.println("You revealed a mine game Over!");
        }
        scanner.close();
    }
}
