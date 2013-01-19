Java Based Compute Cluster 
==========================

Purpose
-------
The purpose of this project is to implement the compute cluster in [this](http://today.java.net/pub/a/today/2005/04/21/farm.html) paper.

Usage
-----
In the build.xml file you must change the "host" parameter to whatever hostname is running the Space component. Default is localhost

You can run each ant directive on different machines.

Run the runComputer directive as many times as necessary.

	ant runSpace
	ant runComputer

Then run some tasks on the cluster

	ant runSalesman
	ant runFib
	ant runMandel
