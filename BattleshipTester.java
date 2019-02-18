
/**
 * Write a description of class Battleship1 here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.*;
public class BattleshipTester
{
    public static void main(String[] args)
    {
        BattleshipBoard board = new BattleshipBoard(1);
        //board.set(5,4,3);
        //board.set(3,4,3);
        //board.set(4,5,3);
        //board.set(4,3,3);
        int[] x = board.adjacent(4,4,"horizontal");
        System.out.println(x[0] + ", " + x[1]);
        /*int[] one = {2,7};
        int[] two = {2,2};
        Ship s = new Ship(one,two);
        System.out.println(s.getLength());
        for (int[] i : s.getEnds())
        {
            System.out.println(Arrays.toString(i));
        }
        System.out.println("---");
        for (int[] i : s.getSegs())
        {
            System.out.println(Arrays.toString(i));
        }*/
    }
}
