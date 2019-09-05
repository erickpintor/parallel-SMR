/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelism.late.graph.concurrentGraph;

public enum SyncType {
	coarseLock, fineLock, hohGetWaitFree, hohHelpedRemove, helpedRemoveGetWaitFree, lockFree
}