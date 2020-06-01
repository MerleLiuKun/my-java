package org.alg;


/**
 * @author ikaros
 */
public class SnowFlakeIdWorker {
    private final long startEpoch = 1589904000000L;

    private final long workerIdBits = 10L;

    private final long maxWorkerId = ~(-1L << workerIdBits);

    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;

    private final long timestampLeftShift = sequenceBits + workerIdBits;

    private final long sequenceMask = ~(-1L << sequenceBits);

    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowFlakeIdWorker(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format(
                            "Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp
                    )
            );
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return ((timestamp - startEpoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，知道获取到新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 当前可用的时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取毫秒数
     *
     * @return 当前毫秒数
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

}