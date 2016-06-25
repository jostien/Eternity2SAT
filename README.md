Eternity2SAT - A SAT-attempt to solve Eternity II 
-------------------------------------------------

Eternity2SAT produces SAT-constraints for Eternity II. There are two variants:

1. A pragmatic version (Eternity2SAT), which produces constraints based on selection variables, e.g., there are 256 binary variables to define where to put each tile and the sum of these variables has to sum up to 1. This produces a constraints-file of size 890 MB, which can hardly be handled by a solver. However, size of constraints can be reduced by simplification via [cryptominisat](https://github.com/msoos/cryptominisat) using the default values for preprocessing to 180 MB (see below).

2. A more economic version (Eternity2SATsmall), which, e.g., codes the position a tile is put on via an 8 bit number. Each number from 0 to 255 has to be used. This produces a contraints-file of size 420 MB. The size can be reduced via cryptominsat using the default values for preprocessing to 114 MB (see below). This version is not checked for bugs in constraints.

3. It is very likely, that the constraints can be improved further.

Some theoretical thoughts
-------------------------

Eternity II was designed to have as few solutions as possible. It looks on the first sight as being a kind of random generated problem. It is well known, that for randomly generated problems of large size, local search approaches like WalkSAT are more suited, provided a satisfying solution exists. Some test runs suggest, that the algebraic approach will always fail, because the number of constraints is so big, and the solver learn so much information about the problem, that memory will always be the limiting factor. Local search only needs to keep track of the constrains and variable state. Especially in the parallel case, the constraints have to be mapped to memory only once and for each thread only the variables have to be handled. For a GPU-version of WalkSAT, see [MarchSAT](https://github.com/jostien/MarchSAT). 

Examples
--------

How to use Eternity2SAT:

1. Run MainEternity2SAT.java, which should produces a constraints file of size around 890 MB.

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

4. <solution_file>.out contains cryptominisat comments and similar stuff so it must be adapted by an helper-script
   ```
   ./adapt.sh <solution_file> # without the ".out"!
   ```

5. adapt.sh creates <solution_file> which can be read in via providing <solution_file> as solution_file in solveEternity2.
 

Requirements
------------

- set heap size of Java VM to at least 8192 MB: -Xmx8192m!!!

- [JSATBox](https://github.com/jostien/JSATBox) for constrain handling

- A SAT-solver, e.g. [MarchSAT](https://github.com/jostien/MarchSAT) and/or [cryptominisat](https://github.com/msoos/cryptominisat)