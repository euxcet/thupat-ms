package com.euxcet.thupat.resources;

import com.euxcet.thupat.event.EventConst;
import com.euxcet.thupat.utils.HttpUtils;
import com.euxcet.thupat.utils.StringUtils;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class THUPatResource {

    private static Logger logger = LoggerFactory.getLogger(THUPatResource.class.getName());

    private static String VER = "0.1";
    private static String PATH = "/thupat/" + VER;

    public void register(Router mainRouter, Router router) {
        router.get("/ping").handler(this::ping);
        router.get("/ping-json").handler(this::ping_json);

        mainRouter.mountSubRouter(PATH, router);
        HttpUtils.dumpRestApi(router, PATH, logger);
    }

    protected void ping(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);
        response.end("pong!");
    }

    protected void ping_json(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);
        JsonObject res = new JsonObject();
        res.put("msg", "pong!");
        response.end(res.encode());
    }
}
