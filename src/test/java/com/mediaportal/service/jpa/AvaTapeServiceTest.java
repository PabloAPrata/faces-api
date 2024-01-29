package com.mediaportal.service.jpa;

import com.mediaportal.jpa.converters.TapeAuxStatus;
import com.mediaportal.jpa.daos.AvaTapeDao;
import com.mediaportal.jpa.daos.AvaTapeProxyDao;
import com.mediaportal.jpa.domain.AvaTape;
import com.mediaportal.jpa.services.AvaTapeServiceInterface;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test class for AvaTapeService class. AvaTapeService is the service class for
 * all AvaTape-related operations.
 *
 * @author renato
 */
@RunWith(SpringRunner.class)
public class AvaTapeServiceTest {

    /**
     * Setting up autowired dependencies.
     */
    @TestConfiguration
    static class AvaTapeServiceTestContextConfiguration {
        @Bean
        public AvaTapeServiceInterface tapeService() {
            return new AvaTapeService();
        }
    }

    /**
     * Tape service object, which we will test.
     */
    @Autowired
    private AvaTapeServiceInterface tapeService;

    /**
     * AvaTapeDao is a dependency for our service class. Thus, it will be mocked.
     */
    @MockBean
    private AvaTapeDao tapeDao;

    /**
     * AvaTapeProxyDao is another dependency that will be mocked.
     */
    @MockBean
    private AvaTapeProxyDao tapeProxyDao;
    
    /**
     * Tape pool name. It does not matter the value, as long as it is consistent
     * over all calls.
     */
    private final String POOL_NAME = "pool";

    /**
     * Testing if, considering there is a tape with required free space, it is
     * correctly returned.
     */
    @Test
    public void whenOneFreeTape_thenReturnIt() {
        // mock set up
        AvaTape tape = new AvaTape();
        tape.setFree(2000L);
        Mockito.when(tapeDao.getFreeTape(POOL_NAME, 0L)).thenReturn(tape);
        
        // test - try to obtain a tape with 1000 free space
        long proxySize = 1000L;
        AvaTape freeTape = tapeService.getFreeTape(POOL_NAME, proxySize);
        
        // assertions
        assertNotNull(freeTape);
        assertThat(freeTape.getFree(), is(greaterThanOrEqualTo(proxySize)));
    }

    /**
     * Testing if, considering there is a tape with required free space, it is
     * correctly returned.
     */
    @Test
    public void whenTwoFreeTape_thenReturnFirstWithSpace() {
        // tape 1 does not have enough free space
        AvaTape tape1 = new AvaTape();
        tape1.setTapeid("tape1");
        tape1.setFree(2000L);
        // tape 2 has enough free space
        AvaTape tape2 = new AvaTape();
        tape2.setTapeid("tape2");
        long tape2FreeSpace = 4000L;
        tape2.setFree(4000L);
        // setting method behavior
        Mockito.when(tapeDao.getFreeTape(POOL_NAME, 0L)).thenReturn(tape1, tape2);
        
        // test - try to obtaing a tape with 3000 free space
        long proxySize = 3000L;
        AvaTape freeTape = tapeService.getFreeTape(POOL_NAME, proxySize);
        
        // assertions - it should have been returned the second tape
        assertNotNull(freeTape);
        assertThat(freeTape.getFree(), is(greaterThanOrEqualTo(proxySize)));
        assertEquals((long) freeTape.getFree(), tape2FreeSpace);
        assertEquals(freeTape.getTapeid(), "tape2");
        // P.S.: setting tape status to FULL was not tested here
    }

    /**
     * Testing if, considering there is just one tape and that it does have the
     * required free space, it will change its status to FULL and return null.
     */
    @Test
    public void whenNotEnoughFreeSizeTape_thenSaveFullAndReturnNull() {
        // mock set up
        AvaTape tape1 = new AvaTape();
        tape1.setFree(2000L);
        Mockito.when(tapeDao.getFreeTape(POOL_NAME, 0L)).thenReturn(tape1, (AvaTape) null);
        tape1.setStatus2(TapeAuxStatus.FULL);
        Mockito.doNothing().when(tapeDao).save(tape1);
        
        // test - no tape should be returned
        long proxySize = 3000L;
        AvaTape freeTape = tapeService.getFreeTape(POOL_NAME, proxySize);
        
        // assertions - required proxy size is more than any free tape's free space, thus, it returns null
        assertNull(freeTape);
        // tapeDao must have had its method called 3 times: 
        // 1 - getFreeTape, returning a tape which does not have enough free space
        // 2 - save, saving a tape with full status
        // 3 - getFreeTape, returning null
        assertEquals(Mockito.mockingDetails(tapeDao).getInvocations().size(), 3);
        // Alternatively, tapeDao.save should have been called just once
        Mockito.verify(tapeDao, Mockito.times(1)).save(tape1);
    }
    
    /**
     * Testing if, considering there is a tape with required free space, it is
     * correctly returned.
     */
    @Test
    public void whenTwoFreeTape_thenReturnList() {
        // tape 1 does not have enough free space
        AvaTape tape1 = new AvaTape();
        tape1.setTapeid("tape1");
        tape1.setFree(2000L);
        // tape 2 has enough free space
        AvaTape tape2 = new AvaTape();
        tape2.setTapeid("tape2");
        long tape2FreeSpace = 4000L;
        tape2.setFree(4000L);
        
        List<AvaTape> tapeList = new ArrayList<>();
        tapeList.add(tape1);
        tapeList.add(tape2);
        // setting method behavior
        Mockito.when(tapeDao.getFreeTapes(POOL_NAME, 0L)).thenReturn(tapeList);
        
        // test - try to obtaing a tape with 3000 free space
        long proxySize = 3000L;
        List<AvaTape> freeTapes = tapeService.getFreeTapes(POOL_NAME, proxySize);
        AvaTape freeTape = freeTapes.stream().findFirst().get();
        
        // assertions - it should have been returned the second tape
        assertNotNull(freeTape);
        assertThat(freeTape.getFree(), is(greaterThanOrEqualTo(proxySize)));
        assertEquals((long) freeTape.getFree(), tape2FreeSpace);
        assertEquals(freeTape.getTapeid(), "tape2");
        // P.S.: setting tape status to FULL was not tested here
    }
}
