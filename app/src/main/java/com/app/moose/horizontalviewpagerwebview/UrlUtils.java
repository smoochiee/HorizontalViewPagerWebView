package com.app.moose.horizontalviewpagerwebview;

/**
 * Created by nOn3c on 12/6/2017.
 */

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
    public static String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }
}