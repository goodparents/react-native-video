package com.brentvatne.exoplayer;

import android.content.Context;
import android.content.ContextWrapper;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.network.CookieJarContainer;
import com.facebook.react.modules.network.ForwardingCookieHandler;
import com.facebook.react.modules.network.OkHttpClientProvider;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import okhttp3.Cookie;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

public class DataSourceUtil {

    private DataSourceUtil() {
    }

    private static Factory rawDataSourceFactory = null;
    private static Factory defaultDataSourceFactory = null;
    private static String userAgent = null;

    public static void setUserAgent(String userAgent) {
        DataSourceUtil.userAgent = userAgent;
    }

    public static String getUserAgent(ReactContext context) {
        if (userAgent == null) {
            userAgent = Util.getUserAgent(context, "ReactNativeVideo");
        }
        return userAgent;
    }

    public static Factory getRawDataSourceFactory(ReactContext context) {
        if (rawDataSourceFactory == null) {
            rawDataSourceFactory = buildRawDataSourceFactory(context);
        }
        return rawDataSourceFactory;
    }

    public static void setRawDataSourceFactory(Factory factory) {
        DataSourceUtil.rawDataSourceFactory = factory;
    }

    public static Factory getDefaultDataSourceFactory(ReactContext context, DefaultBandwidthMeter bandwidthMeter) {
        if (defaultDataSourceFactory == null) {
            defaultDataSourceFactory = buildDataSourceFactory(context, bandwidthMeter);
        }
        return defaultDataSourceFactory;
    }

    public static void setDefaultDataSourceFactory(Factory factory) {
        DataSourceUtil.defaultDataSourceFactory = factory;
    }

    private static Factory buildRawDataSourceFactory(ReactContext context) {
        return new RawResourceDataSourceFactory(context.getApplicationContext());
    }

    private static Factory buildDataSourceFactory(ReactContext context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(context, bandwidthMeter));
    }

    private static DataSource.Factory buildHttpDataSourceFactory(ReactContext context, DefaultBandwidthMeter bandwidthMeter) {
        OkHttpClient client = OkHttpClientProvider.getOkHttpClient();
        CookieJarContainer container = (CookieJarContainer) client.cookieJar();
        ForwardingCookieHandler handler = new ForwardingCookieHandler(context);
        container.setCookieJar(new JavaNetCookieJar(handler));
        return new OkHttpDataSourceFactory(client, getUserAgent(context), bandwidthMeter);
    }

}
