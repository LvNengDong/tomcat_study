package org.example;

import sun.dc.pr.PRError;

/**
 * @Author lnd
 * @Description
 * @Date 2024/3/13 15:28
 */
public class VolatileTest {
    public static int COUNTER = 0;

    public static void main(String[] args) {
        new ChangeListener().start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new ChangeMaker().start();
    }

    static class ChangeListener extends Thread {
        @Override
        public void run() {
            int threadValue = COUNTER;
            while (threadValue < 5) {
                if (threadValue != COUNTER) {
                    System.out.println("Got Change for COUNTER : " + COUNTER + "");
                    threadValue = COUNTER;
                }
                //try {
                //    Thread.sleep(5);
                //} catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }

    static class ChangeMaker extends Thread {
        @Override
        public void run() {
            int threadValue = COUNTER;
            while (COUNTER < 5) {
                System.out.println("Incrementing COUNTER to : " + (threadValue + 1) + "");
                COUNTER = ++threadValue;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
