CS290B-HW3
==========

ASSIGNMENT 3: A DIVIDE-AND-CONQUER API

by Karl Lopker

Paper Summary: CX: A Scalable, Robust Network for Parallel
------------------------------------------

As commodity computers become cheaper, new supercomputers are becoming scarce. This paper introduces an architecture designed to abstract the use of computer clusters for programmers. The architecture is called CX, or Computation eXchange. The goal is to soak up unused CPU cycles on networked computers through a simple API. The paper also describes the CX architecture along with potential performance constraints and fault tolerance considerations.

In order to take cluster computing mainstream, the paper proposes an API for programmers. This API abstracts the underlying system provided the programmer respects the two defined models. The first model is the Computational Model. In the Computational Model, the programmer must be aware that tasks can be stopped at any time so they must execute quickly with little blocking time. In this respect it is best to adopt the Clik paradigm such that all threads are non blocking. The second model is the Programming Model. In the Programming Model the programmer must be aware of communication overhead. When putting tasks on the task server the programmer must be careful with the number of tasks used. The paper suggests batch sending tasks so that one connection can be used. These two models can be at odds with each other however using parallel techniques, tasks can both be large enough for communication and also quick to compute.

The programmer has two methods to interact with the task server: storeTask and storeResult. The storeTask method allows the programmer to add new tasks to the task server. These tasks may have dependencies on other tasks and will be put into a wait queue until they are ready. The storeResult method is used to signal that a task has a usable value to either return to a client or to use as another task's dependency. Both methods allow for sending their arguments in bulk for communication efficiency.

The architecture of CX has several requirements. Operations dealing with scheduling must be as quick as possible. This means a low time complexity, but also memory efficient. Also, the architecture must be scalable. The system must be able to add and subtract both computers and spaces dynamically. This dynamic scaling allows for responsive fault tolerance that can recover without special intervention. Finally, after a failure the system must be able to reconfigure itself to prepare for another. The CX architecture achieves these requirements by using isolated clusters. These clusters each contain its own task server and producers. The producers only communicate with their task server, the task server communicates with its producers and other task servers.

The isolated clusters are configured into a 'fat tree' with two root nodes. Task servers will steal work from the cluster above it and give work extra to the cluster below. To deal with fault tolerance each cluster knows about two clusters above it. This way if the main cluster were to die they can simply switch over to the secondary one. Clusters in the same level and branch share their task results so that completed is not lost in a crash. When a cluster goes down one of the leaf clusters will take its place.

In conclusion, this paper describes a scalable, fault tolerant way to distribute compute tasks. Using this type of system will become more popular in the future so this foundation must be solid. This paper details a concrete API for programmers, but also provides considerations for its use.
