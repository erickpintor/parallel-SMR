/**
 * Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and
 * the authors indicated in the @author tags
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package bftsmart.tom.core;


import bftsmart.communication.ServerCommunicationSystem;

import bftsmart.consensus.roles.Acceptor;
import bftsmart.reconfiguration.ServerViewController;

import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.Recoverable;
import bftsmart.tom.server.RequestVerifier;


/**
 * This class implements the state machine replication protocol described in
 * Joao Sousa's 'From Byzantine Consensus to BFT state machine replication: a latency-optimal transformation' (May 2012)
 * 
 * The synchronization phase described in the paper is implemented in the Synchronizer class
 */
public final class ParallelTOMLayer extends TOMLayer {

   
    /**
     * Creates a new instance of ParallelTOMLayer
     *
     * @param manager Execution manager
     * @param receiver Object that receives requests from clients
     * @param recoverer
     * @param a Acceptor role of the PaW algorithm
     * @param cs Communication system between replicas
     * @param controller Reconfiguration Manager
     * @param verifier
     */
    public ParallelTOMLayer(ExecutionManager manager,
            ServiceReplica receiver,
            Recoverable recoverer,
            Acceptor a,
            ServerCommunicationSystem cs,
            ServerViewController controller,
            RequestVerifier verifier) {

       super(manager, receiver, recoverer, a, cs, controller, verifier);
    }

   
     private void startDeliveryThread(ServiceReplica receiver, Recoverable recoverer){
        this.dt = new ParallelDeliveryThread(this, receiver, recoverer, this.controller); // Create delivery thread
        this.dt.start();
    }
    
}
