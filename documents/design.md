CS290B-HW5
==========

ASSIGNMENT 5

by Karl Lopker

Design Document & Future Enhancements
---------------

![Design](https://www.lucidchart.com/publicSegments/view/5090a39d-b840-4c07-8abf-38e00a60e377/image.png)

Several enhancements were made from the previous iteration. There are three most notable ones. The first is a change to the way waiting tasks are handled. The second is the use of multiple cores per physical computer. The third is the implementation of local task calculation. There are also other enhancements that could be implemented in the future.

The first enhancement was to change the way the Result sorter was checking for waiting tasks to be done. Initially, the sorter would get a new result from the computer proxy, then it would iterate through every waiting task to see if it had a result it needed. This did not scale well when there were many waiting tasks. The solution was to put all the waiting tasks in a hash map where the key was the task's ID. Now when a new result is created it also gets its creator's childID that references a waiting task. The sorter will get the result and simply do a hash look up to find the correct waiting task. When the sorter adds a result to a task the task decrements its join counter. When the join counter is zero the task is now ready to be executed so it is moved to the task queue to be processed.

The second enhancement is the use of multiple cores on a single machine. Before when a Computer class was run through its main method it would only have one core available to it. This leave a lot of computing power on the table. In order to get more cores working the Computer main method now starts several Computer instances. The Computers are run in their own threads which allows the JVM to schedule them automatically for us. Each of these Computers looks like a new real computer to the space so no code had to be changed there.

The third enhancement was the inclusion of local runnable tasks. This feature allows a Computer Proxy to run a task using the Space's resources. This saves the time of marshaling and send the task to a remote Computer. The way this is done is by adding a isLocalTask() method to the Task class. By using this method each class instance can decide if it should be run locally. The decision is usually based on the task's arguments. Experimental results showed great speed ups when decomposition and composition tasks were run locally since these are not typically expensive to calculate.

There are also several future enchantments that could be done to speed up the system. These are; pref etching, caching, and storing results. Preferring would allow computers to get new tasks before the ones they have are completed. This will cut down on communication wait time. Caching is when a task creates more tasks, but these new tasks are just added to the computer's personal task queue instead of being sent to the space and back again. Caching could reduce the number of communications by around half. Lastly, it is also possible to store results. If two tasks have the same arguments, only one of those tasks should be run. Then the results of that one task would be stored and shared with any other tasks with the same arguments. This would be very helpful for jobs like Fibonacci.

