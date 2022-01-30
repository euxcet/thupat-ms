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
//        router.post("/").handler(this::sendSms);
//        router.get("/reply").handler(this::getSmsReply);

        router.get("/").handler(context -> {
            HttpServerResponse response = context.response();
            HttpUtils.setHttpHeader(response);
            response.end("hello!");
        });

        mainRouter.mountSubRouter(PATH, router);
        HttpUtils.dumpRestApi(router, PATH, logger);
    }

    /*
    protected void sendSms(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        JsonObject data = context.getBodyAsJson();
        String phone = data.getString("phone");
        String signId = data.getString("sign_id");
        String templateId = data.getString("template_id");
        String checkNo = data.getString("check_no");

        JsonObject para = new JsonObject()
                .put(EventConst.SMS.REQ.KEYS.PHONE, phone)
                .put(EventConst.SMS.REQ.KEYS.SIGN_ID, signId)
                .put(EventConst.SMS.REQ.KEYS.TEMPLATE_ID, templateId)
                .put(EventConst.SMS.REQ.KEYS.PARA, new JsonArray().add(checkNo));

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.SMS.REQ.ACTIONS.SEND_SMS);

        context.vertx().eventBus().<JsonObject>request(EventConst.SMS.REQ.ID, para, deliveryOptions, handler -> {
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
    protected void getSmsReply(RoutingContext context) {
        HttpServerResponse response = context.response();
        HttpUtils.setHttpHeader(response);

        String phone = context.request().getParam("phone");
        if (StringUtils.isNullOrEmpty(phone)) {
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            JsonObject rt = new JsonObject();
            rt.put("msg", "phone cannot be null");
            response.end(rt.encodePrettily());
            return;
        }

        JsonObject para = new JsonObject()
                .put(EventConst.SMS.REQ.KEYS.PHONE, phone);

        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(DeliveryOptions.DEFAULT_TIMEOUT);
        deliveryOptions.addHeader(EventConst.HEADERS.ACTION, EventConst.SMS.REQ.ACTIONS.QUERY_REPLY);

        context.vertx().eventBus().<JsonObject>request(EventConst.SMS.REQ.ID, para, deliveryOptions, handler -> {
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
     */
}
