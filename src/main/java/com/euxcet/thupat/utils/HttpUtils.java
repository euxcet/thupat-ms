package com.euxcet.thupat.utils;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

public class HttpUtils {

    public static void setHttpHeader(HttpServerResponse response) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
    }

    public static void dumpRestApi(Router router, String rootPath, Logger logger) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        List<Route> routes = router.getRoutes();
        for (Route route : routes) {
            Set<HttpMethod> sets = route.methods();
            for (HttpMethod method : sets)
                sb.append(method.name()).append(" ");

            sb.append(": " + rootPath + route.getPath());
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length()-1);
        logger.info(sb.toString().replaceAll("//", "/"));
    }
}
