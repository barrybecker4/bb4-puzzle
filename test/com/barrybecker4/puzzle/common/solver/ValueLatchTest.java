package com.barrybecker4.puzzle.common.solver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The ValueLatch blocks until the its result has been set.
 */
public class ValueLatchTest {

    private ValueLatch<Integer> valueLatch = new ValueLatch<>();

    @Test
    public void testValueLatchResult() throws Exception {
        computeResult(100);
        assertEquals("Unexpected counter value", (Integer) 4950, valueLatch.getValue());
    }

    /** only the first result is retained. Successive results that are set are ignored */
    @Test
    public void testValueLatchMultipleSets() throws Exception {
        computeResult(10);
        computeResult(20);
        assertEquals("Unexpected counter value", (Integer) 45, valueLatch.getValue());
    }


    /** computes the value latch result in a separate thread */
    void computeResult(int num) throws InterruptedException {
         new Thread(new Worker(num)).start();
    }


    class Worker implements Runnable {
        int num;
        Worker(int num) {
            this.num = num;
        }

        public void run() {
            int sum = 0;
            for (int i=0; i<num; i++) {
               sum += i;
            }
            valueLatch.setValue(sum);
        }

    }


}
