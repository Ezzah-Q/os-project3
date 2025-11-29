11-27-25 4:45 pm
- first devlog entry
- here is what I know:
  - program will accept command line arguments for operations on an index file (b-tree)
  - so the index file is the physical file on disk and the b tree is inside of it?
  - file is divided into 512 byte blocks, and each node of the btree is in one block
  - block 0 would be the header (24 bytes) rest would be nodes
  - numbers stored in file will use 8 byte endian values (Java)
  - minimum degree for b tree is 10, max 19 keys oer node and 20 child pointers
    - child pointers are block IDs not memory addresses
  - cant load entire tree, should only have 3 nodes at a time
- what I am thinking:
  - I will write the program in java
  - I should have a main class that reads the command line argument and sees what it is
  - To handle the command logic I'll use a switch statement, that will call the command methods
  - I will also have another class for dealing with the tree structure
  - and another class that sets up each tree node

11-27-25 5:08 pm
- before session
- I plan on mapping the program out, maybe create pseudocode
- for the main class I plan on implementing a switch statement for the different commands
- I won't write out the methods for the commands just yet, maybe pseudocode them

11-27-25 5:35 pm
- after session
- ended up roughly coding the command processing
- still need to implement the command logic 
- need to think about how to create a b-tree
  - I am guessing I need to section off an index file into the 512 blocks?
  - I will do some thinking and figure out to implement b tree in next session
  - I will also write pseudocode for it

11-28-25 9:13 pm
- before session
- I researched a bit more on how I would make a btree
  - create a file using Java's RandomAccessFile and create a header
    - this is for create command
  - on insertion, we open that file, read header, and start creating a btree
  - in the class for b-tree, I will have to create helper methods that can be called from the main class
    - these helper methods will deal with the actual insertion etc of the tree, and i will call b-treenode class here
- for today's session I will try to map out the rest of the program
- maybe write pseudocode for the class methods
- if I have time I will begin to write actual code

11-28-25 11:42 pm
- after session
- used Files and Paths.get() to get file path to see if a file does or doesnt exist
- will use bytebuffer to create the 512 byte chunks in the file
  - use its .allocate(), .put(), etc functions
- for insert method I passed random access file instance as a parameter
  - so Btree class is using the same instance
- Btree class constructor will instantly read the files header so it can insert new node
  - use .seek() and .read
- today's session I mapped out rest of the program
- Started pseudocode/actual code for the main program
  - more specifically worked on create and insert methods for main and Btree class instantiation logic
- need to figure out how to read header from file
- next session I plan to continue working on inserting a node logic

11-29-25 10:52 am
- before session
- I realized that a Java int is only 4 Bytes, I will switch to using long data type
- today I will continue to work on create and insert methods and try to implement the node class

11-29-25 2:44 pm
- after session
- make sure input command turned to lowercase before processing
- to read the magic number from the header decide to create a ByteArray and use readFully() to read all 8 bytes from file header
- if I make changes to the tree (like add a new node) the header would change
  - I wrote a method that writes the changed header back to the file
- added another insert method in the node class to insert key and value in node's arrays
- in order to read information from a root, I need another method that reads the node and returns that instance
- searched up how to insert a key on full node
- created another method for splitting root and inserting a node normally since a lot of repetition would than happen in insert method
- for splitting the root, will use subList method to get portion of list
- overall, for this session I continued insert method, adding helper methods such as splitRoot and insertNormally
- next session I will finish method insertNormally

11-29-25 4:49 pm
- before session
- I will continue to work on insert method
- I need to write code for handling an insertion in a node with no children (easy)
  - and the ace for a node with children (recursive call)
  