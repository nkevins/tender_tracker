package com.chlorocode.tendertracker.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TTLogger {

    private static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(TTLogger.class);
    }

    public static void info(String className, String log, Object... param) {
        logger.info(String.format("["+className+"] " + log, param));
    }

    public static void debug(String className, String log, Object... param) {
        logger.debug(String.format("["+className+"] " + log, param));
    }

    public static void warn(String className, String log, Object... param) {
        logger.warn(String.format("["+className+"] " + log, param));
    }

    public static void error(String className, String log, Object... param) {
        logger.error(String.format("["+className+"] " + log, className, log, param));
    }

    public static void trace(String className, String log, Object... param) {
        logger.trace(String.format("["+className+"] " + log, className, log, param));
    }

    public static void info(String className, String log, Throwable e) {
        logger.info("["+className+"] " + log, e);
    }

    public static void debug(String className, String log, Throwable e) {
        logger.debug("["+className+"] " + log, e);
    }

    public static void warn(String className, String log, Throwable e) {
        logger.warn("["+className+"] " + log, e);
    }

    public static void error(String className, String log, Throwable e) {
        logger.error("["+className+"] " + log, e);
    }

    public static void trace(String className, String log, Throwable e) {
        logger.trace("["+className+"] " + log, e);
    }
}
