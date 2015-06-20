package com.gans.vk.httpclient;

import java.io.Closeable;
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
import org.apache.http.ParseException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gans.vk.utils.HtmlUtils;

public class HttpVkConnector {
    private static final Log LOG = LogFactory.getLog(HttpVkConnector.class);

    private CloseableHttpClient _httpClient = null;
    private String _cookie = null;

    private String _authCookieDomain;
    private String _authLoginParamsPattern;
    private String _contentType;
    private String _userAgent;
    private String _vkDomain;
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

        try (CloseableHttpResponse response = getHttpClient().execute(method)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOG.error(MessageFormat.format("Fail to reach {0}, response: {1}", method.getURI(), response.getStatusLine().getStatusCode()));
                return "";
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (IOException e) {
            LOG.error(MessageFormat.format("Fail to parse response from {0}: {1}", method.getURI(), e.getMessage()));
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

            httpClient = HttpClients
                    .custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();

            String authUrl = getAuthUrl(httpClient);

            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();
            sslHttpClient = HttpClients
                    .custom()
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                    .setDefaultCookieStore(cookieStore)
                    .build();

            HttpPost httpPost = new HttpPost(authUrl);
            HttpResponse response = sslHttpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_MOVED_TEMPORARILY) {
                throw new IllegalStateException(MessageFormat.format("Expected redirect after successfull login, but got: {0}", response.getStatusLine().getStatusCode()));
            }

            String redirectUrl = "";
            for (Header location : response.getHeaders(HttpHeaders.LOCATION)) {
                redirectUrl = location.getValue();
            }

            httpClient.execute(new HttpGet(redirectUrl));

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
            return result;
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            LOG.error(MessageFormat.format("VK authentication fails with reason: {0}", e.getMessage()));
            throw new IllegalStateException("System error", e);
        } finally {
            closeQuietly(sslHttpClient);
            closeQuietly(httpClient);
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new IllegalStateException("System error", e);
            }
        }
    }

    private String getAuthUrl(CloseableHttpClient httpClient) throws ParseException, IOException {
        final String LOGIN_FORM_ID = "quick_login_form";
        final String IP_HASH_KEY = "ip_h";
        final String LG_HASH_KEY = "lg_h";

        HttpGet request = new HttpGet(_vkDomain);
        request.setHeader(HttpHeaders.USER_AGENT, _userAgent);

        String loginPage = null;
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            loginPage = EntityUtils.toString(response.getEntity());
        }

        loginPage = HtmlUtils.sanitizeHtml(loginPage);
        Document doc = Jsoup.parse(loginPage);
        Element loginForm = doc.getElementById(LOGIN_FORM_ID);

        String action = loginForm.attr("action");
        String ipHash = loginForm.getElementsByAttributeValue("name", IP_HASH_KEY).get(0).attr("value");
        String lgHash = loginForm.getElementsByAttributeValue("name", LG_HASH_KEY).get(0).attr("value");

        StringBuilder authUrl = new StringBuilder(action);
        authUrl.append("&").append(MessageFormat.format(_authLoginParamsPattern, _login, _pass));
        if (StringUtils.isNotEmpty(ipHash)) {
            authUrl.append("&").append(IP_HASH_KEY).append("=").append(ipHash);
        }
        if (StringUtils.isNotEmpty(lgHash)) {
            authUrl.append("&").append(LG_HASH_KEY).append("=").append(lgHash);
        }

        LOG.debug(String.format("AuthURL: %s", authUrl));
        return authUrl.toString();
    }

    public void setAuthCookieDomain(String authCookieDomain) {
        _authCookieDomain = authCookieDomain;
    }

    public void setAuthLoginParamsPattern(String authLoginParamsPattern) {
        _authLoginParamsPattern = authLoginParamsPattern;
    }

    public void setContentType(String contentType) {
        _contentType = contentType;
    }

    public void setUserAgent(String userAgent) {
        _userAgent = userAgent;
    }

    public void setVkDomain(String vkDomain) {
        _vkDomain = vkDomain;
    }

    public void setLogin(String login) {
        _login = login;
    }

    public void setPass(String pass) {
        _pass = pass;
    }

}
