package com.pnxtest.syncloud;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class HttpResendQueue {
    private static final int MAX_POST_ATTEMPTS = 3;
    private final Queue<HttpResendQueueItem> resendQueue;

    public HttpResendQueue(int maxSize) {
        this.resendQueue = new SynchronizedEvictingQueue(maxSize);
    }

    public int size() {
        return this.resendQueue.size();
    }

    public void offer(byte[] request, IOException e) {
        this.resendQueue.offer(new HttpResendQueueItem(request));
    }

    public void offer(byte[] request, HttpException e) {
        if (!e.isClientError()) {
            this.resendQueue.offer(new HttpResendQueueItem(request));
        }

    }

    public void drain(ApiClient httpClient, String path) {
        this.drain(httpClient, path, false);
    }

    public void drain(ApiClient httpClient, String path, boolean gzip) {
        if (!this.resendQueue.isEmpty()) {
            try {
                //LOGGER.info("Attempting to retransmit {} requests", this.resendQueue.size());

                while(!this.resendQueue.isEmpty()) {
                    HttpResendQueueItem item = (HttpResendQueueItem)this.resendQueue.peek();

                    try {
                        byte[] jsonBytes = item.getJsonBytes();
                        httpClient.post(path, jsonBytes, gzip);
                        this.resendQueue.remove();
                        Threads.sleepQuietly(250L, TimeUnit.MILLISECONDS);
                    } catch (Throwable var6) {
                        item.failed();
                        if (3 <= item.getNumFailures()) {
                            this.resendQueue.remove();
                        }

                        throw var6;
                    }
                }
            } catch (Throwable var7) {
                //LOGGER.info("Failure retransmitting queued requests", var7);
            }
        }

    }
}
