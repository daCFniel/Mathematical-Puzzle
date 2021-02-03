import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
/**
 * This program implements an application that
 * checks the correctness of a solution
 * for the Generalised Tower of Hanoi problem.
 * It reads the specified solution from the external txt file.
 * Program uses pre-written data structures Stack and Vector.
 * @author Daniel Bielech ~ db662
 * @version 07.12.2020
 */
public class CorrectnessCheck {

    static int n; // Number of discs.
    static int t; // Number of towers.
    static int s; // Source tower.
    static int d; // Destination tower.
    static Vector<Stack<Integer>> towers; // Collection of all towers.
    static boolean isCorrect; // Tells if the sequence that is being checked is correct or not.
    static HashSet<Integer> usedTowers; // Collection of all towers that have been using during the sequence of moves.

    // Main method.
    public static void main(String[] args) {
        String fileName;
        String myID = "db662";

        // Check if the input filename has been provided as an argument
        if (args.length < 1) {
            System.out.printf("Usage: java %s_task2 <file_name>%n", myID);
            return;
        }

        try {
            // Get the filename
            fileName = args[0];
            System.out.print("Reading the file " + fileName + "\n");

            // Create the object for reading the input file
            FileInputStream input_file = new FileInputStream(fileName);

            // Read the four parameters in the first row of the input file
            n = getNextInteger(input_file);
            t = getNextInteger(input_file);
            s = getNextInteger(input_file);
            d = getNextInteger(input_file);

            // Add all towers to the towers list
            towers = new Vector<>(t);
            for (int i = 0; i < t; i++) {
                towers.add(new Stack<>());
            }

            // Fill destination tower with discs,
            // starting with the largest one - disc n.
            for (int i = n; i > 0; i--) {
                towers.get(s-1).push(i);
            }

            // Initialise the set
            usedTowers =  new HashSet<>();

            // Check the sequence of moves as long as this variable is true
            isCorrect = true;

            System.out.println("\nThe status of all the towers at the start is as follows:");
            showStatusAll();

            while (input_file.available() > 0) {
                int disc = getNextInteger(input_file); // Get disc number for current move
                int start = getNextInteger(input_file); // Get source tower number for current move
                int end = getNextInteger(input_file); // Get destination tower number for current

                // Keep track what towers have been used.
                usedTowers.add(start);
                usedTowers.add(end);

                System.out.println();
                showMove(disc, start, end);

                // -- Correctness check #1 --
                // Check if disc being moved is out of range.
                if(checkOutOfRangeDisc(disc)) {
                    System.out.printf("Move error: The disc %d is out of range", disc);
                    isCorrect = false;
                    break;
                }

                // -- Correctness check #2 --
                // Check if source tower is out of range.
                if(checkOutOfRangeTower(start)) {
                    System.out.printf("Move error: The source tower %d is out of range", start);
                    isCorrect = false;
                    break;
                }
                // Check if destination tower out of range.
                if(checkOutOfRangeTower(end)) {
                    System.out.printf("Move error: The destination tower %d is out of range", end);
                    isCorrect = false;
                    break;
                }

                System.out.println("Before the move:");
                showStatusTwoTowers(start, end);
                System.out.println();

                // -- Correctness check #3 --
                // If disc being moved is not at the top of source tower,
                // print the error message and
                // stop checking the sequence.
                if (!checkTopDisc(disc, start)) {
                    System.out.printf("Move error: Disk %d is not at the top of the source tower %d", disc, start);
                    isCorrect = false;
                    break;
                }

                // Do the actual move.
                // -- Correctness check #4 --
                // If the move happened to be illegal
                // (Putting bigger disc on top of smaller disc),
                // print the error message and,
                // stop checking the sequence.
                if(!doMove(start, end)) {
                    System.out.printf("Move error: Destination tower has a smaller disc than %d on the top", towers.get(start-1).peek());
                    isCorrect = false;
                    break;
                }

                System.out.println("After the move:");
                showStatusTwoTowers(start, end);
                System.out.println();
            }


            // -- Correctness check #5 --
            // Check If all the towers except the destination tower are empty.
            // If they are not empty, print the error message.
            if(isCorrect) {
                if (!checkIfOthersEmpty(d)) {
                    System.out.printf("%nSequence error: All moves were executed, so all towers except the destination tower %d should be empty", d);
                    isCorrect = false;
                }
            }

            // -- Correctness check #6 --
            // If number of discs is greater or equal to number of towers.
            // Check whether all the towers have been used to move the discs.
            if(isCorrect) {
                if (n >= t) {
                    if (usedTowers.size() < t) {
                        StringBuilder towers = new StringBuilder();
                        for (int i = 1; i <= t; i++) {
                            if (!usedTowers.contains(i)) {
                                towers.append(i).append(" ");
                            }
                        }
                        System.out.println("\nSequence error: Towers numbered: " + towers + "have not been used!");
                        isCorrect = false;
                    }
                }
            }

            if(isCorrect) {
                System.out.println("\nThe status of all the towers at the end is as follows:");
                showStatusAll();
                System.out.println("\nThe sequence of moves is correct.");
            } else {
                System.out.println("\n\nThe status of all the towers is as follows:");
                showStatusAll();
                System.out.println("\nThe sequence of moves is incorrect.");
            }


            // Close the file
            input_file.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("\n");
    }

    /**
     * Get the next integer number.
     * Works assuming the file has positive integers.
     * @param input_file File object.
     * @return Next integer number.
     */
    public static int getNextInteger(FileInputStream input_file) {
        int character;
        int digit;
        int number = 0;
        try {
            while ((character = input_file.read()) != -1 && !isBlank(character)) {
                number *= 10;
                digit = character - '0';
                number += digit;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return number;
    }

    /**
     * Checks if a given is a blank space.
     * @param character Character to be checked.
     * @return True if given character is blank space, false otherwise.
     */
    public static boolean isBlank (int character) {
        return character == ' ' || character == '\t' || character == '\n' || character == '\r';
    }

    /**
     * Prints a current move.
     */
    public static void showMove(int disc, int start, int end) {
        System.out.printf("Move: disc %d from tower %d to tower %d%n", disc, start, end);
    }

    /**
     * Checks if the given disc is indeed at the top of the given tower.
     * @param disc Disc number to be checked.
     * @param tower Tower number to be checked.
     * @return True if given disc is at the top of given tower, false otherwise.
     */
    public static boolean checkTopDisc(int disc, int tower) {
        int topDisc = towers.get(tower-1).peek();
        return topDisc == disc;
    }

    /**
     * Does the move.
     * Checks correctness of the move before doing the move.
     * If move is correct, does the move.
     * @return True if the move has been done, false otherwise.
     */
    public static boolean doMove(int start, int end) {
        // Get the value of the top disc on the start tower.
        int startTopDisc = towers.get(start-1).peek();
        int destTopDisc;
        if (towers.get(end-1).size() == 0) {
            // If tower is empty, allow every disc to be put on it.
            destTopDisc = Integer.MAX_VALUE;
        } else {
            // If tower is not empty, check what disc is on the top.
            // Get the value of the top disc on the destination tower.
            destTopDisc = towers.get(end-1).peek();
        }
        // Check if top disc on the destination tower
        // is smaller than disc that is being moved.
        // If it is, just return false.
        // Otherwise, do the move and return true.
        if (destTopDisc < startTopDisc) {
            return false;
        } else {
            int disc = towers.get(start-1).pop(); // Get top disc.
            towers.get(end-1).push(disc); // Put disc on top.
            return true;
        }
    }

    /**
     * Prints status of two towers specified in parameters.
     * @param source The first tower whose status is to be printed.
     * @param dest The second tower whose status is to be printed.
     */
    public static void showStatusTwoTowers(int source, int dest) {
        System.out.printf("Source tower %d: ", source);
        towers.get(source-1).forEach(result -> System.out.print(result + " "));
        System.out.println();
        System.out.printf("Destination tower %d: ", dest);
        towers.get(dest-1).forEach(result -> System.out.print(result + " "));
    }

    /**
     * Prints current status of all towers.
     */
    public static void showStatusAll(){
        for(int i=0;i<t;i++){
            System.out.print("Tower "+(i+1)+": ");
            towers.get(i).forEach(result -> System.out.print(result + " "));
            System.out.println();
        }
    }

    /**
     * Checks if all the towers except the given tower are empty.
     * @param tower Given tower number.
     * @return False if any of the towers, apart from the given tower, is not empty, true otherwise.
     */
    public static boolean checkIfOthersEmpty(int tower) {
        for(int i=0;i<t;i++){
            if (towers.get(i).size() > 0 && i+1 != tower) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if given tower is out of range (1, number of all towers)
     * @param tower Given tower.
     * @return True if given tower is out of range, false otherwise.
     */
    public static boolean checkOutOfRangeTower(int tower) {
        return 1 > tower || tower > t;
    }

    /**
     * Checks if given disc is out of range (1, number of all discs)
     * @param disc Given disc.
     * @return True if given disc is out of range, false otherwise.
     */
    public static boolean checkOutOfRangeDisc(int disc) {
        return 1 > disc || disc > n;
    }
}



