/**
 * B-Tree program that maintains a tree of degree 10 and takes in user commands to...
 * - create an index file
 * - insert a key value pair
 * - search for a key and return the key value pair
 * - load in key value paris from a csv file
 * - print key value pairs from the tree
 * - extract key value pairs from tree to an output file
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BTreeProgram {
    // define some constants
    private static final String MAGIC = "4348PRJ3";
    private static final int BLOCK_SIZE = 512;
    private static final int MAX_DEGREE = 19;
    private static final int CHILD_POINTERS  = 20;

    /**
     * Main method that processes each command
     * @param args user inputs command
     * @throws IOException
     */
    public static void main(String [] args) throws IOException {
        // if there is no command give an error
        if (args.length == 0) {
            System.err.println("Error: There is no command");
            return;
        }

        // take in command + turn it to lowercase
        String command = args[0].toLowerCase();

        // switch statement
        switch(command) {
            case "create":
                // if there is no second argument then give error
                if (args.length < 2) {
                    System.err.println("Error: Command needs a filename");
                    return;
                }
                // call create method
                create(args[1]);
                break;

            case "insert":
                // if there is no second, third, fourth command give an error
                if (args.length < 4) {
                    System.err.println("Error: Command needs a filename, key, and value");
                    return;
                }
                // call insert method
                insert(args[1], Long.parseLong(args[2]), Long.parseLong(args[3]));
                break;

            case "search":
                // if there is no second or third command give error
                if (args.length < 3) {
                    System.err.println("Error: Command needs a filename and key");
                    return;
                }
                // call search method
                search(args[1], Integer.parseInt(args[2]));
                break;

            case "load":
                // if there is no second or third command give error
                if (args.length < 3) {
                    System.err.println("Error: Command needs a filename and csv filename");
                    return;
                }
                // call load method
                load(args[1], args[2]);
                break;

            case "print":
                // if there is no second argument then give error
                if (args.length < 2) {
                    System.err.println("Error: Command needs a filename");
                    return;
                }
                // call print method
                print(args[1]);
                break;

            case "extract":
                // if there is no second or third command give error
                if (args.length < 3) {
                    System.err.println("Error: Command needs a filename and another filename");
                    return;
                }
                // call extract method
                extract(args[1], args[2]);
                break;

            default:
                System.err.println("Error: Unknown command");
        }
    }

    /**
     * method that initiates an index file
     * @param filename name of index file to be created
     * @throws IOException
     */
    private static void create(String filename) throws IOException {
        // if file already exists, fail with error message
        Path filePath = Paths.get(filename);
        if (Files.exists(filePath)) {
            System.err.println("Error: File already exists");
            return;
        }

        // create an index file here and write in header
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw")) {
            // divide file into blocks
            ByteBuffer header = ByteBuffer.allocate(BLOCK_SIZE);
            // put in magic number, root id = 0, and next block id = 1 and write to file
            header.put(MAGIC.getBytes());
            header.putLong(0);
            header.putLong(1);
            indexFile.write(header.array());
        }
    }

    /**
     * method that creates instance of B-Tree in order to insert key value pair
     * @param filename name of index file
     * @param key key to insert
     * @param value value to insert
     * @throws IOException
     */
    private static void insert(String filename, long key, long value) throws IOException {
        // if file doesn't already exist or has incorrect index, fail with error message
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        // create instance of file, pass it to instance of bTree, and insert key/value
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw")) {
            BTree tree = new BTree(indexFile);
            tree.insert(indexFile, key, value);
        }
    }

    /**
     * method that creates an instance of B-Tree in order to search a given key and return value
     * @param filename name of index file
     * @param key key to search
     */
    private static void search(String filename, long key) throws IOException {
        // If the file doesn't exist or if the file is not a valid index file then exit with an error.
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        // create instance of file, pass it to instance of bTree, and search for key
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw")) {
            BTree tree = new BTree(indexFile);
            Long value = BTree.search(indexFile, key);
            // print out the returned value if it exists, give error if it doesn't
            if (value != null) {
                System.out.println(key + "," + value);
            } else {
                System.err.println("Error: key was not found");
            }
        }
    }

    /**
     * method that reads each line from a csv file and calls insert method
     * @param filename name of index file
     * @param csvFile name of csv file
     */
    private static void load(String filename, String csvFile) throws IOException {
        String line;

        //If the file does not exist or if the file is not a valid index file then exit with an error.
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        //If the csv file does not exist, the exit with an error message
        Path csvFilePath = Paths.get(csvFile);
        if (!Files.exists(csvFilePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        // read each line from csv file and call insert command
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw");
             BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            BTree tree = new BTree(indexFile);
            // as long as there is a line
            while ((line = reader.readLine()) != null) {
                // Each line of the file is a comma separated key/value pair
                String[] pairParts = line.split(",");
                // extract the key and value from the array
                if (pairParts.length >= 2) {
                    long keyValue = Long.parseLong(pairParts[0].trim());
                    long val = Long.parseLong(pairParts[1].trim());
                    tree.insert(indexFile, keyValue, val);
                }
            }
        }
    }

    /**
     * method that creates an instance of B-Tree in order to print key value pairs
     * @param filename name of index file to print
     */
    private static void print(String filename) throws IOException{
        // If the file does not exist or if the file is not a valid index file then exit with an error
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        // create btree instance and pass in index file, call print method
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw")) {
            BTree tree = new BTree(indexFile);
            tree.print(indexFile);
        }
    }

    /**
     * method that outputs key value paris to a file
     * @param filename name of index file
     * @param outputFile name of file to output to
     */
    private static void extract(String filename, String outputFile) throws IOException{
        // If the file does not exist or if the file is not a valid index file then exit with an error.
        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist");
            return;
        }

        //If the second file exists, exit with an error message
        Path outputFilePath = Paths.get(outputFile);
        if (Files.exists(outputFilePath)) {
            System.err.println("Error: File already exists");
            return;
        }

        //The file should remain unmodified. Save every key/value pair in the index as comma separated pairs to the file.
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw");
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            BTree tree = new BTree(indexFile);
            tree.extract(indexFile, writer);
        }
    }

    /**
     * Tree class that handles actions regarding the index file the tree is on and actions that change structure of tree
     */
    static class BTree {
        static long rootID;
        static long nextBlockID;

        /**
         * BTree constructor, reads in the file header
         * @param indexFile instance of file
         * @throws IOException
         */
        BTree (RandomAccessFile indexFile) throws IOException {
            // go to index 0 on file
            indexFile.seek(0);
            // take in first 8 bytes and read them into an array representing magic number
            byte[] magicBytes = new byte[8];
            indexFile.readFully(magicBytes);
            // read the next 2 longs and save it as the root ID and next block ID
            rootID = indexFile.readLong();
            nextBlockID = indexFile.readLong();
        }

        /**
         * method to write header to index file after any changes, such as insertions
         * @param file instance of file
         * @throws IOException
         */
        void writeHeader(RandomAccessFile file) throws IOException {
            file.seek(0);
            file.writeBytes(MAGIC);
            file.writeLong(rootID);
            file.writeLong(nextBlockID);
        }

        /**
         * method to insert key value pair in a node
         * two ways to insert --> if tree has no root, or if tree already has root
         * @param file instance of index file
         * @param key key to insert
         * @param value value to insert
         * @throws IOException
         */
        void insert(RandomAccessFile file, long key, long value) throws IOException {
            // if tree is empty, insert new node as the root
            if (rootID == 0) {
                // create a root node instance
                BTreeNode root = new BTreeNode(nextBlockID);

                // insert key value pair into the node's array and write to file
                root.insert(key, value);
                root.write(file);

                // increase the root ID and next block ID
                rootID = nextBlockID;
                nextBlockID++;

                // write the changed header back to file
                writeHeader(file);
            } else {
                // Get root info (# of keys) by returning an instance of node class
                BTreeNode root = readNode(file, rootID);
                // if root number of keys is greater than 19, you have to split root
                if (root.numKeys >= 19) {
                    // create a new root node
                    BTreeNode newRoot = new BTreeNode(nextBlockID);
                    // insert the old root ID as a child in the new root
                    newRoot.childPointers.add(rootID);
                    // this should be a new method splitOldRoot
                    splitRoot(file, newRoot, 0);
                    // write to file
                    newRoot.write(file);
                    // increase the root ID and next block ID
                    rootID = nextBlockID;
                    nextBlockID++;
                    // insert key and value normally --> create new method
                    insertNormally(file, newRoot, key, value);
                } else {
                    // insert key and value normally in root --> create new method
                    insertNormally(file, root, key, value);
                }
                // write the changed header back to file
                writeHeader(file);
            }
        }

        /**
         * method to insert a key value pair in a node that still has space
         * two ways to insert --> into a node with children and one without
         * @param file instance of file
         * @param node instance of node we are inserting into
         * @param key key to insert
         * @param value value to insert
         * @throws IOException
         */
        void insertNormally(RandomAccessFile file, BTreeNode node, long key, long value) throws IOException {
            // set key size to i
            int i = node.keys.size() - 1;
            // if the node to insert key is a leaf node
            if (node.childPointers.isEmpty()) {
                // start from end and work backward and key is less than current key
                while (i >= 0 && key < node.keys.get(i)) {
                    i--;
                }
                // you found the place to insert value
                // if given key equals current key, set that place in array to given value
                if (i >= 0 && key == node.keys.get(i)) {
                    node.values.set(i, value);
                } else {
                    // if given key doesn't equal current key, then place value one after current index
                    node.keys.add(i + 1, key);
                    node.values.add(i + 1, value);
                }
                // update the amount of keys and write to file
                node.numKeys = node.keys.size();
                node.write(file);

            // if the node to insert key has children
            } else {
                // start from end and work backward until key is greater than current key
                // i will tell us that given key is greater then key[i]
                while (i >= 0 && key < node.keys.get(i)) {
                    i--;
                }
                // increment i to get the child pointer (in between keys)
                i++;
                // get info from child node of blockID i
                BTreeNode child = readNode(file, node.childPointers.get(i));
                // see if child is full, if it is split it
                if (child.keys.size() >= 19) {
                    splitRoot(file, node, i);
                    // decide if key belongs in left or right child
                    // if key greater than i (mid) increment i go to right child
                    if (key > node.keys.get(i)) {
                        i++;
                    }
                    // reread correct child since after split, child pointers could've changed
                    child = readNode(file, node.childPointers.get(i));
                }
                // recursive call back to function
                insertNormally(file, child, key, value);
            }
        }

        /**
         * method that splits the child node of the parent node we pass in
         * @param file instance of file
         * @param parent instance of node we pass in
         * @param i refers to child pointer index
         * @throws IOException
         */
        void splitRoot(RandomAccessFile file, BTreeNode parent, int i) throws IOException {
            // create left child instance of passed node
            BTreeNode left = readNode(file, parent.childPointers.get(i));
            // create a right child instance of passed node
            BTreeNode right = new BTreeNode(nextBlockID);
            nextBlockID++;

            // get middle key index from left node
            int midIndex = left.keys.size() / 2;
            // get mid key and mid value
            long midKey = left.keys.get(midIndex);
            long midValue = left.values.get(midIndex);

            // loop through left node from mid to end and put those values in right node
            for (int j = midIndex + 1; j < left.keys.size(); j++) {
                right.keys.add(left.keys.get(j));
                right.values.add(left.values.get(j));
            }

            // loop through left node from mid to end and put those values in right node
            if (!left.childPointers.isEmpty()) {
                for (int j = midIndex + 1; j < left.childPointers.size(); j++) {
                    right.childPointers.add(left.childPointers.get(j));
                }
            }

            // set num of keys to right amount
            right.numKeys = right.keys.size();
            right.write(file);

            // delete from mid-index to end from left child instance
            left.keys.subList(midIndex, left.keys.size()).clear();
            left.values.subList(midIndex, left.values.size()).clear();
            if(!left.childPointers.isEmpty()) {
                left.childPointers.subList(midIndex + 1, left.childPointers.size()).clear();
            }

            // set num of keys to right amount
            left.numKeys = left.keys.size();
            left.write(file);

            // write in the new parent info
            parent.keys.add(i, midKey);
            parent.values.add(i, midValue);
            parent.childPointers.add(i + 1, right.blockID);
            parent.numKeys = parent.keys.size();
            parent.write(file);
        }

        /**
         * method to search for key
         * @param indexFile instance of index file
         * @param key key to search
         * @return
         */
        static Long search(RandomAccessFile indexFile, long key) throws IOException {
            // if tree is empty return null
            if (rootID == 0) return null;

            // store the current rootID
            long currentID = rootID;

            // loop through the nodes of the tree
            while (true) {
                BTreeNode node = readNode(indexFile, currentID);
                int i = 0;

                // loop through each key in the current node until given key is less than current key
                while (i < node.keys.size() && key > node.keys.get(i)) {
                    i++;
                }

                // if the keys are equal return the value
                if (i < node.keys.size() && key == node.keys.get(i)) {
                    return node.values.get(i);
                }

                // if the node does not have children, return null
                if (node.childPointers.isEmpty()) return null;

                // if they aren't equal and have a child, move down to child and search
                currentID = node.childPointers.get(i);
            }
        }

        /**
         * helper method to call the recursive print method
         * @param indexFile instance of index file
         */
        void print(RandomAccessFile indexFile) throws IOException {
            // if tree is empty return null
            if (rootID == 0) return;

            // if tree is not empty call printRecursive
            printRecursive(indexFile, rootID);
        }

        /**
         * recursive method to print out keys and values of current node
         * @param file instance of index file
         * @param currentNodeID current node to loop through
         */
        void printRecursive(RandomAccessFile file, long currentNodeID) throws IOException{
            BTreeNode node = readNode(file, currentNodeID);

            // loop through the keys of the node and print each one out
            for ( int i = 0; i < node.keys.size(); i++) {
                System.out.println(node.keys.get(i) + ", " + node.values.get(i));
            }

            // loop through the children of current node and recurse back to print their keys/values
            for (long childID : node.childPointers) {
                printRecursive(file, childID);
            }
        }

        /**
         * helper method to call the recursive extract method
         * @param indexFile instance of index file
         * @param writer instance of print writer
         */
        void extract(RandomAccessFile indexFile, PrintWriter writer) throws IOException {
            // if tree is empty return null
            if (rootID == 0) return;

            // if tree is not empty call extractRecursive
            extractRecursive(indexFile, writer, rootID);
        }

        /**
         * recursive method to extract the keys and values from current node
         * @param file instance of index file
         * @param writer instance of print writer
         * @param nodeID id of node to extract from
         */
        void extractRecursive(RandomAccessFile file, PrintWriter writer, long nodeID) throws IOException {
            BTreeNode node = readNode(file, nodeID);

            // loop through the keys of the node and send each one to output file
            for ( int i = 0; i < node.keys.size(); i++) {
                writer.println(node.keys.get(i) + ", " + node.values.get(i));
            }

            // loop through the children of current node and recurse back to print their keys/values
            for (long childID : node.childPointers) {
                extractRecursive(file, writer, childID);
            }
        }

        /**
         * method to read how many keys a node has and return the instance
         * @param file instance of index file
         * @param blockID id for node we want to read
         * @return returns a node instance
         * @throws IOException
         */
        static BTreeNode readNode(RandomAccessFile file, long blockID) throws IOException {
            // go to index 0 on file
            file.seek(BLOCK_SIZE * blockID);
            // create instance of node class with the specific block ID
            BTreeNode specificNode = new BTreeNode(blockID);
            // read the num of keys it has
            specificNode.read(file);
            return specificNode;
        }
    }

    /**
     * class that handles individual node actions
     */
    static class BTreeNode {
        long blockID;
        long parentID;
        long numKeys;

        // list of keys, values, and child pointers for each node
        List<Long> keys = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        List<Long> childPointers = new ArrayList<>();

        /**
         * initializes a new node
         * @param blockID current node id
         */
        BTreeNode(long blockID) {
            this.blockID = blockID;
            this.parentID = 0;
            this.numKeys = 0;
        }

        /**
         * inserts the key and value into the array lists
         * @param key key to insert
         * @param value value to insert
         */
        void insert(Long key, Long value) {
            // add key and value to corresponding array
            this.keys.add(key);
            this.values.add(value);
            this.numKeys++;
        }

        /**
         * write changes back to index file
         * @param file instance of index file
         * @throws IOException
         */
        void write(RandomAccessFile file) throws IOException {
            // indicate the needed block size
            file.seek(blockID * BLOCK_SIZE);
            ByteBuffer changedNodeInfo = ByteBuffer.allocate(BLOCK_SIZE);

            // put info into the array
            changedNodeInfo.putLong(blockID);
            changedNodeInfo.putLong(parentID);
            changedNodeInfo.putLong(numKeys);

            // loop through each array list and insert current key if their index is less than 19
            for(int i = 0; i < MAX_DEGREE ; i++) {
                changedNodeInfo.putLong(i < keys.size() ? keys.get(i) : 0);
            }

            // loop through each array list and insert current key if their index is less than 19
            for(int i = 0; i < MAX_DEGREE ; i++) {
                changedNodeInfo.putLong(i < values.size() ? values.get(i) : 0);
            }
            // loop through each array list and insert current key if their index is less than 19
            for(int i = 0; i < CHILD_POINTERS ; i++) {
                changedNodeInfo.putLong(i < childPointers.size() ? childPointers.get(i) : 0);
            }

            // write to file
            file.write(changedNodeInfo.array());
        }

        /**
         * read node info from the index file
         * @param file
         * @throws IOException
         */
        void read(RandomAccessFile file) throws IOException {
            // indicate the needed block size
            ByteBuffer nodeInfo = ByteBuffer.allocate(BLOCK_SIZE);
            file.readFully(nodeInfo.array());

            // get info from array node
            blockID = nodeInfo.getLong();
            parentID = nodeInfo.getLong();
            numKeys = nodeInfo.getLong();

            // get keys from array
            for(int i = 0; i < MAX_DEGREE ; i++) {
                long key = nodeInfo.getLong();
                // only add key if i is less than numKeys
                if (i < numKeys) keys.add(key);
            }

            // get values from array
            for(int i = 0; i < MAX_DEGREE ; i++) {
                long value = nodeInfo.getLong();
                // only add key if i is less than numKeys
                if (i < numKeys) values.add(value);
            }

            // get child pointers from array
            for(int i = 0; i < CHILD_POINTERS ; i++) {
                long child = nodeInfo.getLong();
                // only add non-zero children
                if (child != 0) childPointers.add(child);
            }
        }
    }
}


