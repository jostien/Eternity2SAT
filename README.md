Eternity2SAT - An SAT-attempt to solve Eternity II 
--------------------------------------------------

Eternity2SAT produces SAT-constraints for Eternity II.

Examples
--------

1.) Run MainEternity2SAT.java, which should produces a constraints file of size around 890 MB.
2.a) Wait until solver finishes (which is unlikely but you can try).
2.b) Stop solver and program and keep the constraints-file.
3.)
3.1) Run
```
./cryptominisat4 --preproc 1 <path_to_constrains_file> <path_to_constraints_file>.simplified
```
from within the cryptominisat4-installation directory, which rewrites and simplifies the constraints.
3.2) This produces a smaller constraints-file of size around 180 MB and the savedstate.dat-file in the cryptominisat4-installation directory. The dat-file is necessary for mapping the variables in the simplified constraints back to the variables of the original constraints. So keep it!
4.) Try to solve the "smaller" problem instance, e.g. with MarchSAT, which also produces intermediate solutions

MarchSAT-Example for intermediate solution
------------------------------------------

5.) Intermediate solution should look like this:
```
s SATISFIABLE
v -1 2 -3 -4 -5 -6 -7 8 ...
```
6.) Change src/solutionextender.cpp in cryptominisat line 117 so that backmapping goes through for intermediate solutions.
7.) Then call 
```
./cryptominisat4 --preproc 2 <simplified_solution_file> > <solution_file>.out
```
8.) <solution_file>.out contains cryptominisat comments and similar stuff so it must be adapted by an helper-script
```
./adapt.sh <solution_file> # without the ".out"!
```
9.) adapt.sh creates <solution_file> which can be read in via providing <solution_file> as solution_file in solveEternity2.
 

Requirements
------------

- set heap size of Java VM to at least 8192 MB: -Xmx8192m!!!

- JSATBox for constrain handling

- A SAT-solver, e.g. MarchSAT and/or cryptominisat