package com.seaboxdata.auth.api;

public class FeignClientConfig {
    public static final String NAME = "AUTH-SERVER";
    public static final String URL = "${seaboxdata.auth.hostname}";
}
