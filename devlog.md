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


  