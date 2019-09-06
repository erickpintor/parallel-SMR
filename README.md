# Parallel State Machine Replication (SMR)


This package contains the source code that implements several different techniques for parallel SMR built on top of BFT-SMaRt (src/), the binary file (dist/), the libraries needed (lib/), the running script (p_smartrun.sh) and the configuration files (config/). For a detailed explanation about how to configure the system, please check the BFT-SMaRt github page (https://github.com/bft-smart/library).

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

Late scheduling is based on a Conflict-Ordered Set (COS) used to track conflicts. The current COS implementations are based on a dependency graph. There are several implementations of this graph that lead to different levels of concurrency and performance. The late scheduling techniques implemented in this library were published in the following paper.

- **Ian Escobar, Fernando Dotti, Eduardo Alchieri and Fernando Pedone. Boosting concurrency in Parallel State Machine Replication. ACM/IFIP International Middleware Conference, 2019.** (http://2019.middleware-conference.org/)

In the following we explain how to implement and execute an application using these techiques. For this, we use the linked list demo used in the experiments reported in the previouly mentioned paper.

**Implementation.**

Basically, to implement a replicated service it is necessary to follow the same steps used in BFT-SMaRt (https://github.com/bft-smart/library/wiki/Getting-Started-with-BFT-SMaRt). Additionally, it is necessary to inform the requests conflicts by providing a conflict definition,as presented below. The linked list operations used in the experiments was the following: add -- to include (write) an element in the list; and contains -- to check if some element is in the list (read). This conflict definition states that two requests conflics if at least one of them is a write request, otherwise they do not conflict. 

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

For example, you should use the following commands to execute three replicas (to tolerate up to one crash failure) using the lock free graph, 10 threads and 10k entries in the list.

```
./p_bftsmartrun.sh demo.list.ListServer 0 10 10000 true lockFree
./p_bftsmartrun.sh demo.list.ListServer 1 10 10000 true lockFree
./p_bftsmartrun.sh demo.list.ListServer 2 10 10000 true lockFree

```

2) To execute the clients, it is necessary to use the following command.


```
./p_bftsmartrun.sh demo.list.ListClientMO <num clients> <client id> <number of requests> <interval> <maxIndex> <parallel?> <operations per request> <conflict percent>

num clients = number of threads clients to be created in the process, each thread represents one client
client id = the client identifier
number of requests = the number of requests to be sent during the execution
interval = waiting time between requests
maxIndex = the list size, clients will use in the requests a random value ranging from 0 to maxIndex-1
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

This technique uses the notion of classes of requests used by a programmer to express the concurrency in an application.
In brief, the idea is to group service requests in classes and then specify how classes must be synchronized.
For example, we can model the previously mentioned linked list application with a class of read requests and a class of write requests. The class of write requests conflicts with itself and with the class of read requests.
This ensures that a write is serialized with reads and with other writes. It is also possible to consider more elaborate concurrency models that assume sharded application state with read and write operations within and across shards.
Afterwards, these requests classes are statically mapped to working threads. The client needs to inform the class its request belongs and the scheduler dispatches it according with this information. A detailed description about this scheduling technique can be found in the following paper:

- **Eduardo Alchieri, Fernando Dotti and Fernando Pedone. Early Scheduling in Parallel State Machine Replication. ACM Symposium on Cloud Computing, 2018.** (https://dl.acm.org/citation.cfm?id=3267825)
