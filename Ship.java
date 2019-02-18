public class Ship
{
    private int[] end1;
    private int[] end2;
    private int length;
    private int[][] segments;
    private String name;

    /**
     * Creates a ship object.
     * @param end1  the first end of the ship
     * @param end2  the second end of the ship
     * @param name  the name of the ship
     */
    public Ship(int[] end1, int[] end2, String name)
    {
        if (end1[0] == end2[0] || end1[1] == end2[1])
        {
            this.end1 = end1;
            this.end2 = end2;
            this.length = Math.abs((end1[0] - end2[0]) + (end1[1] - end2[1])) + 1;
            this.segments = new int[this.length][2];
            this.name = name;

            if (end1[0] == end2[0])
            {
                int start = Math.min(end1[1],end2[1]);
                for (int i = 0; i < this.length; i++)
                {
                    this.segments[i] = new int[] {end1[0],start+i};
                }
            }
            else
            {
                int start = Math.min(end1[0],end2[0]);
                for (int i = 0; i < this.length; i++)
                {
                    this.segments[i] = new int[] {start+i,end1[1]};
                }
            }
        }
        else
        {
            this.end1 = new int[] {-1,-1};
            this.end2 = new int[] {-1,-1};
            this.length = -1;
            this.segments = new int[][] {{-1,-1}};
        }
    }

    /**
     * Gives the ship's length
     * @return the length of the ship
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Gives the ends of the ship
     * @return a list of the coordinates of both ends
     */
    public int[][] getEnds()
    {
        return new int[][] {end1, end2};
    }

    /**
     * Gives all the segments of the ship
     * @return a list of all the coordinates of the ship sections
     */
    public int[][] getSegs()
    {
        return segments;
    }

    /**
     * Gives the ship's name
     * @return the name of the ship
     */
    public String getName()
    {
        return name;
    }
}
