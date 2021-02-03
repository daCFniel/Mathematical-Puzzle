import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.Stack;

/**
 * This program implements an application that
 * solves Generalised Tower of Hanoi problem.
 * The logic is based on the Frame – Stewart algorithm.
 * Program uses pre-written data structures Stack and Vector.
 * @author Daniel Bielech ~ db662
 * @version 06.12.2020
 */
public class Main {
    private int n; // Number of discs.
    private int t; // Number of towers.
    private int s; // Source tower.
    private int d; // Destination tower.
    private Vector<Integer> buffers; // Collection of buffer towers.
    private Vector<Stack<Integer>> towers; // Collection of all towers.
    private Vector<String> moves; // List for storing moves that have been done.
    static int counter;

    // Main method.
    public static void main(String[] args) {
        int n, t, s, d;
        String myID = "db662";
        if (args.length < 4)
        {
            System.out.printf("Usage: java %s_task1 <n> <t> <s> <d>%n", myID);
            return;
        }
        n = Integer.parseInt(args[0]);  // Read user input n
        t = Integer.parseInt(args[1]);  // Read user input t
        s = Integer.parseInt(args[2]);  // Read user input s
        d = Integer.parseInt(args[3]);  // Read user input d

        // Check the inputs for sanity
        if (n<1 || t<3 || s<1 || s>t || d<1 || d>t)
        {
            System.out.print("Please enter proper parameters. (n>=1; t>=3; 1<=s<=t; 1<=d<=t)\n");
            return;
        }

        // Create the output file name
        String fileName;
        fileName = myID + "_ToH_n" + n + "_t" + t + "_s" + s + "_d" + d + ".txt";
        try {
            // Create the Writer object for writing to "filename"
            FileWriter writer = new FileWriter(fileName);

            // Write the first line: n, t, s, d
            writer.write(n + " " + t + " " + s + " " + d + "\n");

            // Create the object of this class to solve the generalised ToH problem
            Main hanoi = new Main(n, t, s, d);

            hanoi.towerOfHanoi();
            System.out.println(counter);

            for (String move : hanoi.moves) {
                writer.write(move + "\n");
            }

            // Close the file
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("\n");
    }

    /**
     * Constructor of the class Main.
     * @param n Number of discs.
     * @param t Number of towers.
     * @param s Source tower.
     * @param d Destination tower.
     */
    public Main(int n, int t, int s, int d) {
        this.n = n;
        this.t = t;
        this.s = s;
        this.d = d;

        // Add buffers to the buffers list
        this.buffers = new Vector<>();
        for (int i = 1; i <= t; i++) {
            int buffer = 0;
            buffer += i;
            if (buffer != s && buffer != d) {
                this.buffers.add(buffer);
            }
        }

        // Add all towers to the towers list
        this.towers = new Vector<>(t);
        for (int i = 0; i < t; i++) {
            this.towers.add(new Stack<>());
        }

        // Fill destination tower with discs,
        // starting with the largest one - disc n.
        for (int i = n; i > 0; i--) {
            this.towers.get(s-1).push(i);
        }

        moves = new Vector<>();
    }

    /**
     * Call move method.
     */
    public void towerOfHanoi() {
        move(n, t, s, d, buffers);
    }

    /**
     * Solves Tower of Hanoi Problem with arbitrary number of towers and discs (recursively),
     * where n >= 1, and. t >= 3,
     * by moving n discs from source to destination with t-2 buffer towers.
     * Prints all the moves with disc numbers.
     * @param n Number of discs.
     * @param t Number of towers.
     * @param sourceTower Source tower.
     * @param destTower Destination tower.
     * @param buffers List of buffer towers.
     */
    public void move(int n, int t, int sourceTower, int destTower, Vector<Integer> buffers) {
        // If number of discs is 0,
        // do nothing
        if (n == 0) {
            return;
        }
        // If number of discs is 1,
        // move the disc
        if (n == 1) {
            // Make the move and add it in appropriate format to the moves list.
            String move = String.join(" ", printMove(sourceTower, destTower));
            moves.add(move);
            return;
        }
        // Calculate k
        // We will be moving top k discs from source tower.
        // Value of k depends on the number of towers.
        int k;
        if (t == 3) {
            // The problem becomes more trivial if there are only 3 towers.
            k = n-1;
        } else if (t == 4) {
            // This value of k works well if there are only 4 towers.
            k = n - (int) Math.round(Math.sqrt(2 * n + 1)) + 1;
        } else {
            // This value of k works well if there are 5 towers or more.
            k = n/3;
        }
        if (n < t) {
            // Just one disc when there are more towers than discs.
            k = 1;
        }

        // For k, 1<=k<n, transfer the top k discs from source tower
        // to a single tower other than the start or destination towers.
        // using all towers.
        Vector<Integer> buffers_copy = new Vector<>(buffers);
        int buffer1 = buffers_copy.firstElement(); // Use first available buffer tower.
        buffers_copy.set(0, destTower);
        move(k, t, sourceTower, buffer1, buffers_copy);

        // Without disturbing the tower that now contains the top k discs,
        // transfer the remaining n − k discs from source tower
        // to the destination tower, using only the remaining t − 1 towers.
        buffers_copy = new Vector<>(buffers);
        buffers_copy.remove(0); // Remove the tower that now contains the top k discs.
        move(n - k, t - 1, sourceTower, destTower, buffers_copy);

        // Finally, transfer the top k discs to the destination tower,
        // using all towers.
        buffers_copy = new Vector<>(buffers);
        buffers_copy.set(0, sourceTower);
        move(k, t, buffer1, destTower, buffers_copy);
    }

    /**
     * Prints move with disc number.
     * @param start The tower from which the disc will be moved.
     * @param end The tower the disc will be moved to.
     * @return Current move converted into string array format.
     */
    public String[] printMove(int start, int end) {
        int disc = towers.get(start-1).pop(); // Get top disc.
        towers.get(end-1).push(disc); // Put disc on top.
        System.out.printf("Move disc %d from T%d to T%d%n", disc, start, end);
        counter++;
        return new String[]{String.valueOf(disc), String.valueOf(start), String.valueOf(end)};
    }
}