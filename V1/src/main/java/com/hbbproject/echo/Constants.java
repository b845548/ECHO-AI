package com.hbbproject.echo;

/**
 * Created by marco.granatiero on 03/10/2014.
 */
public final class Constants {

    // Defines a custom Intent action
    public static final String BROADCAST_ACTION_BRAIN_STATUS =
            "com.hbbproject.echo.BROADCAST_ACTION_BRAIN_STATUS";
    public static final String BROADCAST_ACTION_BRAIN_ANSWER =
            "com.hbbproject.echo.BROADCAST_ACTION_BRAIN_ANSWER";
    public static final String BROADCAST_ACTION_LOGGER =
            "com.hbbproject.echo.BROADCAST_ACTION_LOGGER";

    // Defines the key for the status "extra" in an Intent
    public static final String EXTRA_BRAIN_STATUS =
            "com.hbbproject.echo.BRAIN_STATUS";
    public static final String EXTRA_BRAIN_ANSWER =
            "com.hbbproject.echo.EXTRA_BRAIN_ANSWER";

    public static final int STATUS_BRAIN_LOADING = -1;
    public static final int STATUS_BRAIN_LOADED = 1;

    public static final String EXTENDED_LOGGER_INFO =
            "com.hbbproject.echo.LOGGER_INFO";

}
