package com.euxcet.thupat.db.proxy;

import com.euxcet.thupat.db.dao.ExampleDao;
import com.euxcet.thupat.model.ExampleModel;
import com.euxcet.thupat.event.ErrorCodes;
import com.euxcet.thupat.event.EventConst;
import com.google.gson.Gson;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleProxy extends AbstractProxy {
    private static Logger logger = LoggerFactory.getLogger(ExampleProxy.class.getName());

    private Vertx vertx;
    private JDBCClient client;
    private ExampleDao dao;
    private static Gson gson = new Gson();

    public ExampleProxy(Vertx vertx, JDBCClient client) {
        this.vertx = vertx;
        this.client = client;
        this.dao = new ExampleDao(client);
    }

    @Override
    public void proc(Message<JsonObject> msg) {
        String action = msg.headers().get(ACTION);

        switch (action) {
            case EventConst.THUPAT_DB.REQ.ACTIONS.ADD_ONE -> addOne(msg);
            case EventConst.THUPAT_DB.REQ.ACTIONS.DELETE_ONE -> deleteOne(msg);
            case EventConst.THUPAT_DB.REQ.ACTIONS.GET_ONE -> getOne(msg);
            case EventConst.THUPAT_DB.REQ.ACTIONS.GET_SERVICES -> getServices(msg);
            default -> msg.fail(ErrorCodes.INVALID_ACTION, "Invalid action (THUPatProxy): " + action);
        }
    }

    private void addOne(Message<JsonObject> msg) {
        JsonObject data = msg.body().getJsonObject(EventConst.THUPAT_DB.REQ.KEYS.DATA);
        ExampleModel model = new ExampleModel();
        model.setTime(data.getLong(EventConst.THUPAT_DB.REQ.KEYS.TIME));
        model.setLocation(data.getString(EventConst.THUPAT_DB.REQ.KEYS.LOCATION));
        dao.addOne(model, done -> {
            if (done.failed()) {
                onFailure(msg, done.cause(), logger);
            } else {
                JsonObject result = new JsonObject()
                        .put(EventConst.THUPAT_DB.REPLY.COMMON_KEYS.RESULT, gson.toJson(done.result()));
                msg.reply(result);
            }
        });
    }

    private void deleteOne(Message<JsonObject> msg) {
        JsonObject data = msg.body().getJsonObject(EventConst.THUPAT_DB.REQ.KEYS.DATA);
        int id = data.getInteger(EventConst.THUPAT_DB.REQ.KEYS.ID);

        Promise<ExampleModel> promise = Promise.promise();
        dao.getOne(id, promise);
        promise.future().compose(v -> {
            Promise<Integer> subPromise = Promise.promise();
            dao.deleteOne(id, subPromise);
            return subPromise.future();
        }).onComplete(res -> {
            Future<Integer> done = (Future<Integer>) res;
            if (done.failed()) {
                onFailure(msg, done.cause(), logger);
            } else {
                int deleted = done.result();
                JsonObject obj = new JsonObject()
                        .put(EventConst.THUPAT_DB.REPLY.COMMON_KEYS.RESULT, deleted);
                msg.reply(obj);
            }
        });
    }

    private void getOne(Message<JsonObject> msg) {
        JsonObject data = msg.body().getJsonObject(EventConst.THUPAT_DB.REQ.KEYS.DATA);
        int id = data.getInteger(EventConst.THUPAT_DB.REQ.KEYS.ID);

        dao.getOne(id, handler -> {
            if (handler.failed()) {
                onFailure(msg, handler.cause(), logger);
            }
            else {
                JsonObject obj = new JsonObject()
                        .put(EventConst.THUPAT_DB.REPLY.COMMON_KEYS.RESULT, handler.result() != null ? gson.toJson(handler.result()) : null);
                msg.reply(obj);
            }
        });
    }

    private void getServices(Message<JsonObject> msg) {
        JsonObject data = msg.body().getJsonObject(EventConst.THUPAT_DB.REQ.KEYS.DATA);
//        int id = data.getInteger(EventConst.THUPAT_DB.REQ.KEYS.ID);
        int id = data.getInteger(EventConst.THUPAT_DB.REQ.KEYS.TIME);

        dao.getServices(id, handler -> {
            if (handler.failed()) {
                onFailure(msg, handler.cause(), logger);
            }
            else {
                JsonObject obj = new JsonObject()
                        .put(EventConst.THUPAT_DB.REPLY.COMMON_KEYS.RESULT, handler.result() != null ? gson.toJson(handler.result()) : null);
                msg.reply(obj);
            }
        });
    }
}
