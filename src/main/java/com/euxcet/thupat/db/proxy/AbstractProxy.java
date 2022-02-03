package com.euxcet.thupat.db.proxy;

import com.euxcet.thupat.event.ErrorCodes;
import io.vertx.core.json.JsonObject;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;

public abstract class AbstractProxy {
    public static String ACTION = "action";

    public abstract void proc(Message<JsonObject> msg);

    protected void onFailure(Message<JsonObject> msg, Throwable cause, Logger logger) {
        msg.fail(ErrorCodes.DB_ERROR, cause.getMessage());
        logger.error(cause.getMessage());
    }

    protected void onFailure(Message<JsonObject> msg, int error_code, String message, Logger logger) {
        msg.fail(error_code, message);
        logger.error(msg.body().encodePrettily());
    }
}
