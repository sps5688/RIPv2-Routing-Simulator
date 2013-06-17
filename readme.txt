Steven Shaw
Data Comm 2
Project 1: RIPv2 Simulation

HOW TO COMPILE AND RUN:
	Compile: javac RIP_Simulation.java NetworkGraph.java Node.java Edge.java RoutingTable.java
	Run:     java RIP_Simulation [num of pairs of routers (2 routers/pair)] OPTIONAL[enable]

	NOTE: The enable/disable parameter for to switch the node failure probability feature
	and it is optional. If you want to enable this feature simply type enable on the command
	line after the number of pairs of routers and a space.

	NOTE: 2 routers are generated per pair. So if you entered 5 on the command line it would
	generate 10 routers.

Other useful information:
	When the probability feature is enabled and a router fails, it will not come back online.
	It will announce that it has failed and it it's thread will terminate. The simulation
	will terminate when all of the threads have terminated.

	If the probability feature is not enabled, every router will continously print their
	routing table.

	NOTE: It may be difficult to see the effects of the entries being removed after 6 seconds
	feature because every router will generally fail before they get a chance to remove them
	from their routing table. The effects can be seen in the timeout duration collumn for a
	router that is still running for routers that have failed.