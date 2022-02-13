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
import io.vertx.ext.web.handler.BodyHandler;
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
        router.get("/reverse").handler(this::reverse);

        // database
        router.post("/add-one").consumes("*/json").handler(this::addOne);
        router.post("/delete-one").consumes("*/json").handler(this::deleteOne);
        router.get("/get-one").consumes("*/json").handler(this::getOne);

        router.post("/get-services").consumes("*/json").handler(this::getServices);

        mainRouter.mountSubRouter(PATH, router);
        HttpUtils.dumpRestApi(router, PATH, logger);
    }

    protected void addOne(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        JsonObject request = context.getBodyAsJson();
        if (request.getLong(EventConst.THUPAT_DB.REQ.KEYS.TIME) == null ||
            request.getString(EventConst.THUPAT_DB.REQ.KEYS.LOCATION) == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            JsonObject rt = new JsonObject();
            rt.put("msg", "parameters cannot be null");
            response.end(rt.encodePrettily());
            return;
        }

        JsonObject data = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.TIME, request.getLong(EventConst.THUPAT_DB.REQ.KEYS.TIME))
                .put(EventConst.THUPAT_DB.REQ.KEYS.LOCATION, request.getString(EventConst.THUPAT_DB.REQ.KEYS.LOCATION));

        JsonObject para = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.DATA, data);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.THUPAT_DB.REQ.ACTIONS.ADD_ONE);

        context.vertx().eventBus().<JsonObject>request(EventConst.THUPAT_DB.ID, para, deliveryOptions, handler -> {
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

    protected void deleteOne(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        JsonObject request = context.getBodyAsJson();
        if (request.getLong(EventConst.THUPAT_DB.REQ.KEYS.ID) == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            JsonObject rt = new JsonObject();
            rt.put("msg", "parameters cannot be null");
            response.end(rt.encodePrettily());
            return;
        }

        JsonObject data = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.ID, request.getLong(EventConst.THUPAT_DB.REQ.KEYS.ID));

        JsonObject para = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.DATA, data);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.THUPAT_DB.REQ.ACTIONS.DELETE_ONE);

        context.vertx().eventBus().<JsonObject>request(EventConst.THUPAT_DB.ID, para, deliveryOptions, handler -> {
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

    protected void getOne(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        JsonObject request = context.getBodyAsJson();
        if (request.getLong(EventConst.THUPAT_DB.REQ.KEYS.ID) == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            JsonObject rt = new JsonObject();
            rt.put("msg", "parameters cannot be null");
            response.end(rt.encodePrettily());
            return;
        }

        JsonObject data = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.ID, request.getLong(EventConst.THUPAT_DB.REQ.KEYS.ID));

        JsonObject para = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.DATA, data);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.THUPAT_DB.REQ.ACTIONS.GET_ONE);

        context.vertx().eventBus().<JsonObject>request(EventConst.THUPAT_DB.ID, para, deliveryOptions, handler -> {
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
                .put(EventConst.THUPAT_WEB.REQ.KEYS.STR, str);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.THUPAT_WEB.REQ.ACTIONS.REVERSE);

        context.vertx().eventBus().<JsonObject>request(EventConst.THUPAT_WEB.ID, para, deliveryOptions, handler -> {
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

    protected void getServices(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        JsonObject request = context.getBodyAsJson();
        if (request.getLong(EventConst.THUPAT_DB.REQ.KEYS.TIME) == null ||
                request.getString(EventConst.THUPAT_DB.REQ.KEYS.LOCATION) == null ||
                request.getJsonObject(EventConst.THUPAT_DB.REQ.KEYS.GEO_LOCATION) == null) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            JsonObject rt = new JsonObject();
            rt.put("msg", "parameters cannot be null");
            response.end(rt.encodePrettily());
            return;
        }

        JsonObject data = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.TIME, request.getLong(EventConst.THUPAT_DB.REQ.KEYS.TIME))
                .put(EventConst.THUPAT_DB.REQ.KEYS.LOCATION, request.getString(EventConst.THUPAT_DB.REQ.KEYS.LOCATION))
                .put(EventConst.THUPAT_DB.REQ.KEYS.GEO_LOCATION, request.getJsonObject(EventConst.THUPAT_DB.REQ.KEYS.GEO_LOCATION));

        JsonObject para = new JsonObject()
                .put(EventConst.THUPAT_DB.REQ.KEYS.DATA, data);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.THUPAT_DB.REQ.ACTIONS.GET_SERVICES);

        context.vertx().eventBus().<JsonObject>request(EventConst.THUPAT_DB.ID, para, deliveryOptions, handler -> {
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
