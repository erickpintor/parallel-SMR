# Parallel State Machine Replication (SMR)


This package contains the source code that implements a parallel SMR based on BFT-SMaRt (src/), binary file (dist/), libraries needed (lib/), running script (p_smartrun.sh) and configuration files (config/). For a detailed explanation about how to configure the system, please take a look at the BFT-SMaRt github page (https://github.com/bft-smart/library).

## Parallel State Machine Replication Overview

State machine replication (SMR) is a conceptually simple, yet effective approach to rendering systems fault-tolerant.
The basic idea is that server replicas execute client requests deterministically and in the same order. Consequently, replicas transition through the same sequence of states and produce the same output. State machine replication can tolerate a configurable number of faulty replicas. Moreover, application programmers can focus on the inherent complexity of the application, while avoiding the difficulty of handling replica failures. 

Modern multi-processor servers challenge the state machine replication model since deterministic execution of requests often leads to single-threaded replicas. To overcome this limitation, a number of techniques have been proposed to allow multi-threaded execution of requests in SMR. Most existing techniques build on the observation that independent requests can execute concurrently while conflicting requests must be serialized and executed in the same order by the replicas (two requests conflict if they access common state and at least one of them updates the state, otherwise requests are independent). 

Existing proposals differ on how dependency-based scheduling is performed to provide concurrent execution of independent requests and serialize conflicting requests. Based on existing proposals, there are three classes of protocols:

 - Late scheduling protocols: Requests are scheduled for execution after they are ordered. This essentially means that requests are scheduled at the replicas. Besides the aforementioned requirement on conflicting requests, there are no further restrictions on scheduling.

 - Early scheduling protocols: Part of the scheduling decisions are made before requests are ordered (e.g., the request must be executed by a subset of the existing worker threads). After requests are ordered, their scheduling at each replica must respect these restrictions (i.e., scheduling at the replicas determines which thread in the defined subset will execute the request).

- Static scheduling protocols: Scheduling decisions are made before requests are ordered for execution. 
Thus, there is no request scheduling at the replicas.

