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
package bftsmart.tom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import bftsmart.reconfiguration.ReconfigureReply;
import bftsmart.reconfiguration.views.View;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.util.Extractor;
import bftsmart.tom.util.Logger;
import bftsmart.tom.util.TOMUtil;

/**
 * This class implements a TOMSender and represents a proxy to be used on the
 * client side of the replicated system. It sends a request to the replicas,
 * receives the reply, and delivers it to the application.
 */
public class ParallelServiceProxy extends ServiceProxy {


    /**
     * Constructor
     *
     * @see bellow
     */
    public ParallelServiceProxy(int processId) {
        this(processId, null, null, null);
    }

    /**
     * Constructor
     *
     * @see bellow
     */
    public ParallelServiceProxy(int processId, String configHome) {
        this(processId, configHome, null, null);
    }

    /**
     * Constructor
     *
     * @param processId Process id for this client (should be different from
     * replicas)
     * @param configHome Configuration directory for BFT-SMART
     * @param replyComparator used for comparing replies from different servers
     * to extract one returned by f+1
     * @param replyExtractor used for extracting the response from the matching
     * quorum of replies
     */
    public ParallelServiceProxy(int processId, String configHome,
            Comparator<byte[]> replyComparator, Extractor replyExtractor) {
        super(processId, configHome, replyComparator, replyExtractor);
    }

   

    public byte[] invokeParallel(byte[] request, int groupId) {
        return invoke(request, TOMMessageType.ORDERED_REQUEST, groupId);
    }

   

    /**
     * This method sends a request to the replicas, and returns the related
     * reply. If the servers take more than invokeTimeout seconds the method
     * returns null. This method is thread-safe.
     *
     * @param request Request to be sent
     * @param reqType TOM_NORMAL_REQUESTS for service requests, and other for
     * reconfig requests.
     * @return The reply from the replicas related to request
     */
    public byte[] invoke(byte[] request, TOMMessageType reqType, int groupId) {
        canSendLock.lock();

        // Clean all statefull data to prepare for receiving next replies
        Arrays.fill(replies, null);
        receivedReplies = 0;
        response = null;
        replyQuorum = getReplyQuorum();
        //System.out.println("************** reply quorum = "+replyQuorum);
        
        // Send the request to the replicas, and get its ID
        reqId = generateRequestId(reqType);
        operationId = generateOperationId();
        requestType = reqType;

        replyServer = -1;
        hashResponseController = null;

        if (requestType == TOMMessageType.UNORDERED_HASHED_REQUEST) {

            replyServer = getRandomlyServerId();
            Logger.println("[" + this.getClass().getName() + "] replyServerId(" + replyServer + ") "
                    + "pos(" + getViewManager().getCurrentViewPos(replyServer) + ")");

            hashResponseController = new HashResponseController(getViewManager().getCurrentViewPos(replyServer),
                    getViewManager().getCurrentViewProcesses().length);

            TOMMessage sm = new TOMMessage(getProcessId(), getSession(), reqId, operationId, request,
                    getViewManager().getCurrentViewId(), requestType);
            sm.setReplyServer(replyServer);

            TOMulticast(sm);
        } else {
            TOMulticast(request, reqId, operationId, reqType,groupId);
        }

        Logger.println("Sending request (" + reqType + ") with reqId=" + reqId);
        Logger.println("Expected number of matching replies: " + replyQuorum);

        // This instruction blocks the thread, until a response is obtained.
        // The thread will be unblocked when the method replyReceived is invoked
        // by the client side communication system
        try {
            if (reqType == TOMMessageType.UNORDERED_HASHED_REQUEST) {
                if (!this.sm.tryAcquire(invokeUnorderedHashedTimeout, TimeUnit.SECONDS)) {
                    System.out.println("######## UNORDERED HASHED REQUEST TIMOUT ########");
                    canSendLock.unlock();
                    return invoke(request, TOMMessageType.ORDERED_REQUEST, groupId);
                }
            } else if (!this.sm.tryAcquire(invokeTimeout, TimeUnit.SECONDS)) {
                Logger.println("###################TIMEOUT#######################");
                Logger.println("Reply timeout for reqId=" + reqId);
                System.out.print(getProcessId() + " // " + reqId + " // TIMEOUT // ");
                System.out.println("Replies received: " + receivedReplies);
                canSendLock.unlock();

                return null;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        Logger.println("Response extracted = " + response);

        byte[] ret = null;

        if (response == null) {
            //the response can be null if n-f replies are received but there isn't
            //a replyQuorum of matching replies
            Logger.println("Received n-f replies and no response could be extracted.");

            canSendLock.unlock();
            if (reqType == TOMMessageType.UNORDERED_REQUEST || reqType == TOMMessageType.UNORDERED_HASHED_REQUEST) {
                //invoke the operation again, whitout the read-only flag
                Logger.println("###################RETRY#######################");
                return invokeOrdered(request);
            } else {
                throw new RuntimeException("Received n-f replies without f+1 of them matching.");
            }
        } else //normal operation
        //******* EDUARDO BEGIN **************//
        if (reqType == TOMMessageType.ORDERED_REQUEST) {
            //Reply to a normal request!
            if (response.getViewID() == getViewManager().getCurrentViewId()) {
                ret = response.getContent(); // return the response
            } else {//if(response.getViewID() > getViewManager().getCurrentViewId())
                //updated view received
                reconfigureTo((View) TOMUtil.getObject(response.getContent()));

                canSendLock.unlock();
                return invoke(request, reqType, groupId);
            }
        } else if (reqType == TOMMessageType.UNORDERED_REQUEST || reqType == TOMMessageType.UNORDERED_HASHED_REQUEST) {
            if (response.getViewID() == getViewManager().getCurrentViewId()) {
                ret = response.getContent(); // return the response
            } else {
                canSendLock.unlock();
                return invoke(request, TOMMessageType.ORDERED_REQUEST, groupId);
            }
        } else if (response.getViewID() > getViewManager().getCurrentViewId()) {
            //Reply to a reconfigure request!
            Logger.println("Reconfiguration request' reply received!");
            Object r = TOMUtil.getObject(response.getContent());
            if (r instanceof View) { //did not executed the request because it is using an outdated view
                reconfigureTo((View) r);

                canSendLock.unlock();
                return invoke(request, reqType, groupId);
            } else if (r instanceof ReconfigureReply) { //reconfiguration executed!
                reconfigureTo(((ReconfigureReply) r).getView());
                ret = response.getContent();
            } else {
                Logger.println("Unknown response type");
            }
        } else {
            Logger.println("Unexpected execution flow");
        }
        //******* EDUARDO END **************//

        canSendLock.unlock();
        return ret;
    }

    protected int getReplyQuorum() {
		if (getViewManager().getStaticConf().isBFT()) {
			return (int) Math.ceil((getViewManager().getCurrentViewN()
					+ getViewManager().getCurrentViewF()) / 2) + 1;
		} else {
			return 1;
		}
	}


    
    public void TOMulticast(byte[] m, int reqId, int operationId, TOMMessageType reqType, int groupId) {
		getCommunicationSystem().send(isUseSignatures(), getViewManager().getCurrentViewProcesses(),
				new TOMMessage(getProcessId(), getSession(), reqId, operationId, m, 
                                        getViewManager().getCurrentViewId(), reqType,groupId));
    }
    

}
