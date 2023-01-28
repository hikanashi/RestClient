package com.jamasoftware.services.restclient.httpconnection;

import com.jamasoftware.services.restclient.exception.RestClientException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class TestHttpClient implements HttpClient {
    private static final Logger logger = LogManager.getLogger(TestHttpClient.class);

    private JSONObject responses;
    private JSONObject payloads;
    public TestHttpClient() {
        loadTestData(
            "testData" + File.separator + "testApiResponses.json", 
            "testData" + File.separator + "testApiPayloads.json");
    }

    public TestHttpClient(String responseFile, String PayloadFile) {
        loadTestData(responseFile, PayloadFile);
    }

    private void loadTestData(String responseFile, String PayloadFile) {
        JSONParser parser = new JSONParser();
        try {
            responses = (JSONObject)parser.parse(new FileReader(responseFile));
            payloads = (JSONObject)parser.parse(new FileReader(PayloadFile));
        } catch (IOException | ParseException e) {
            logger.error(e);
        }   

    }

    @Override
    public Response get(String url, String username, String password, String apiKey) throws RestClientException {
        String key = "GET " + url;
        JSONObject responseObject = (JSONObject)responses.get(key);
        String response = ((JSONObject)responseObject.get("response")).toJSONString();
        Integer statusCode = (int)(long)responseObject.get("statusCode");

        logger.info(key);
        return new Response(statusCode, response);
    }

    @Override
    public Response delete(String url, String username, String password, String apiKey) throws RestClientException {
        return null;
    }

    @Override
    public Response post(String url, String username, String password, String apiKey, String payload) throws RestClientException {
        String key = "POST " + url;
        JSONObject payloadObject = (JSONObject)payloads.get(key);

        String expectedPayload = ((JSONObject)payloadObject.get("payload")).toJSONString();
        String response = ((JSONObject)payloadObject.get("response")).toJSONString();
        Integer statusCode = (int)(long)payloadObject.get("statusCode");

        if(expectedPayload == null) {
            throw new RestClientException("Payloads object returned null for POST " + url);
        }
        try {
            JSONAssert.assertEquals(expectedPayload, payload, true);
        } catch (JSONException e) {
            throw new RestClientException(e);
        }

        return new Response(statusCode, response);
    }

    @Override
    public Response put(String url, String username, String password, String apiKey, String payload) throws RestClientException {
        String key = "PUT " + url;
        JSONObject payloadObject = (JSONObject)payloads.get(key);

        String expectedPayload = ((JSONObject)payloadObject.get("payload")).toJSONString();
        String response = ((JSONObject)payloadObject.get("response")).toJSONString();
        Integer statusCode = (int)(long)payloadObject.get("statusCode");

        if(expectedPayload == null) {
            throw new RestClientException("Payloads object returned null for POST " + url);
        }
        try {
            JSONAssert.assertEquals(expectedPayload, payload, true);
        } catch (JSONException e) {
            throw new RestClientException(e);
        }

        return new Response(statusCode, response);
    }

    @Override
    public Response putFile(String url, String username, String password, String apiKey, File file) throws RestClientException {
        throw new RestClientException("PUT file upload is not supported " + url);
    }

    @Override
    public FileResponse getFile(String url, String username, String password, String apiKey) throws RestClientException {
        String key = "GET " + url;
        JSONObject responseObject = (JSONObject)responses.get(key);
        String responsepath = ((JSONObject)responseObject.get("filepath")).toJSONString();
        Integer statusCode = (int)(long)responseObject.get("statusCode");

        try {
            InputStream inputStream = new FileInputStream(responsepath);
            return new FileResponse(statusCode, inputStream );
        } catch (IOException e) {
            throw new RestClientException(e);
        }
    }
}
