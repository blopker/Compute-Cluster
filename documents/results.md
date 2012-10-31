CS290B-HW4
==========

ASSIGNMENT 4

by Karl Lopker

Results
-------

### Sales Man
Calculating a 13 city tour with a task split cut off of 8 cities. Best of 4 runs.

#### Same JVM
	Total Time: 154.362 sec

#### Different JVMs, same machine
	Total Time: 158.256 sec

#### Different machines.
	Total Time: 105.705 sec

#### Parallel Efficiency, multiple computers
1. 105705/(1*105705) = 1
2. 105705/(2*52079) = 1.014
8. 105705/(8*37178) = 0.355

![TSP](tsp.PNG)

Analysis
--------
#### Explain your parallel [in]efficiencies
Each computer in these tests contained 4 cores. This program is designed to used all of them. So when we are testing 8 computers, that's really 32 cores at work. This is enough parallelism to strain most non-embarrassingly parallel applications. From the graph we can see that at two computers (8 cores) the parallel efficiency is holding strong. However, when 8 computers are used (32 cores) efficiency takes a hit. This is without a doubt do to the Space's inefficiencies. The space makes several expensive operations, like in its dependency sorting method, that drastically slows down task transfers. To alleviate this new data structures should be used. Also only one thread does the task sorting, this could be parallelized as well.
