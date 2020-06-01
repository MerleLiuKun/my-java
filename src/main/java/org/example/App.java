package org.example;

import org.alg.SnowFlakeIdWorker;

/**
 * @author Ikaros
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        SnowFlakeIdWorker worker = new SnowFlakeIdWorker(1023);

        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; System.currentTimeMillis() - start < 1000; i++, count = i) {
            worker.nextId();
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
        System.out.println(count);
    }
}
