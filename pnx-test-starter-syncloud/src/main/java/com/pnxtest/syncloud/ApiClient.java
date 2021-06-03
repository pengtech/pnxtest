package com.pnxtest.syncloud;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

public class ApiClient {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 15000;
    private final ApiConfiguration apiConfig;
    private final Proxy proxy;

    public ApiClient(ApiConfiguration apiConfig) {
        this.apiConfig = apiConfig;
        this.proxy = HttpProxy.fromSystemProperties();
    }

    public String post(String path, byte[] jsonBytes) throws IOException, HttpException {
        return this.post(path, jsonBytes, false);
    }

    public String post(String path, byte[] jsonBytes, boolean gzip) throws IOException, HttpException {
        HttpURLConnection connection = null;

        String var8;
        try {
            URL url = new URL(this.apiConfig.getApiUrl() + path);
            connection = (HttpURLConnection)url.openConnection(this.proxy);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            if (gzip) {
                connection.setRequestProperty("Content-Encoding", "gzip");
            }

            connection.setRequestProperty("X-Stackify-Key", this.apiConfig.getApiKey());
            connection.setRequestProperty("X-Stackify-PV", "V1");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
            OutputStream stream = null;
            if (gzip) {
                stream = new BufferedOutputStream(new GZIPOutputStream(connection.getOutputStream()));
            } else {
                stream = new BufferedOutputStream(connection.getOutputStream());
            }

            stream.write(jsonBytes);
            stream.flush();
            stream.close();
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new HttpException(statusCode);
            }

            var8 = this.readAndClose(connection.getInputStream());
        } finally {
            if (connection != null) {
                try {
                    this.readAndClose(connection.getInputStream());
                } catch (Throwable var18) {
                }

                try {
                    this.readAndClose(connection.getErrorStream());
                } catch (Throwable var17) {
                }
            }

        }

        return var8;
    }

    private String readAndClose(InputStream stream) throws IOException {
        String contents = null;
//        if (stream != null) {
//            contents = CharStreams.toString(new InputStreamReader(new BufferedInputStream(stream), "UTF-8"));
//            stream.close();
//        }

        return contents;
    }

}
