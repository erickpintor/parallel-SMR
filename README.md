# Parallel State Machine Replication (SMR)


This package contains the source code that implements a parallel SMR based on BFT-SMaRt (src/), binary file (dist/), libraries needed (lib/), running script (p_smartrun.sh) and configuration files (config/). For a detailed explanation about how to configure the system, please take a look at the BFT-SMaRt github page (https://github.com/bft-smart/library).

## Overview

State machine replication (SMR) is a conceptually simple, yet effective approach to rendering systems fault-tolerant.
The basic idea is that server replicas execute client requests deterministically and in the same order. Consequently, replicas transition through the same sequence of states and produce the same output. State machine replication can tolerate a configurable number of faulty replicas. Moreover, application programmers can focus on the inherent complexity of the application, while avoiding the difficulty of handling replica failures. 

Modern multi-processor servers challenge the state machine replication model since deterministic execution of requests often leads to single-threaded replicas. To overcome this limitation, a number of techniques have been proposed to allow multi-threaded execution of requests in SMR. Most existing techniques build on the observation that independent requests can execute concurrently while conflicting requests must be serialized and executed in the same order by the replicas (two requests conflict if they access common state and at least one of them updates the state, otherwise requests are independent). 

Existing proposals differ on how dependency-based scheduling is performed to provide concurrent execution of independent requests and serialize conflicting requests. Based on existing proposals, there are three classes of protocols:

 - Late scheduling protocols: Requests are scheduled for execution after they are ordered. This essentially means that requests are scheduled at the replicas. Besides the aforementioned requirement on conflicting requests, there are no further restrictions on scheduling.

 - Early scheduling protocols: Part of the scheduling decisions are made before requests are ordered (e.g., the request must be executed by a subset of the existing worker threads). After requests are ordered, their scheduling at each replica must respect these restrictions (i.e., scheduling at the replicas determines which thread in the defined subset will execute the request).

- Static scheduling protocols: Scheduling decisions are made before requests are ordered for execution. 
Thus, there is no request scheduling at the replicas.

For a detailed discussion about the techniques for parallel execution and their tradeoffs, see the following paper:

- **Eduardo Alchieri, Fernando Dotti, Parisa Marandi, Odorico Mendizabal and Fernando Pedone. Boosting State Machine Replication with Concurrent Execution. IEEE Latin-American Symposium on Dependable Computing, 2018.** (https://ieeexplore.ieee.org/document/8671580)

This library implements the late and early scheduling techniques, presented below.

### Late Scheduling

Late scheduling is based on a dependency graph, there are several implementations of this graph that lead to different level of concurrency and performance. The late scheduling techniques implemented in this library were published in the following paper.

- **Ian Escobar, Fernando Dotti, Eduardo Alchieri and Fernando Pedone. Boosting concurrency in Parallel State Machine Replication. ACM/IFIP International Middleware Conference, 2019.** (link to be included!)

In the following we explain how to implemente and execute an application using these techiques. For this, we use the linked list demo used in the experiments reported in the previouly mentioned paper.

**Implementation.**

Basically, to implementat a replicated service it is necessary to follow the same steps used in BFT-SMaRt (https://github.com/bft-smart/library/wiki/Getting-Started-with-BFT-SMaRt). Additionally, it is necessary to inform the requests conflicts by providing a conflict definition,as presented below. The linked list operations used in the experiments was the following: add -- to include (write) an element in the list; and contains -- to check if some element is in the list (read). This conflict definition states tha two requests conflics if at least one of them is a write request, otherwise they do not conflict. 

Afterwards, it is necessary to create a CBASEServiceReplica object providing the conflict definition, the number of worker threads, the graph type (see below), among some other parameters already used in BFT-SMaRt.

```
ConflictDefinition confDefinition = new ConflictDefinition() {
     public boolean isDependent(MessageContextPair r1, MessageContextPair r2) {
             if(r1.classId == ParallelMapping.WRITE || r2.classId == ParallelMapping.WRITE){
                        return true;
              }
              return false;
       }
 }
 new CBASEServiceReplica(replicaId, executor, recoverable, numberThreads, confDefinition, graphType);
```

**Execution.**

It is necessary to execute the server replicas and the clients using the p_bftsmatrun.sh script, as follows.

1) To execute a server replica, it is necessary to use the following command.

```
./p_bftsmartrun.sh demo.list.ListServer <process id> <num threads> <initial entries> <late scheduling?> <graph type>

process id = the process identifier
num threads = number of worker threads
initial entries = the initial list size; use 0 for the tradition sequential SMR
late scheduling? = true to use the late scheduling tecnique, false for early scheduling
graph type = the graph synchronization strategy to be used. It can be coarseLock, fineLock and lockFree (see the paper for details)
```

For example, you should use the following commands to execute three replicas (to tolerate upt to one crash failure) using the lock free graph, 10 threads and 10k entries in the list.

```
./p_bftsmartrun.sh demo.list.ListServer 0 10 10000 true lockFree
./p_bftsmartrun.sh demo.list.ListServer 1 10 10000 true lockFree
./p_bftsmartrun.sh demo.list.ListServer 1 10 10000 true lockFree

```

1) To execute the clients, it is necessary to use the following command.


```
./p_bftsmartrun.sh demo.list.ListClientMO <num clients> <client id> <number of requests> <interval> <maxIndex> <parallel?> <operations per request> <conflict percent>

num clients = number of threads clients to be created in the process, each thread represents one client
client id = the client identifier
number of requests = the number of requests to be sent during the execution
interval = waiting time between requests
maxIndex = the list size, so the clients will use in the requests a random value within the range 0 - maxIndex-1
parallel? = true for parallel execution, false otherwise
operations per request = number of operations contained in a request
conflict percent = the percentage of write requests in the workload
```
For example, you should use the following commands to execute 200 clients distributed in four machines/processes, using a workload with 10% of writes.

```
./p_bftsmartrun.sh demo.list.ListClientMO 50 4001 100000 0 10000 true 50 10
./p_bftsmartrun.sh demo.list.ListClientMO 50 5001 100000 0 10000 true 50 10
./p_bftsmartrun.sh demo.list.ListClientMO 50 6001 100000 0 10000 true 50 10
./p_bftsmartrun.sh demo.list.ListClientMO 50 7001 100000 0 10000 true 50 10

```

### Early Scheduling

In this technique ...
