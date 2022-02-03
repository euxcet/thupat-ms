package com.euxcet.db;

import com.euxcet.db.proxy.AbstractProxy;
import com.euxcet.db.proxy.ExampleProxy;
import com.euxcet.thupat.config.SysConfigPara;
import com.euxcet.thupat.event.ErrorCodes;
import com.euxcet.thupat.event.EventConst;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(DatabaseVerticle.class.getName());

    private Gson gson = new Gson();

    private ExampleProxy proxy;
    private JDBCClient client;
    private MessageConsumer<JsonObject> consumer;

    public Future<Void> startVerticle() {
        Promise<Void> promise = Promise.promise();

        SysConfigPara.DatabaseVerticlePara para = gson.fromJson(
                config().getString(SysConfigPara.VerticleParaKey.Verticle.DB),
                SysConfigPara.DatabaseVerticlePara.class
        );
        client = JDBCClient.createShared(vertx, new JsonObject(para.database_para));

        proxy = new ExampleProxy(vertx, client);
        consumer = vertx.eventBus().consumer(EventConst.THUPAT_DB.ID, this::onMsg);

        promise.complete();
        return promise.future();
    }

    private void onMsg(Message<JsonObject> message) {
        if (!message.headers().contains(AbstractProxy.ACTION)) {
            message.fail(ErrorCodes.NO_ACTION_SPECIFIED, "No action header specified");
            return;
        }
        proxy.proc(message);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        startVerticle().onComplete(startPromise);
    }

    @Override
    public void stop() {
        consumer.unregister();
        client.close();
    }
}
