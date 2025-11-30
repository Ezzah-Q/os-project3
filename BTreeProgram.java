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
        String command = args[0].toLowerCase();

        // switch statement
        switch(command) {
            case "create":
                // if there is no second argument then give error
                if (args.length < 2) {
                    System.err.println("Error: Command needs a filename");
                    return;
                }
                // call create method and pass in filename
                create(args[1]);
                break;

            case "insert":
                // if there is no second, third, fourth command give an error
                if (args.length < 4) {
                    System.err.println("Error: Command needs a filename, key, and value");
                    return;
                }
                // needs file name, key, and value --> converted into unsigned/signed ints
                insert(args[1], Long.parseLong(args[2]), Long.parseLong(args[3]));
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
        try (RandomAccessFile indexFile = new RandomAccessFile(filename, "rw")) {
            // divide file into blocks
            ByteBuffer header = ByteBuffer.allocate(BLOCK_SIZE);
            // put in magic number, root id = 0, and next block id = 1 and write to file
            header.put(MAGIC.getBytes());
            header.putInt(0);
            header.putInt(1);
            indexFile.write(header.array());
        }
    }

    // method for insert logic
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

    // method for search logic
    private static void search(String filename, long key) {
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
         long rootID;
         long nextBlockID;

        // constructor, initializes index file instance with header
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

        // method to write the changed header back to file
        void writeHeader(RandomAccessFile file) throws IOException {
            file.seek(0);
            file.writeBytes(MAGIC);
            file.writeLong(rootID);
            file.writeLong(nextBlockID);
        }

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
            for (int j = midIndex; j < left.keys.size(); j++) {
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

        long search(long key) {
            // search for node base don key
            return key;
        }

        void print() {

        }

        void extract(){

        }

        // method to read a specific node
        BTreeNode readNode(RandomAccessFile file, long blockID) throws IOException {
            // go to index 0 on file
            file.seek(BLOCK_SIZE * blockID);
            // create instance of node class with the specific block ID
            BTreeNode specificNode = new BTreeNode(blockID);
            // read the num of keys it has
            specificNode.read(file);
            return specificNode;
        }

    }

    static class BTreeNode {
        long blockID;
        long parentID;
        long numKeys;

        // list of keys, values, and child pointers for each node
        List<Long> keys = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        List<Long> childPointers = new ArrayList<>();

        // constructor, initialize a new node
        BTreeNode(long blockID) {
            this.blockID = blockID;
            this.parentID = 0;
            this.numKeys = 0;
        }

        void insert(Long key, Long value) {
            // add key and value to corresponding array
            this.keys.add(key);
            this.values.add(value);
            this.numKeys++;
        }

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
            for(int i = 0; i < MAX_DEGREE ; i++) {
                changedNodeInfo.putLong(i < childPointers.size() ? childPointers.get(i) : 0);
            }

            // write to file
            file.write(changedNodeInfo.array());

        }

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
                keys.add(key);
            }

            // get values from array
            for(int i = 0; i < MAX_DEGREE ; i++) {
                long value = nodeInfo.getLong();
                values.add(value);
            }

            // get child pointers from array
            for(int i = 0; i < CHILD_POINTERS ; i++) {
                long child = nodeInfo.getLong();
                childPointers.add(child);
            }
        }


    }
}


