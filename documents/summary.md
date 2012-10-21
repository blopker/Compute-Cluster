CS290B Homework 2
=================
Karl Lopker

Geoffrey Douglas

Paper Summary: How To Build A Compute Farm
------------------------------------------

As commodity computers become cheaper the traditional supercomputer has taken a back seat. The paper "How To Build A Compute Farm" explores how to harness the power of distributed computing without the client having to worry about implementation. The only extra work on the client side is coming up with instructions on how to split its job into tasks. The paper then explains how computers running on different JVMs can share job code. Finally, the paper details how faults can be handled without service interruption.

Ideally a user of a distributed computing network should not have to know any implementation details. In fact, a user should think they are running their jobs on a single highly parallel computer. To address this requirement the paper implements a Replicated-Worker Pattern. In this pattern the client has a job they want completed, say generating the Mandelbrot set. Like any parallel job the client must specify how the job should be broken up into tasks by the computer. Once this is done the client can send the tasks to be completed. Traditionally, the client might have to specify what computers should handle each task and would also have to handle load balancing itself. With the Replicated-Worker Pattern, the client simply adds tasks to a single compute space without having to know anything about the computers doing the actual work. Each worker then can take on tasks as it becomes available for work. This supplies us with free load-balancing since slower computers will just consume less tasks. While this works well for a system running under a single JVM, compute farms running multiple JVMs must also share task classes in order to do any work.

To share class code, the paper uses solutions provided by Java RMI. Usually to share code you have to set up a whole HTTP server. This approach can add complexity and more room for failure. The paper instead uses a ClassServer implementation to circumvent this issue. The ClassServer is run on the client and allows the compute space and computers to dynamically load required task and job classes. However, after the classes are distributed work still needs to be done. This is when more problems can happen.

The last section of this paper focuses on fault tolerance. To deal with faults inherent in a distributed system, the paper turns to Peter Deutsch's "The Eight Fallacies of Distributed Computing". Instead of trying to hide these problems from a system designer, the paper opts to make these types of faults easier to handle. To do this the programmer is given several custom exception classes; RemoteException, ComputeSpaceException, CannotWriteTaskException, CannotTakeResultException, and CancelledException. RemoteException is the most important as it signals a loss of communication for any reason. This exception should be handled gracefully and should not stop a job from running. CancelledException gets raised when the client requests that a job should stop. The other exceptions have to do with running out of resources and unsuccessful, but not broken, communication.

In conclusion, this paper introduces the ComputeFarm concept. With this construct we are able to build generalized, fault compute clusters that behave like large supercomputers for a fraction of the cost. The only downside is that clients must still parallelize their jobs manually.
