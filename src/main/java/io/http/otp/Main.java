package io.http.otp;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by works on 2014/11/18.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String url = "https://selfsolve.apple.com/wcResults.do";

        Map<String, String> data = new HashMap<>();
        data.put("github_url", "https://gist.github.com/");
        data.put("email", "");
        Gson gson = new Gson();
        String postContent = gson.toJson(data);


        HttpPost post = new HttpPost(url);
        HttpHost target = URIUtils.extractHost(post.getURI());

        // add header
        StringEntity requestEntity = new StringEntity(postContent, ContentType.APPLICATION_JSON);
        post.setEntity(requestEntity);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials("username", "password"));

//        HttpClient client = HttpClients.createDefault();
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();

        try {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            CloseableHttpResponse response = client.execute(target, post, localContext);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                System.out.println(result.toString());
            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
    }
}
