/**
 * Implements all the methods for a board to play Battleship.
 * 
 * Board values:
 * 0 = water
 * 1 = miss
 * 2 = ship
 * 3 = ship (hit)
 * 
 * @author Jonathan Shi
 * @version 14 December 2018
 */
import java.util.*;
public class BattleshipBoard
{
    private int[][] board;
    private int player;
    
    /**
     * Creates a Battleship board
     * @param player  the player number of the board
     */
    public BattleshipBoard(int player)
    {
        this.board = new int[10][10];
        this.player = player;
    }
    
    /**
     * Gives the array of the board.
     * @param hidden  whether the locations of ships should be hidden or not
     * @return the board array
     */
    public int[][] getBoard(boolean hidden)
    {
        if (!hidden)
        {
            return board;
        }
        else
        {
            int[][] secret = new int[10][10];
            for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < 10; j++)
                {
                    if (board[i][j] == 2)
                    {
                        secret[i][j] = 0;
                    }
                    else
                    {
                        secret[i][j] = board[i][j];
                    }
                }
            }
            return secret;
        }
    }
    
    /**
     * Gives the player that is in control of the board.
     * @return the player number
     */
    public int getPlayer()
    {
        return player;
    }
    
    /**
     * Gives the board but as a string
     * @return the string of the board
     */
    public String stringBoard(boolean hidden)
    {
        String display = Arrays.deepToString(getBoard(hidden)).replace(",","").replace("] [","\n");
        return display.substring(2,display.length()-2);
    }
    
    /**
     * Sets the value of a spot on the board to a given value.
     * @param row  the row of the spot
     * @param column the column of the spot
     * @param number the number to set the value to
     */
    public void set(int row, int column, int number)
    {
        board[row][column] = number;
    }
    
    /**
     * Determines whether a ship can be placed (if all the spaces a ship will occupy are empty)
     * @param row  the row of the first end of the ship
     * @param column  the column of the first end of the ship
     * @param length  the length of the ship
     * @param change  the direction the boat is placed
     * @return whether all the spaces are empty or not
     */
    public boolean allEmpty(int row, int col, int length, String change)
    {
        boolean empty = true;
        if (change.equals("row"))
        {
            for (int i = row; i < row + length; i++)
            {
                if (board[i][col] != 0)
                {
                    empty = false;
                    break;
                }
            }
        }
        else
        {
            for (int i = col; i < col + length; i++)
            {
                if (board[row][i] != 0)
                {
                    empty = false;
                    break;
                }
            }
        }
        return empty;
    }
    
    /**
     * Generates a list of coordinates to place the second end of the ship.
     * @param row  the row of the first end of the ship
     * @param column  the column of the first end of the ship
     * @param length  the length of the ship
     * @return a list of all the possible endpoints of the ship
     */
    public int[][] place(int row, int column, int length)
    {
        int[][] result = new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
        length--;
        if (board[row][column] == 0)
        {
            if (row + length < 10 && board[row + length][column] == 0)
            {
                if (allEmpty(row,column,length,"row")) result[0] = new int[] {row+length,column};
            }
            if (row - length >= 0 && board[row - length][column] == 0)
            {
                if (allEmpty(row-length,column,length,"row")) result[1] = new int[] {row-length,column};
            }
            if (column + length < 10 && board[row][column+length] == 0)
            {
                if (allEmpty(row,column,length,"col")) result[2] = new int[] {row,column+length};
            }
            if (column - length >= 0 && board[row][column - length] == 0)
            {
                if (allEmpty(row,column-length,length,"col")) result[3] = new int[] {row,column-length};
            }
            return result;
        }
        else
        {
            return new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
        }
    }
    
    /**
     * Chooses a spot adjacent to the one provided that has not been shot
     * @param row  the row of the spot
     * @param column  the column of the spot
     * @param direction  the direction to look for the adjacent spot
     * @return the coordinates for the adjacent spot
     */
    public int[] adjacent(int row, int column, String direction)
    {
        int[][] spots;
        if (direction.equals("horizontal")) spots = new int[][] {{row,column+1},{row,column-1}};
        else if (direction.equals("vertical")) spots = new int[][] {{row+1,column},{row-1,column}};
        else spots = new int[][] {{row+1,column},{row-1,column},{row,column+1},{row,column-1}};
        ArrayList<Integer> order = new ArrayList<Integer>();
        for (int i = 0; i < spots.length; i++) order.add(i);
        Collections.shuffle(order);
        int[] result;
        while (order.size() > 0)
        {
            int index = order.remove(0);
            result = spots[index];
            if (result[0] >= 0 && result[0] < 10 && result[1] >= 0 && result[1] < 10 && board[result[0]][result[1]] % 2 == 0)
            {
                return result;
            }
        }
        return new int[] {-1,-1};
    }
    
    /**
     * Shoots at the given coordinates.
     * @param row  the row of the spot to shoot at
     * @param column  the column of the spot to shoot at
     */
    public void shoot(int row, int column)
    {
        if (board[row][column] % 2 == 0) board[row][column]++;
    }
}
