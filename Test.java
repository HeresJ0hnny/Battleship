
/**
 * Write a description of class Test here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.util.*;
public class Test
{
    private Asdf one;
    private Asdf two;
    private Asdf[] asdf;
    
    public Test()
    {
        one = new Asdf(1);
        two = new Asdf(2);
        asdf = new Asdf[] {one,two};
    }
    
    public int wow(int x)
    {
        if (x == 1) return 1;
        else return 0;
    }

    
    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public static void main(String[] args)
    {
        int[][] weirdHits = new int[][]{{1,2},{3,4},{5,6},{7,8},{-1,-1}};
        int numWeird = 4;
        for (int[] i : weirdHits) System.out.println(Arrays.toString(i));
        
        for (int i = 1; i <= numWeird; i++)
        {
            weirdHits[i-1] = Arrays.copyOf(weirdHits[i],2);
        }
        for (int[] i : weirdHits) System.out.println(Arrays.toString(i));
    }
    
}
