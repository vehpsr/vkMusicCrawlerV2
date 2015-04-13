package com.gans.vk.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.gans.vk.utils.RestUtils;

public class HttpVkConnector {
    private static final Log LOG = LogFactory.getLog(HttpVkConnector.class);

    private CloseableHttpClient _httpClient = null;
    private String _cookie = null;

    private String _authCookieDomain;
    private String _authLoginUrlPattern;
    private String _contentType;
    private String _userAgent;
    private String _login;
    private String _pass;

    public String get(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        HttpGet httpGet = new HttpGet(url);
        return executeHttpMethod(httpGet);
    }

    public String post(String url, String postEntity) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(postEntity));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
            return "";
        }
        return executeHttpMethod(httpPost);
    }

    private String executeHttpMethod(HttpUriRequest method) {
        setHeaders(method);

        CloseableHttpResponse response = null;
        try {
            response = getHttpClient().execute(method);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error(MessageFormat.format("Fail to reach {0}, response: {1}", method.getURI(), response.getStatusLine().getStatusCode()));
                return "";
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            if (e instanceof ClientProtocolException || e instanceof IOException) {
                LOG.error(MessageFormat.format("Fail to parse response from {0}: {1}", method.getURI(), e.getMessage()));
            } else {
                throw new IllegalStateException("System error", e);
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new IllegalStateException("System error", e);
                }
            }
        }
        return "";
    }

    private CloseableHttpClient getHttpClient() {
        if (_httpClient == null) {
            _httpClient = HttpClients.createDefault();
        }
        return _httpClient;
    }

    private void setHeaders(HttpUriRequest method) {
        if (StringUtils.isEmpty(_cookie)) {
            _cookie = getAuthenticationCookie();
        }

        method.setHeader("Cookie", _cookie);
        method.setHeader(HttpHeaders.USER_AGENT, _userAgent);
        method.setHeader(HttpHeaders.CONTENT_TYPE, _contentType);
    }

    private String getAuthenticationCookie() {
        final String VK_SECURITY_COOKIE = "remixsid=";

        CloseableHttpClient sslHttpClient = null;
        CloseableHttpClient httpClient = null;
        try {
            BasicCookieStore cookieStore = new BasicCookieStore();

            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();
            sslHttpClient = HttpClients
                    .custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                    .setDefaultCookieStore(cookieStore)
                    .build();

            String authUrl = MessageFormat.format(_authLoginUrlPattern, _login, _pass);
            HttpPost httpPost = new HttpPost(authUrl);
            HttpResponse response = sslHttpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
                throw new IllegalStateException(MessageFormat.format("Expected redirect after successfull login, but got: {0}", response.getStatusLine().getStatusCode()));
            }

            String redirectUrl = "";
            for (Header location : response.getHeaders(HttpHeaders.LOCATION)) {
                redirectUrl = location.getValue();
            }

            CloseableHttpClient httpclient = HttpClients
                    .custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();

            httpclient.execute(new HttpGet(redirectUrl));

            StringBuilder builder = new StringBuilder();
            List<Cookie> cookies = cookieStore.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getDomain().equals(_authCookieDomain)) {
                    builder.append(cookie.getName());
                    builder.append("=");
                    builder.append(cookie.getValue());
                    builder.append(";");
                }
            }
            String result = builder.toString();
            if (!result.contains(VK_SECURITY_COOKIE)) {
                throw new IllegalStateException(MessageFormat.format("Fail to authenticate. Not found secure cookie {0} in response for login {1}", VK_SECURITY_COOKIE, _login));
            }
            RestUtils.sleep();
            return result;
        } catch (Exception e) {
            if (e instanceof NoSuchAlgorithmException ||
                    e instanceof KeyStoreException ||
                    e instanceof KeyManagementException ||
                    e instanceof ClientProtocolException ||
                    e instanceof IOException) {
                LOG.error(MessageFormat.format("VK authentication fails with reason: {0}", e.getMessage()));
            }
            throw new IllegalStateException("System error", e);
        } finally {
            try {
                if (sslHttpClient != null) {
                    sslHttpClient.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException("System error", e);
            }
        }
    }

    public void setAuthCookieDomain(String authCookieDomain) {
        _authCookieDomain = authCookieDomain;
    }

    public void setAuthLoginUrlPattern(String authLoginUrlPattern) {
        _authLoginUrlPattern = authLoginUrlPattern;
    }

    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    public void setUserAgent(String userAgent) {
        _userAgent = userAgent;
    }

    public void setLogin(String login) {
        _login = login;
    }

    public void setPass(String pass) {
        _pass = pass;
    }

}
