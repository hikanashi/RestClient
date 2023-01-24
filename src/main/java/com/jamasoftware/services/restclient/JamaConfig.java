package com.jamasoftware.services.restclient;

import com.jamasoftware.services.restclient.httpconnection.ApacheHttpClient;
import com.jamasoftware.services.restclient.httpconnection.HttpClient;
import com.jamasoftware.services.restclient.json.JsonHandler;
import com.jamasoftware.services.restclient.json.SimpleJsonHandler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JamaConfig {
    private final Logger logger = LogManager.getLogger(JamaConfig.class);

    private String baseUrl;
    // TODO add ending forward slash off of domain name
    private String username;
    private String password;
    private JsonHandler json;
    private HttpClient httpClient;
    private Integer resourceTimeOut;    //value is seconds
    private String openUrlBase;
    private String apiKey = null;
    private String clientCertFilePath;
    private String clientCertPassword;

    public JamaConfig() {
        json = new SimpleJsonHandler();
    }

    public JamaConfig(boolean loadFromPropertiesFile, String filename) {
        this();
        loadProperties(loadFromPropertiesFile, filename);
        initializeHttpClient();
    }


    public JamaConfig(boolean loadFromPropertiesFile) {
        this();
        loadProperties(loadFromPropertiesFile, "jama.properties");
        initializeHttpClient();
    }

    private void loadProperties(boolean loadFromPropertiesFile, String filename) {
        if(!loadFromPropertiesFile) {
            return;
        }

        InputStream input = null;
        try {
            Properties properties = new Properties();
            input = new FileInputStream(filename);
            properties.load(input);
            setBaseUrl(properties.getProperty("baseUrl"));
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            String timeOutString = properties.getProperty("resourceTimeOut");
            setOpenUrlBase(properties.getProperty("baseUrl"));
            apiKey = properties.getProperty("apiKey");
            resourceTimeOut = Integer.valueOf(timeOutString);
            clientCertFilePath = properties.getProperty("clientCertFilePath");
            clientCertPassword = properties.getProperty("clientCertPassword");
        } catch(Exception e) {
            logger.error("can't load properties:"+ filename, e);
        }
    }

    private void initializeHttpClient() {
        try {
            httpClient = new ApacheHttpClient(clientCertFilePath,clientCertPassword);
        } catch(Exception e) {
            logger.error("can't initialize HttpClient",e);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        if(baseUrl.contains("/rest/")) {
            if(baseUrl.endsWith("/")) {
                this.baseUrl = baseUrl;
                return;
            }
            this.baseUrl = baseUrl + "/";
            return;
        }
        if(baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl + "rest/v1/";
            return;
        }
        this.baseUrl = baseUrl + "/rest/v1/";
    }

    public void setOpenUrlBase(String baseOpenUrl) {
        if(baseOpenUrl.contains("/perspective.req#/")) {
            if (baseOpenUrl.contains("items/")) {
                if (baseOpenUrl.endsWith("/")) {
                    this.openUrlBase = baseOpenUrl;
                    return;
                }
                this.openUrlBase = baseOpenUrl + "/";
                return;
            }
            else {
                if(baseOpenUrl.endsWith("/")){
                    this.openUrlBase = baseOpenUrl + "items/";
                }
                else {
                    this.openUrlBase = baseOpenUrl + "/items/";
                }
            }
        } else {
            if (baseOpenUrl.endsWith("/")) {
                this.openUrlBase = baseOpenUrl + "perspective.req#/items/";
                return;
            }
            this.openUrlBase = baseOpenUrl + "/perspective.req#/items/";
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JsonHandler getJson() {
        return json;
    }

    public void setJson(JsonHandler json) {
        this.json = json;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Integer getResourceTimeOut() {
        return resourceTimeOut;
    }

    public void setResourceTimeOut(Integer resourceTimeOut) {
        this.resourceTimeOut = resourceTimeOut;
    }

    public String getOpenUrlBase() {
        return this.openUrlBase;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return this.apiKey;
    }
}
