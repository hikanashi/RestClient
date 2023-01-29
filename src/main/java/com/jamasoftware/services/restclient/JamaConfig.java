package com.jamasoftware.services.restclient;

import com.jamasoftware.services.restclient.httpconnection.ApacheHttpClient;
import com.jamasoftware.services.restclient.httpconnection.HttpClient;
import com.jamasoftware.services.restclient.json.JsonHandler;
import com.jamasoftware.services.restclient.json.SimpleJsonHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public JamaConfig(boolean loadFromPropertiesFile, String filename) throws Exception {
        this();
        loadProperties(loadFromPropertiesFile, filename);
    }


    public JamaConfig(boolean loadFromPropertiesFile) throws Exception {
        this();
        loadProperties(loadFromPropertiesFile, "jama.properties");
    }

    private void loadProperties(boolean loadFromPropertiesFile, String filename) throws Exception {
        if(!loadFromPropertiesFile) {
            return;
        }

        InputStream input = null;

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
    }

    public void saveProperties(String filename) throws Exception {
        OutputStream ouput = null;
        try {
            Properties properties = new Properties();

            setValidProperty(properties, "baseUrl", baseUrl);
            setValidProperty(properties, "username", username);
            setValidProperty(properties,"password", password);

            if(resourceTimeOut == null) {
                throw new NullPointerException("Resource Timeout is not set");
            }
            setValidProperty(properties,"resourceTimeOut", resourceTimeOut.toString());
            setValidProperty(properties,"apiKey", apiKey);
            
            if(clientCertFilePath != null && clientCertFilePath.length() > 0) {
                Path pathobj = Paths.get(clientCertFilePath);
                if (Files.exists(pathobj) != true) {
                    logger.error("client cert file is not exist:"+ clientCertFilePath);
                    throw new IOException("client cert file is not exist:"+ clientCertFilePath);
                }

                setValidProperty(properties,"clientCertFilePath", clientCertFilePath);
                setValidProperty(properties,"clientCertPassword", clientCertPassword);
            } else {
                if(clientCertPassword != null && clientCertPassword.length() > 0) {
                    throw new IllegalArgumentException("Enter the path to the client certificate.");
                }
            }

            // for (String name : properties.stringPropertyNames()) {
            //     logger.debug("property name:" + name + " value:" + properties.getProperty(name, ""));
            // }
            ouput = new FileOutputStream(filename);
            properties.store(ouput,filename);
        } catch(Exception e) {
            throw e;
        }
    }

    private void setValidProperty(Properties properties, String name, String value) throws IllegalArgumentException, NullPointerException {
        if( value == null) {
            throw new NullPointerException("properties is null. name:"+ name);
        }

        if( value.length() <= 0) {
            throw new IllegalArgumentException("properties is illegal. name:"+ name);
        }

        logger.debug("setValidProperty name:"+ name + " value:" + value);
        properties.setProperty(name, value);
    }

    private void initializeHttpClient(String clientCertFilePath, String clientCertPassword) {

        try {
            httpClient = new ApacheHttpClient(clientCertFilePath,clientCertPassword);
        } catch(Exception e) {
            logger.error("Can't initialize HttpClient",e);
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
        if(httpClient == null) {
            initializeHttpClient(clientCertFilePath, clientCertPassword);
        }

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

    public void setClientCertFilePath(String clientCertFilePath) {
        this.clientCertFilePath = clientCertFilePath;
    }

    public String getClientCertFilePath() {
        return this.clientCertFilePath;
    }

    public void setClientCertPassword(String clientCertPassword) {
        this.clientCertPassword = clientCertPassword;
    }

    public String getClientCertPassword() {
        return this.clientCertPassword;
    }
}
