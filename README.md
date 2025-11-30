# os-project3

# B-Tree Program
This Java program takes in user commands from command line to create, manage, and search a B-Tree index file.
B-Tree is stored on a file in 512-byte blocks, with a minimum degree of 10, a maximum of 19 keys per node, and 20 child pointers per node.

# Requirements
java 8+ and basic terminal/command-line 

# Compiling code
first compile the program: javac BTreeProgram.java

# Running commands
user can type in these commands...

- java BTreeProgram create filename.idx
- java BTreeProgram insert filename.idx key# value#
- java BTreeProgram search filename.idx key#
- java BTreeProgram load filename.idx filename.csv
- java BTreeProgram print filename.idx
- java BTreeProgram extract filename.idx filename.csv

make sure to put java in front of each command
