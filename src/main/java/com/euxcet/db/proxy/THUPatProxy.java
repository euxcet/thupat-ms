package com.euxcet.db.proxy;

import com.euxcet.db.dao.THUPatDao;
import com.euxcet.model.ExampleModel;
import com.euxcet.thupat.event.ErrorCodes;
import com.euxcet.thupat.event.EventConst;
import com.google.gson.Gson;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class THUPatProxy extends AbstractProxy {
    private static Logger logger = LoggerFactory.getLogger(THUPatProxy.class.getName());

    private Vertx vertx;
    private JDBCClient client;
    private THUPatDao dao;
    private static Gson gson = new Gson();

    public THUPatProxy(Vertx vertx, JDBCClient client) {
        this.vertx = vertx;
        this.client = client;
    }

    @Override
    public void proc(Message<JsonObject> msg) {
        String action = msg.headers().get(ACTION);

        switch (action) {
            case EventConst.THUPAT_DB.REQ.ACTIONS.ADD_ONE -> addOne(msg);
            case EventConst.THUPAT_DB.REQ.ACTIONS.GET_ONE -> getOne(msg);
            case EventConst.THUPAT_DB.REQ.ACTIONS.DELETE_ONE -> deleteOne(msg);
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

    private void getOne(Message<JsonObject> msg) {

    }

    private void deleteOne(Message<JsonObject> msg) {

    }
}
