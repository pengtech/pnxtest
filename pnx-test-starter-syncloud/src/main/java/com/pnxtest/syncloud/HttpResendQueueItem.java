package com.pnxtest.syncloud;

public class HttpResendQueueItem {
    private final byte[] jsonBytes;
    private int numFailures;

    public HttpResendQueueItem(byte[] jsonBytes) {
        this.jsonBytes = jsonBytes;
        this.numFailures = 1;
    }

    public byte[] getJsonBytes() {
        return this.jsonBytes;
    }

    public int getNumFailures() {
        return this.numFailures;
    }

    public void failed() {
        ++this.numFailures;
    }
}
