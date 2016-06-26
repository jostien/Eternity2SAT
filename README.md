Eternity2SAT - A SAT-attempt to solve Eternity II 
-------------------------------------------------

Eternity2SAT produces SAT-constraints for Eternity II. There are two variants:

0. A small example of size 6x6 (MainSmallExample.java). Can be solved by any solver in a few seconds. Tiles are encoded by strings similar to "\_AH\_", colors are encoded by different characters like "\_" for gray (boarder) and "A" or "H" for an inner color. The first character is the north-, the second is the east-, the third is the south- and the fourth is the west-triangle of a tile. One/the solution for the example is then given by:
  ```
  _AH_ _DBA _CAD _FJC _BAF __CB
  HGJ_ BCAG ADBC JDED ABJD C_JB
  JCI_ AIBC BDAI ECFD JHEC J_IH
  IHD_ BCAH AFEC FGAF EHCG I_FH
  DFJ_ ADBF EGID AGBG CBDG F_EB
  JG__ BJ_G IA_J BC_A DD_C E__D
  ```
For this solution the tiles are numbered as follows (see ordering of tile-strings in MainSmallExample.java):
   ```
    4  5  6  7  8  1
    9 21 22 23 24 10
   11 25 26 27 28 12
   13 29 30 31 32 14
   15 33 34 35 36 16
    2 17 18 19 20  3
   ```

1. A pragmatic version (Eternity2SAT), which produces constraints based on selection variables, e.g., there are 256 binary variables to define where to put each tile and the sum of these variables has to sum up to 1. This produces a constraints-file of size 890 MB, which can hardly be handled by a solver. However, size of constraints can be reduced by simplification via [cryptominisat](https://github.com/msoos/cryptominisat) using the default values for preprocessing to 180 MB (see below).

2. A more economic version (Eternity2SATsmall), which, e.g., codes the position a tile is put on via an 8 bit number. Each number from 0 to 255 has to be used. This produces a contraints-file of size 420 MB. The size can be reduced via cryptominsat using the default values for preprocessing to 114 MB (see below). This version is not checked for bugs in constraints.

3. It is very likely, that the constraints can be improved further.

Some theoretical thoughts
-------------------------

Eternity II was designed to have as few solutions as possible. It looks on the first sight as being a kind of random generated problem. It is well known, that for randomly generated problems of large size, local search approaches like WalkSAT are more suited, provided a satisfying solution exists. Some test runs suggest, that the algebraic approach will always fail, because the number of constraints is very large producing a lot of learnt information about the problem so that memory will always be the limiting factor. Local search only needs to keep track of the constrains and variable state. Especially in the parallel case, the constraints have to be mapped to memory only once and for each thread only the variables have to be handled. For a GPU-version of WalkSAT, see [MarchSAT](https://github.com/jostien/MarchSAT). 

Examples
--------

How to use Eternity2SAT:

1. Run MainEternity2SAT.java, which should produce a constraints file of size around 890 MB.

2. Then:
   1. Wait until solver finishes (which is unlikely but you can try).
   2. Stop solver and program and keep the constraints-file.

3. Then:
   1. Run
      ```
      ./cryptominisat4 --preproc 1 <path_to_constrains_file> <path_to_constraints_file>.simplified
      ```
      from within the cryptominisat4-installation directory, which rewrites and simplifies the constraints.
      
   2. This produces a smaller constraints-file of size around 180 MB and the savedstate.dat-file in the cryptominisat4-installation directory. The dat-file is necessary for mapping the variables in the simplified constraints back to the variables of the original constraints. So keep it!

4. Try to solve the "smaller" problem instance, e.g. with MarchSAT, which also produces intermediate solutions

MarchSAT-Example for intermediate solution
------------------------------------------

1. Intermediate solution should look like this:
   ```
   s SATISFIABLE
   v -1 2 -3 -4 -5 -6 -7 8 ...
   ```

2. Change src/solutionextender.cpp in cryptominisat line 117 so that backmapping goes through for intermediate solutions.

3. Then call 
   ```
   ./cryptominisat4 --preproc 2 <simplified_solution_file> > <solution_file>.out
   ```

4. \<solution_file\>.out contains cryptominisat comments and similar stuff so it must be adapted by an helper-script
   ```
   ./adapt.sh <solution_file> # without the ".out"!
   ```

5. adapt.sh creates the file \<solution_file\> which can be set as parameter in method solveEternity2.
 

Requirements
------------

- set heap size of Java VM to at least 8192 MB: -Xmx8192m!!!

- [JSATBox](https://github.com/jostien/JSATBox) for constrain handling

- A SAT-solver, e.g. [MarchSAT](https://github.com/jostien/MarchSAT) and/or [cryptominisat](https://github.com/msoos/cryptominisat)