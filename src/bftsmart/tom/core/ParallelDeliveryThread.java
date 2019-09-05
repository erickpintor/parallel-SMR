/**
 * Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bftsmart.tom.core;

import java.util.logging.Level;

import bftsmart.reconfiguration.ServerViewController;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.server.Recoverable;
import java.util.concurrent.BrokenBarrierException;
import parallelism.ParallelServiceReplica;

/**
 * This class implements a thread which will deliver totally ordered requests to
 * the application
 *
 */
public final class ParallelDeliveryThread extends DeliveryThread {

    /**
     * Creates a new instance of DeliveryThread
     *
     * @param tomLayer TOM layer
     * @param receiver Object that receives requests from clients
     */
    public ParallelDeliveryThread(TOMLayer tomLayer, ServiceReplica receiver, Recoverable recoverer, ServerViewController controller) {
        super(tomLayer, receiver, recoverer, controller);
    }

    private void processReconfigMessages(int consId) {
        try {
            ((ParallelServiceReplica) this.receiver).getReconfBarrier().await();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(ParallelDeliveryThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            java.util.logging.Logger.getLogger(ParallelDeliveryThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] response = controller.executeUpdates(consId);
        TOMMessage[] dests = controller.clearUpdates();

        if (controller.getCurrentView().isMember(receiver.getId())) {
            for (int i = 0; i < dests.length; i++) {
                tomLayer.getCommunication().send(new int[]{dests[i].getSender()},
                        new TOMMessage(controller.getStaticConf().getProcessId(),
                                dests[i].getSession(), dests[i].getSequence(), response,
                                controller.getCurrentViewId(), TOMMessageType.RECONFIG));
            }

            tomLayer.getCommunication().updateServersConnections();
        } else {
            receiver.restart();
        }

        try {
            ((ParallelServiceReplica) this.receiver).getReconfBarrier().await();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(ParallelDeliveryThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            java.util.logging.Logger.getLogger(ParallelDeliveryThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
