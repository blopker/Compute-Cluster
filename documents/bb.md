CS290B-HW4
==========

ASSIGNMENT 4

by Karl Lopker

Paper Summary: A Development and Deployment Framework for Distributed Branch & Bound
------------------------------------------

There are a number of NP-Complete problems that have no polynomial time solutions. Since finding these solutions probably wont happen for awhile, we need some other ways to speed up our computations. One of these ways is the Branch & Bound approach. Although Branch & Bound doesn't provide a better big O runtime of a problem, it does decrease runtime by giving the opportunity to do less work. Branch & Bound is useful for problems like the Salesman Problem. This paper discusses Branch & Bound in the context of solving a distributed TSP problem, modeled by a directed acyclic graph. First the paper discusses the distributed Branch & Bound architecture used in the JICOS framework, including fault tolerance considerations. Then the paper lays out several performance enhancements that can be done. Later the paper shows the Branch & Bound API, also in JICOS. Finally the paper presents its experimental results.

The Branch & Bound architecture has three main comments; Hosting Service Provider, Task Server, and Host. The Hosting Service Provider (HSP) is the service that both interacts with clients and looks after other architecture parts. The HSP is essentially a name server. The Task Servers are databases for the tasks. The HSP is contacted by a Task Server and is then told how to join the system. In our system the HSP and Task Server are the same entity called a "Space". Each Task Server has its own associated Hosts. A Host does the actual computations. It consumes tasks from its Task Server and reports back the result. When a Host is detected as faulty, the Task Server will kill the Host and resend its tasks to another Host.

The paper details some performance enhancements that the system has. These are; Task caching, Task pre-fetching and Task server computation. Task caching is when a Host holds onto tasks that is creates and adds them to its own queue. This eliminates the overhead of the Task server re-sending the tasks. Pre-fetching is when a host will ask fore more tasks before it's done with the one it has. Finally, server computation is when a Task Server will compute a task itself. This is usually done for simple comparison tasks that would take longer to send than to just compute.

The JICOS framework has several classes that help a programmer run a distributed TSP problem. The main classes are; BranchAndBound, IntUpperBound, and MinSolution. The BranchAndBound class extends the Task class and is the main TSP class. BranchAndBound can do one of two things. If the subtree BranchAndBound represents is too big, it can spawn a series of smaller BranchAndBound tasks for the Task Server. On the other hand, BranchAndBound may also search its tree and report back with a IntUpperBound if it finds a good solution. IntUpperBound is a shared class that all Tasks have access to. IntUpperBound represents the smallest TSP tour found so far by any task. If a task finds a better upper bound, IntUpperBound will be replaced with the new one. Finally the MinSolution class also extends Task. MinSolution takes all the best tours from several completed BranchAndBound or other MinSolution tasks and outputs the best of them. These classes are used together by the programmer to solve the TSP problem.

To test this system the paper used a 200 city TSP problem. The upper bound was set to the optimal tour so that the best case run could be measured. This shows that real performance will be between the test case and the worst case (no B&B). The results in the paper are promising. Two processors (one computer) obtained a run time of 9 hours and 33 minutes (100% efficiency). On the other hand, 240 processors (120 computers) got a time of 11 minutes (94% efficiency). Considering these are commodity machines the results show how effective this architecture is.