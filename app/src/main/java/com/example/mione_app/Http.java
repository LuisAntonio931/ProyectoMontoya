package com.example.mione_app;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    private Context context;
    private String url, method = "GET", data = null, response = null;
    private Integer statusCode = 0;
    private Boolean token = false;
    private LocalStorage localStorage;

    public Http(Context context, String url) {
        this.context = context;
        this.url = url;
        this.localStorage = new LocalStorage(context);
    }

    public Boolean getToken() {
        return token;
    }

    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setToken(Boolean token) {
        this.token = token;
    }

    public String getResponse() {
        return response;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void send() {
        HttpURLConnection connection = null;
        try {
            URL sUrl = new URL(url);
            connection = (HttpURLConnection) sUrl.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/vnd.api+json");
            connection.setRequestProperty("Accept", "application/vnd.api+json");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            if (token) {
                connection.setRequestProperty("Authorization", "Bearer " + localStorage.getToken());
            }

            if (!method.equals("GET")) {
                connection.setDoOutput(true);
            }

            if (data != null) {
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
            }

            statusCode = connection.getResponseCode();
            Log.d("Http", "Response Code: " + statusCode);

            InputStreamReader isr;
            if (statusCode >= 200 && statusCode <= 299) {
                isr = new InputStreamReader(connection.getInputStream());
            } else {
                isr = new InputStreamReader(connection.getErrorStream());
            }

            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            response = sb.toString();
            Log.d("Http", "Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Http", "Exception: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
