public class BTreeProgram {
    // define some constants
    private static final String MAGIC = "4348PRJ3";
    private static final int BLOCK_SIZE = 512;
    private static final int MIN_DEGREE = 10;
    private static final int MAX_DEGREE = 19;
    private static final int CHILD_POINTERS  = 20;

    public static void main(String [] args) {
        // if there is no command give an error
        if (args.length == 0) {
            System.err.println("Error: There is no command");
            return;
        }

        // take in command
        String command = args[0];

        // switch statement
        switch(command) {
            case "create":
                // needs an argument after it,m the index file
                // if it already exists return
                // if there is no command give error
                // call create method
                break;
            case "insert":
                // needs file name, key, and value --> converted into unsigned/signed ints
                break;
            case "search":
                // needs file name and key --> converted into unsigned/signed ints
                break;
            case "load":
                // needs file name, and csv file name
                break;
            case "print":
                // needs file name
                break;
            case "extract":
                // needs file name and another file name
                break;
            default:
        }
    }

    // method for create logic
    private static void create(String filename) {

    }

    // method for insert logic
    private static void insert(String filename, int key, int value) {

    }

    // method for search logic
    private static void search(String filename, int key) {

    }

    // method for load logic
    private static void insert(String filename, String csvFile) {

    }

    // method for print logic
    private static void print(String filename) {

    }

    // method for extract logic
    private static void extract(String filename, String outputFile) {

    }
}
