package com.pnxtest.syncloud;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class LogSender {
    private static final String LOG_SAVE_PATH = "/Log/Save";
    private final ApiConfiguration apiConfig;
    private final ObjectMapper objectMapper;
    private final HttpResendQueue resendQueue = new HttpResendQueue(20);

    public LogSender(ApiConfiguration apiConfig, ObjectMapper objectMapper) {
        //Preconditions.checkNotNull(apiConfig);
        //Preconditions.checkNotNull(objectMapper);
        this.apiConfig = apiConfig;
        this.objectMapper = objectMapper;
    }

    public int send(LogMsgGroup group) throws IOException {
        //Preconditions.checkNotNull(group);
        ApiClient httpClient = new ApiClient(this.apiConfig);
        this.resendQueue.drain(httpClient, "/Log/Save", true);
        byte[] jsonBytes = this.objectMapper.writer().writeValueAsBytes(group);
        boolean var4 = true;

        int statusCode;
        try {
            httpClient.post("/Log/Save", jsonBytes, true);
            statusCode = 200;
        } catch (IOException var6) {
            //LOGGER.info("Queueing logs for retransmission due to IOException");
            this.resendQueue.offer(jsonBytes, var6);
            throw var6;
        } catch (HttpException var7) {
            statusCode = var7.getStatusCode();
            //LOGGER.info("Queueing logs for retransmission due to HttpException", var7);
            this.resendQueue.offer(jsonBytes, var7);
        }

        return statusCode;
    }
}
