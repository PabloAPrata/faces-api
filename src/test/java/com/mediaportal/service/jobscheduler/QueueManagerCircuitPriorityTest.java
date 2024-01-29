package com.mediaportal.service.jobscheduler;

import com.mediaportal.jpa.converters.MoveStatus;
import com.mediaportal.jpa.converters.StatusExecucao;
import com.mediaportal.jpa.domain.CaRequest;
import com.mediaportal.jpa.domain.McAssetMoveQ;
import com.mediaportal.jpa.domain.McMoveRequestor;
import com.mediaportal.jpa.domain.McStorage;
import com.mediaportal.jpa.services.CaRequestServiceInterface;
import java.util.Iterator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test class for QueueManagerCircuitPriority operations
 *
 * @author aksato
 */
public class QueueManagerCircuitPriorityTest {

    /**
     * Perform a test with adding and removing jobs
     */
    @Test
    public void addAndRemoveJobs() {
        // Create dummy jobs
        McAssetMoveQ job1 = new McAssetMoveQ();
        setJobParameters(1, job1, 1, 2, 3, 5);
        McAssetMoveQ job2 = new McAssetMoveQ();
        setJobParameters(2, job2, 2, 3, 4, 15);
        McAssetMoveQ job3 = new McAssetMoveQ();
        setJobParameters(3, job3, 4, 5, 6, 7);
        McAssetMoveQ job4 = new McAssetMoveQ();
        setJobParameters(4, job4, 2, 3, 4, 5);
        McAssetMoveQ job5 = new McAssetMoveQ();
        setJobParameters(5, job5, 4, 5, 6, 4);
        McAssetMoveQ job6 = new McAssetMoveQ();
        setJobParameters(6, job6, 1, 2, 3, 4);
        McAssetMoveQ job7 = new McAssetMoveQ();
        setJobParameters(7, job7, 4, 5, 6, 10);
        McAssetMoveQ job8 = new McAssetMoveQ();
        setJobParameters(8, job8, 1, 2, 3, 6);

        // Initialize manager
        QueueManager queueManager = new QueueManagerCircuitPriority();
        Iterator<McAssetMoveQ> it;

        // Insert first job into queue
        System.out.println("\n Inserting job with parameters { " + job1.getSrcstorage().getStorageid() + ", " + job1.getDeststorage().getStorageid() + ", " + job1.getRequestor().getValue() + ", " + job1.getActionid() + "}");
        queueManager.addNewJob(job1);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());

        // Insert second job into queue
        System.out.println("\n Inserting job with parameters { " + job2.getSrcstorage().getStorageid() + ", " + job2.getDeststorage().getStorageid() + ", " + job2.getRequestor().getValue() + ", " + job2.getActionid() + "}");
        queueManager.addNewJob(job2);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());
        assertEquals(job2, it.next());

        // Insert third job into queue
        System.out.println("\n Inserting job with parameters { " + job3.getSrcstorage().getStorageid() + ", " + job3.getDeststorage().getStorageid() + ", " + job3.getRequestor().getValue() + ", " + job3.getActionid() + "}");
        queueManager.addNewJob(job3);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());
        assertEquals(job3, it.next());
        assertEquals(job2, it.next());

        // Insert fourth job into queue
        System.out.println("\n Inserting job with parameters { " + job4.getSrcstorage().getStorageid() + ", " + job4.getDeststorage().getStorageid() + ", " + job4.getRequestor().getValue() + ", " + job4.getActionid() + "}");
        queueManager.addNewJob(job4);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());
        assertEquals(job4, it.next());
        assertEquals(job2, it.next());
        assertEquals(job3, it.next());

        // Insert fifth job into queue
        System.out.println("\n Inserting job with parameters { " + job5.getSrcstorage().getStorageid() + ", " + job5.getDeststorage().getStorageid() + ", " + job5.getRequestor().getValue() + ", " + job5.getActionid() + "}");
        queueManager.addNewJob(job5);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job1, it.next());
        assertEquals(job4, it.next());
        assertEquals(job2, it.next());

        // Insert sixth job into queue
        System.out.println("\n Inserting job with parameters { " + job6.getSrcstorage().getStorageid() + ", " + job6.getDeststorage().getStorageid() + ", " + job6.getRequestor().getValue() + ", " + job6.getActionid() + "}");
        queueManager.addNewJob(job6);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job6, it.next());
        assertEquals(job1, it.next());
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job4, it.next());
        assertEquals(job2, it.next());

        // Insert seventh job into queue
        System.out.println("\n Inserting job with parameters { " + job7.getSrcstorage().getStorageid() + ", " + job7.getDeststorage().getStorageid() + ", " + job7.getRequestor().getValue() + ", " + job7.getActionid() + "}");
        queueManager.addNewJob(job7);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job6, it.next());
        assertEquals(job1, it.next());
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job7, it.next());
        assertEquals(job4, it.next());
        assertEquals(job2, it.next());

        // Insert eighth job into queue
        System.out.println("\n Inserting job with parameters { " + job8.getSrcstorage().getStorageid() + ", " + job8.getDeststorage().getStorageid() + ", " + job8.getRequestor().getValue() + ", " + job8.getActionid() + "}");
        queueManager.addNewJob(job8);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job6, it.next());
        assertEquals(job1, it.next());
        assertEquals(job8, it.next());
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job7, it.next());
        assertEquals(job4, it.next());
        assertEquals(job2, it.next());

        // Remove job 6 from queue
        System.out.println("\n Removing job 6");
        queueManager.removeJob(6);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job7, it.next());
        assertEquals(job1, it.next());
        assertEquals(job8, it.next());
        assertEquals(job4, it.next());
        assertEquals(job2, it.next());

        // Remove job 2 from queue
        System.out.println("\n Removing job 2");
        queueManager.removeJob(2);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job7, it.next());
        assertEquals(job1, it.next());
        assertEquals(job8, it.next());
        assertEquals(job4, it.next());

        // Remove job 1 from queue
        System.out.println("\n Removing job 1");
        queueManager.removeJob(1);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job7, it.next());
        assertEquals(job4, it.next());
        assertEquals(job8, it.next());

        // Remove job 4 from queue
        System.out.println("\n Removing job 4");
        queueManager.removeJob(4);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job5, it.next());
        assertEquals(job3, it.next());
        assertEquals(job7, it.next());
        assertEquals(job8, it.next());

        // Remove job 3 from queue
        System.out.println("\n Removing job 3");
        queueManager.removeJob(3);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job5, it.next());
        assertEquals(job7, it.next());
        assertEquals(job8, it.next());

        // Remove job 5 from queue
        System.out.println("\n Removing job 5");
        queueManager.removeJob(5);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job8, it.next());
        assertEquals(job7, it.next());

        // Remove job 8 from queue
        System.out.println("\n Removing job 8");
        queueManager.removeJob(8);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job7, it.next());

        // Remove job 7 from queue
        System.out.println("\n Removing job 7");
        queueManager.removeJob(7);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertFalse(it.hasNext());
    }

    @Autowired
    CaRequestServiceInterface caRequestSvr;
    
    protected void updateRequestStatus(McAssetMoveQ job, StatusExecucao status) {
        // Atualizando status na ca_request
        CaRequest request = job.getRequest();
        //LOGGER.debug("atualizando STATUS do request " + request.getRequestid() + " para EM_EXECUCAO");
        request.setStatus(status);
        caRequestSvr.saveRequest(request);
    }
    
    /**
     * Set parameters for a new job
     * @param job job to be modified
     * @param srcStorageId source storage id
     * @param destStorageId destination storage id
     * @param requestor requestor id
     * @param priority priority of the job
     */
    private void setJobParameters(Integer jobId, McAssetMoveQ job, int srcStorageId, int destStorageId, int requestor, int priority) {
        job.setMoveid(jobId);
        job.setSrcstorage(new McStorage(srcStorageId));
        job.setDeststorage(new McStorage(destStorageId));
        job.setRequestor(new McMoveRequestor(requestor));
        job.setActionid(priority);
        job.setStatus(MoveStatus.AGENDADO);
        //updateRequestStatus(job, StatusExecucao.INICIO);
    }
    
    /**
     * Perform a test with getFirstScheduled method
     */
    @Test
    public void getFirstScheduledTest() {
        // Create dummy jobs
        McAssetMoveQ job1 = new McAssetMoveQ();
        setJobParameters(1, job1, 1, 1, 1004, 4);
        McAssetMoveQ job2 = new McAssetMoveQ();
        setJobParameters(2, job2, 1, 1, 1004, 4);
        
        // Initialize manager
        QueueManager queueManager = new QueueManagerCircuitPriority();
        Iterator<McAssetMoveQ> it;

        // Insert first job into queue
        System.out.println("\n Inserting job with parameters { " + job1.getSrcstorage().getStorageid() + ", " + job1.getDeststorage().getStorageid() + ", " + job1.getRequestor().getValue() + ", " + job1.getActionid() + "}");
        queueManager.addNewJob(job1);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());
        
        // Get first scheduled
        McAssetMoveQ firstScheduledJob = queueManager.getFirstScheduled();
        System.out.println("First scheduled job: job " + firstScheduledJob.getMoveid());
        assertEquals(job1, firstScheduledJob);
        
        // Reinsert first job into queue
        System.out.println("\n Inserting job with parameters { " + job1.getSrcstorage().getStorageid() + ", " + job1.getDeststorage().getStorageid() + ", " + job1.getRequestor().getValue() + ", " + job1.getActionid() + "}");
        queueManager.addNewJob(job1);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());
        
        // Insert second job into queue
        System.out.println("\n Inserting job with parameters { " + job2.getSrcstorage().getStorageid() + ", " + job2.getDeststorage().getStorageid() + ", " + job2.getRequestor().getValue() + ", " + job2.getActionid() + "}");
        queueManager.addNewJob(job2);
        System.out.println(queueManager.toString());
        it = queueManager.iterator();
        assertEquals(job1, it.next());
        assertEquals(job2, it.next());
        
        // Get second scheduled
        queueManager.getFirstScheduled();
        McAssetMoveQ secondScheduledJob = queueManager.getFirstScheduled();
        assertNotNull(secondScheduledJob);
        System.out.println("Second scheduled job: job " + secondScheduledJob.getMoveid());
        assertEquals(job2, secondScheduledJob);
    }
}
