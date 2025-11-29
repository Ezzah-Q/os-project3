import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BTreeProgram {
    // define some constants
    private static final String MAGIC = "4348PRJ3";
    private static final int BLOCK_SIZE = 512;
    private static final int MIN_DEGREE = 10;
    private static final int MAX_DEGREE = 19;
    private static final int CHILD_POINTERS  = 20;

    public static void main(String [] args) throws IOException {
        // if there is no command give an error
        if (args.length == 0) {
            System.err.println("Error: There is no command");
            return;
        }

        // take in command + turn it to lowercase
        String command = args[0];

        // switch statement
        switch(command) {
            case "create":
                // if it already exists return
                // if there is no command give error
                // call create method and pass in filename
                create(args[1]);
                break;
            case "insert":
                // needs file name, key, and value --> converted into unsigned/signed ints
                insert(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                break;
            case "search":
                // needs file name and key --> converted into unsigned/signed ints
                search(args[1], Integer.parseInt(args[2]));
                break;
            case "load":
                // needs file name, and csv file name
                load(args[1], args[2]);
                break;
            case "print":
                // needs file name
                print(args[1]);
                break;
            case "extract":
                // needs file name and another file name
                extract(args[1], args[2]);
                break;
            default:
                System.err.println("Error: Unknown command");
        }
    }

    // method for create logic
    private static void create(String filename) throws IOException {
        // if file already exists, fail with error message
        Path filePath = Paths.get(filename);
        if (Files.exists(filePath)) {
            System.err.println("Error: File already exists");
            return;
        }

        // create an index file here and write in header
        try { RandomAccessFile indexFile = new RandomAccessFile(filename, "rw");
            // divide file into blocks
            ByteBuffer header = ByteBuffer.allocate(BLOCK_SIZE);
            // put in magic number, root id, and next block id
            header.put(MAGIC.getBytes());
            header.putInt(0);
            header.putInt(1);
            // write to the file
            indexFile.write(header.array());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // method for insert logic
    private static void insert(String filename, int key, int value) throws IOException {
        // if file doesn't already exist or has incorrect index, fail with error message
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        // create instance of file and pass it to instance of bTree
        RandomAccessFile indexFile = new RandomAccessFile(filename, "rw");
        BTree tree = new BTree(indexFile);
        tree.insert(key, value);
    }

    // method for search logic
    private static void search(String filename, int key) {
        // If the file doesn't exist or if the file is not a valid index file then exit with an error.
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        // create instance of bTree

    }

    // method for load logic
    private static void load(String filename, String csvFile) {
        //If the file does not exist or if the file is not a valid index file then exit with an error.
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        //If the csv file does not exist, the exit with an error message

    }

    // method for print logic
    private static void print(String filename) {
        // If the file
        //does not exist or if the file is not a valid index file then exit with an error
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

    }

    // method for extract logic
    private static void extract(String filename, String outputFile) {
        // If the
        //file does not exist or if the file is not a valid index file then exit with an error.
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        //If the second file exists, exit with an error message.
    }


    static class BTree {
         int rootID;
         int nextBlockID;


        // constructor
        BTree (RandomAccessFile indexFile) throws IOException {
            indexFile.seek(0);
            byte[] byteArray = new byte[8];
            indexFile.get(byteArray);
        }

        void insert(int key, int value) {
            // if tree is empty (rootid == 0) then create the first node
            // call node instance
            // if root is full, crate new root and split old one
            // if root has space just put it in
                // key can be a leaf or internal node
        }

        int search(int key) {
            // search for node base don key
            return key;
        }

        void print() {

        }

        void extract(){

        }
    }

    class BTreeNode {
        int blockID;
        int parentID;
        int numKeys;

        List<int> keys = new ArrayList<>();
        List<int> values = new ArrayList<>();
        List<int> childPointers = new ArrayList<>();

        BTreeNode() {

        }
    }
}
