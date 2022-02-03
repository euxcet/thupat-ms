package com.euxcet.thupat;

import com.euxcet.thupat.event.ErrorCodes;
import com.euxcet.thupat.event.EventConst;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.euxcet.thupat.event.EventConst.HEADERS.ACTION;

public class THUPatVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(THUPatVerticle.class.getName());

    private MessageConsumer<JsonObject> msgConsumer;

    @Override
    public void start(Promise<Void> future) {
        msgConsumer = vertx.eventBus().consumer(EventConst.THUPAT_WEB.ID, this::onTHUPatMsg);
        future.complete();
    }

    @Override
    public void stop() {
        msgConsumer.unregister();
    }

    private void onTHUPatMsg(io.vertx.core.eventbus.Message<JsonObject> msg) {
        String action = msg.headers().get(ACTION);
        JsonObject body = msg.body();

        String str = body.getString(EventConst.THUPAT_WEB.REQ.KEYS.STR);

        switch (action) {
            case EventConst.THUPAT_WEB.REQ.ACTIONS.REVERSE:
                JsonObject res = reverseString(str);
                msg.reply(res);
                break;
            default:
                logger.error("invalid msg type: {}", action);
                msg.fail(ErrorCodes.MSG_TYPE_ERROR, "invalid msg type");
        }
    }

    private JsonObject reverseString(String str) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("result", new StringBuffer(str).reverse().toString());
        return jsonObject;
    }

}
