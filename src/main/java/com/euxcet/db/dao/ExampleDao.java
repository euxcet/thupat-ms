package com.euxcet.db.dao;

import com.euxcet.model.ExampleModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;

public class ExampleDao extends AbstractDao {
    private final String INSERT_SQL = "INSERT INTO example_table (time, location) VALUES(?, ?)";
    private final String QUERY_BY_ID_SQL = "SELECT * FROM example_table WHERE id = ?";
    private final String DELETE_ONE_SQL = "DELETE FROM example_table WHERE id = ?";

    public ExampleDao(JDBCClient client) {
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

    public void deleteOne(long id, Handler<AsyncResult<Integer>> done) {
        commonDelete(DELETE_ONE_SQL, id, done);
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

    public void getOne(long id, Handler<AsyncResult<ExampleModel>> done) {
        JsonArray para = new JsonArray();
        para.add(id);

        commonGetOne(QUERY_BY_ID_SQL, para, result -> {
            if (result.failed())
                done.handle(Future.failedFuture(this.getClass().getName() + ": " + result.cause()));
            else {
                JsonArray jsonArray = result.result();
                done.handle(Future.succeededFuture(parse(jsonArray)));
            }
        });
    }
}
