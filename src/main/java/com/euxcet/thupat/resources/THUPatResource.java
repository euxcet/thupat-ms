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
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class THUPatResource {

    private static Logger logger = LoggerFactory.getLogger(THUPatResource.class.getName());

    private static String VER = "0.1";
    private static String PATH = "/thupat/" + VER;

    public void register(Router mainRouter, Router router) {
        router.get("/ping").handler(this::ping);
        router.get("/ping-json").handler(this::ping_json);
        router.get("/reverse").handler(this::reverse);

        mainRouter.mountSubRouter(PATH, router);
        HttpUtils.dumpRestApi(router, PATH, logger);
    }

    protected void reverse(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        String str = context.request().getParam("str");
        if (StringUtils.isNullOrEmpty(str)) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            JsonObject rt = new JsonObject();
            rt.put("msg", "str cannot be null");
            response.end(rt.encodePrettily());
            return;
        }

        JsonObject para = new JsonObject()
                .put(EventConst.THUPAT.REQ.KEYS.STR, str);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.THUPAT.REQ.ACTIONS.REVERSE);

        context.vertx().eventBus().<JsonObject>request(EventConst.THUPAT.REQ.ID, para, deliveryOptions, handler -> {
            if (handler.failed()) {
                response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                JsonObject res = new JsonObject();
                res.put("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
                res.put("msg", handler.cause().getMessage());
                response.end(res.encodePrettily());
            }
            else {
                JsonObject rt = handler.result().body();
                response.setStatusCode(HttpStatus.SC_OK);
                response.end(rt.encodePrettily());
            }
        });
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
