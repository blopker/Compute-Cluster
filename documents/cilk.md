CS290B-HW3
==========

ASSIGNMENT 3: A DIVIDE-AND-CONQUER API

by Karl Lopker

Paper Summary: Cilk: An Efficient Multithreaded Runtime System
------------------------------------------

Recently CPU manufactures have hit a wall on clock speeds. Since CPUs are not getting substantially faster they must instead find new ways to speed up program execution. One method is through parallel computing, however creating parallel programs has always been difficult. This paper proposes a work stealing/closure paradigm called Cilk to help with making parallel software. Along with the Cilk concepts, this paper also introduces a performance model to find the effectiveness of parallel programs.

Traditionally programmers interested in parallel programming would have to manage threads themselves. This can quickly become tedious. Cilk takes care of this for the programmer through use of the closure data structure and work stealing. The closure data structure holds a space for arguments, the number of missing arguments (join counter) and space for an answer. When a thread wants a task to be done in parallel it creates a closure and hands it to the scheduler. There the scheduler fills the closure with data it needs to run. When the closure's join counter is zero it becomes 'ready' and the scheduler assigns it to processor to be run.

Occasionally the scheduler will assign several slow closures to a processor. Other processors my become available for work, but the only available work is already assigned. This is where work stealing becomes useful. Essentially processors are allowed to take closures from other processors queues, but they need be careful not to impact performance. The paper specifies that processors should steal the closure with the lowest level, that is closures that are closest to level 0 in the process tree. This stealing heuristic may not be optimal, but it is most likely to reduce communication between processors and also take closures that are on the critical path.

In order to asses the effectiveness of the Cilk scheduler, this paper proposes a parallel performance model. The paper states that a perfect scheduler will divide the runtime of a program by the number of processors available. In other words, if a program takes 10 hours to run with one processor a perfect scheduler would make it run in 1 hour given 10 processors. The paper refers to this case as 'perfect linear speedup'. However this is rarely achieved since the program itself may have a long critical path and thus not perfectly parallel. The paper uses this model to test the Cilk sceduler and concludes it only starts to become inefficient as the number of processors increase to the program's average parallelism. This, it says, is to be expected.

In conclusion, this paper aims to enable developers with new tools to create parallel software. The paper recognizes the fact that developers should not have to worry about thread management and proposes methods to deal with it automatically. Through closures and work stealing, Cilk can scale out to as many processors available while having provably good performance and low overhead.
