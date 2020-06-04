package com.seaboxdata.auth.utlis;

import lombok.SneakyThrows;

import java.net.URI;

public class UrlUtils {

    @SneakyThrows
    public static String urlAddToken(String uri, String access_token, String refresh_token) {
        URI oldUri = new URI(uri);
        String newQuery = oldUri.getQuery();
        String addUrl = "access_token=" + access_token + "&refresh_token=" + refresh_token;
        if (newQuery == null) {
            newQuery = uri + "?" + addUrl;
        } else {
            newQuery = uri + "&" + addUrl;
        }
        return newQuery;
    }

}
