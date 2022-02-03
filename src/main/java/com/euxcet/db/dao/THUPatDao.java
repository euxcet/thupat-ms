package com.euxcet.db.dao;

import com.euxcet.model.ExampleModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

public class THUPatDao extends AbstractDao {
    private final String INSERT_SQL = "INSERT INTO thupat (time, location) VALUES(?, ?)";
    private final String QUERY_BY_ID_SQL = "SELECT * FROM thupat WHERE id = ?";

    public THUPatDao(JDBCClient client) {
        super(client);
    }

    public void addOne(ExampleModel model, Handler<AsyncResult<ExampleModel>> done) {
        JsonArray para = new JsonArray();
        para.add(model.getTime());
        para.add(model.getLocation());
        commonAddOne(INSERT_SQL, para, QUERY_BY_ID_SQL, result -> {
            if (result.failed()) {
                done.handle(Future.failedFuture(this.getClass().getName() + ": " + result.cause()));
            } else {
                JsonArray jsonArray = result.result();
                done.handle(Future.succeededFuture(parse(jsonArray)));
            }
        });
    }

    private ExampleModel parse(JsonArray data) {
        if (data == null) {
            return null;
        }
        ExampleModel model = new ExampleModel();
        model.setId(data.getInteger(0));
        model.setTime(data.getLong(1));
        model.setLocation(data.getString(2));
        return model;
    }
}
