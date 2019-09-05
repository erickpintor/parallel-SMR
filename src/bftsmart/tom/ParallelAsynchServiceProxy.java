/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bftsmart.tom;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.util.Extractor;
import bftsmart.tom.util.Logger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

/**
 *
 * @author eduardo 
 * 
 */
public class ParallelAsynchServiceProxy extends AsynchServiceProxy{

    public ParallelAsynchServiceProxy(int id) {
        super(id);
    }

    public ParallelAsynchServiceProxy(int id, String configHome) {
        super(id,configHome);
    }
    
    
    public ParallelAsynchServiceProxy(int processId, String configHome,
			Comparator<byte[]> replyComparator, Extractor replyExtractor) {
            super(processId, configHome, replyComparator, replyExtractor);
    }
    
    
  
    /**
	 * 
	 * @param request
	 * @param replyListener
	 * @param reqType Request type
	 * @return 
	 */
    public int invokeParallelAsynchRequest(byte[] request, ReplyListener replyListener, TOMMessageType reqType, int groupId) {
		return invokeParallelAsynchRequest(request, super.getViewManager().getCurrentViewProcesses(), replyListener, reqType, groupId);
    }
    
    /**
     *   
     * @param request
     * @param targets
     * @param replyListener
     * @param reqType Request type
     * @return
     */
    public int invokeParallelAsynchRequest(byte[] request, int[] targets, ReplyListener replyListener, TOMMessageType reqType, int groupId) {
		return invokeAsynch(request, targets, replyListener, reqType, groupId);
    }
    
     /**
     * 
     * @param request
     * @param targets
     * @param replyListener
     * @param reqType
     * @return
     */
	private int invokeAsynch(byte[] request,int[] targets, ReplyListener replyListener, TOMMessageType reqType, int groupId) {

            Logger.println("Asynchronously sending request to " + Arrays.toString(targets));

            RequestContext requestContext = null;
		
		canSendLock.lock();

		requestContext = new RequestContext(generateRequestId(reqType), generateOperationId(),
				reqType, targets, System.currentTimeMillis(), replyListener);

		try {
                        Logger.println("Storing request context for " + requestContext.getReqId());
			requestsContext.put(requestContext.getReqId(), requestContext);

                        sendMessageToTargets(request, requestContext.getReqId(), requestContext.getOperationId(), targets, reqType, groupId);
			
		} finally {
			canSendLock.unlock();
		}

		return requestContext.getReqId();
	}
    
    
    public void sendMessageToTargets(byte[] m, int reqId, int operationId, int[] targets, TOMMessageType type, int groupId) {
        if (this.getViewManager().getStaticConf().isTheTTP()) {
            type = TOMMessageType.ASK_STATUS;
        }

        getCommunicationSystem().send(isUseSignatures(), getViewManager().getCurrentViewProcesses(),
                new TOMMessage(getProcessId(), getSession(), reqId, operationId, m,
                        getViewManager().getCurrentViewId(), type, groupId));

        
    }
    
    
}
