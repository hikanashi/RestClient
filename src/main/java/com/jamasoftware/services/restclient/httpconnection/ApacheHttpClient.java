package com.jamasoftware.services.restclient.httpconnection;

import com.jamasoftware.services.restclient.exception.JamaApiException;
import com.jamasoftware.services.restclient.exception.RestClientException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.*;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApacheHttpClient implements HttpClient {
    private final Logger logger = LogManager.getLogger(ApacheHttpClient.class);
    private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    protected org.apache.http.client.HttpClient client;
    protected boolean trustallsite = true;
    protected boolean selectclientcert = false;


    public ApacheHttpClient(String keyStorePath, String KeyStorepassowrd) throws RestClientException {
        connectionManager.setDefaultMaxPerRoute(10);
        try {
            SSLContext sslContext = createSSLContext(keyStorePath,KeyStorepassowrd);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                    null,
                    NoopHostnameVerifier.INSTANCE);
            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoTimeout(20000)
                    .build();
            connectionManager.setDefaultSocketConfig(socketConfig);
            client = HttpClients
                    .custom()
                    .disableAuthCaching()
                    .setConnectionTimeToLive(20, TimeUnit.SECONDS)
                    .disableCookieManagement()
                    .disableRedirectHandling()
                    .setSSLSocketFactory(sslsf)
                    .setSSLContext(sslContext)
                    .build();
        } catch (Exception e) {
            throw new RestClientException(e);
        }
    }

    private SSLContext createSSLContext(
            String keyStorePath, String keyStorePassword)
            throws IOException, GeneralSecurityException {

        TrustStrategy trustStrategy = null;
        PrivateKeyStrategy privatekeyStrategy = null;

        if(trustallsite == true ) {
            trustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(final X509Certificate[] chain, final String authType)
                        throws CertificateException {
                    return true;
                }
            };
        }

        if(selectclientcert == true) {
            privatekeyStrategy = new PrivateKeyStrategy() {
                @Override
                public String chooseAlias(Map<String,PrivateKeyDetails> aliases, Socket socket) {
                    return aliases.keySet().iterator().next();
                }            
            };
        }

        SSLContextBuilder sslContextBuilder = SSLContexts.custom();

        KeyStore keystore = null;
        if(keyStorePath != null && keyStorePath.length() > 0) {
            String keyStoreType = "PKCS12";
            char[] keyStorePasswordArray = keyStorePassword.toCharArray();
            keystore = loadKeyStore(keyStorePath, keyStoreType, keyStorePasswordArray);
            if(privatekeyStrategy != null ) {
                sslContextBuilder.loadKeyMaterial(keystore, keyStorePasswordArray, privatekeyStrategy); 
            } else {
                sslContextBuilder.loadKeyMaterial(keystore, keyStorePasswordArray); 
            }
        }

        if(trustStrategy != null) {
            sslContextBuilder.loadTrustMaterial(trustStrategy);
        }

        SSLContext sslContext = sslContextBuilder.build();
        return sslContext;
 
    }

    private static KeyStore loadKeyStore(String keyStorePath, String keyStoreType, char[] keyStorePassword)
            throws IOException, GeneralSecurityException {
        KeyStore keyStore = null;
        try (InputStream is = Files.newInputStream(Paths.get(keyStorePath))) {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(is, keyStorePassword);
        }
        return keyStore;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            connectionManager.close();
        } finally {
            super.finalize();
        }
    }

    private Header getAuthenticationHeader(UsernamePasswordCredentials credentials, HttpRequest request)
            throws RestClientException {
        try {
            return new BasicScheme().authenticate(credentials, request, null);
        } catch (AuthenticationException e) {
            logger.fatal(e.toString(), e);
            throw new RestClientException("Unable to create authentication header");
        }
    }

    private String getEntityContentOrNull(HttpEntity responseEntity) {
        try {
            // return (new BufferedReader(new
            // InputStreamReader(responseEntity.getContent()))).readLine();
            return (new BufferedReader(new InputStreamReader(responseEntity.getContent(), "UTF-8"))).readLine();
        } catch (IOException | NullPointerException e) {
            return null;
        }
    }

    private Response execute(HttpRequestBase request, UsernamePasswordCredentials credentials)
            throws RestClientException {
        HttpEntity responseEntity = null;
        try {
            request.addHeader(getAuthenticationHeader(credentials, request));
            HttpResponse rawResponse = client.execute(request);
            responseEntity = rawResponse.getEntity();
            return new Response(rawResponse.getStatusLine().getStatusCode(),
                    getEntityContentOrNull(rawResponse.getEntity()));
        } catch (IOException e) {
            throw new RestClientException(e);
        } finally {
            try {
                EntityUtils.consume(responseEntity);
            } catch (IOException e) {
                // Not throwing here because it's clean up
                logger.fatal(e.toString(), e);
            }
        }
    }

    public Response get(String url, String username, String password, String apiKey) throws RestClientException {
        logger.debug("GET REQ:" + url);
        HttpGet getRequest = new HttpGet(url);
        if (apiKey != null) {
            getRequest.setHeader("api-key", apiKey);
        }
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        Response response = execute(getRequest, credentials);
        
        logger.debug("GET RES:" + String.valueOf(response.getStatusCode()));
        if (response.getStatusCode() >= 400) {
            throw new JamaApiException(response.getStatusCode(), response.getResponse() + "\nURL: " + url);
        }

        logger.debug(response.getResponse());
        return response;
    }

    public Response delete(String url, String username, String password, String apiKey) throws RestClientException {
        logger.debug("DELETE REQ:" + url);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        HttpDelete deleteRequest = new HttpDelete(url);
        if (apiKey != null) {
            deleteRequest.setHeader("api-key", apiKey);
        }

        Response response = execute(deleteRequest, credentials);
        logger.debug("DELETE RES:" + String.valueOf(response.getStatusCode()));

        if (response.getStatusCode() >= 400) {
            throw new JamaApiException(response.getStatusCode(), response.getResponse() + "\nURL: " + url);
        }
        return response;
    }

    public Response post(String url, String username, String password, String apiKey, String payload)
            throws RestClientException {
        logger.debug("POST REQ:" + url);
        logger.debug("POST BODY:" + payload);

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        HttpPost postRequest = new HttpPost(url);
        if (apiKey != null) {
            postRequest.setHeader("api-key", apiKey);
        }
        StringEntity body = new StringEntity(payload, "UTF-8");
        body.setContentType("application/json");
        postRequest.setEntity(body);

        Response response = execute(postRequest, credentials);
        logger.debug("POST RES:" + String.valueOf(response.getStatusCode()));
        
        if (response.getStatusCode() >= 400) {
            throw new JamaApiException(response.getStatusCode(),
                    response.getResponse() + "\nURL: " + url + "\nPayload: " + payload);
        }
        return response;
    }

    public Response put(String url, String username, String password, String apiKey, String payload)
            throws RestClientException {
        logger.debug("PUT REQ:" + url);
        logger.debug("PUT BODY:" + payload);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        HttpPut putRequest = new HttpPut(url);
        if (apiKey != null) {
            putRequest.setHeader("api-key", apiKey);
        }

        StringEntity body = new StringEntity(payload, "UTF-8");
        body.setContentType("application/json");
        putRequest.setEntity(body);

        Response response = execute(putRequest, credentials);
        logger.debug("PUT RES:" + String.valueOf(response.getStatusCode()));

        if (response.getStatusCode() >= 400) {
            throw new JamaApiException(response.getStatusCode(),
                    response.getResponse() + "\nURL: " + url + "\nPayload: " + payload);
        }
        return response;
    }

    public Response putFile(String url, String username, String password, String apiKey, File file)
            throws RestClientException {
        logger.debug("PUT REQ:" + url);
        logger.debug("PUT FILE:" + file.getPath());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        HttpPut putRequest = new HttpPut(url);
        if (apiKey != null) {
            putRequest.setHeader("api-key", apiKey);
        }
        
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody("file", file);
        multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
        HttpEntity fileEntity = multipartEntityBuilder.build();
        putRequest.setEntity(fileEntity);
        Response response = execute(putRequest, credentials);
        logger.debug("PUT RES:" + String.valueOf(response.getStatusCode()));
        if (response.getStatusCode() >= 400) {
            throw new JamaApiException(response.getStatusCode(),
                    response.getResponse() + "\nURL: " + url + ", Filename: " + file.getName());
        }
        return response;
    }

    public FileResponse getFile(String url, String username, String password, String apiKey)
            throws RestClientException {
        logger.debug("GET(File) REQ:" + url);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        HttpEntity responseEntity = null;
        HttpGet request = new HttpGet(url);
        if (apiKey != null) {
            request.setHeader("api-key", apiKey);
        }

        try {
            request.addHeader(getAuthenticationHeader(credentials, request));
            HttpResponse rawResponse = client.execute(request);
            logger.debug("GET(File) RES:" + String.valueOf(rawResponse.getStatusLine().getStatusCode()));
            responseEntity = rawResponse.getEntity();
            return new FileResponse(rawResponse.getStatusLine().getStatusCode(),
                    rawResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new RestClientException(e);
        } finally {
            try {
                EntityUtils.consume(responseEntity);
            } catch (IOException e) {
                // Not throwing here because it's clean up
                logger.fatal(e.toString(), e);
            }
        }
    }
}
