package com.euxcet.db.dao;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

public abstract class AbstractDao {
    protected JDBCClient client;

    public AbstractDao(JDBCClient client) {
        this.client = client;
    }

    protected void updateWithFuture(SQLConnection conn, String sql, JsonArray para, Handler<AsyncResult<UpdateResult>> done) {
        conn.updateWithParams(sql, para, res -> {
            if (res.failed())
                done.handle(Future.failedFuture(res.cause()));
            else
                done.handle(Future.succeededFuture(res.result()));
        });
    }

    protected void queryWithFuture(SQLConnection conn, String sql, Handler<AsyncResult<ResultSet>> done) {
        conn.query(sql, res -> {
            if (res.failed())
                done.handle(Future.failedFuture(res.cause()));
            else
                done.handle(Future.succeededFuture(res.result()));
        });
    }

    protected void queryWithFuture(SQLConnection conn, String sql, JsonArray para, Handler<AsyncResult<ResultSet>> done) {
        conn.queryWithParams(sql, para, res -> {
            if (res.failed())
                done.handle(Future.failedFuture(res.cause()));
            else {
                done.handle(Future.succeededFuture(res.result()));
            }
        });
    }

    protected void commonAddOne(SQLConnection conn, int insertedId, String insertSql, JsonArray para, String queryByIdSql, Handler<AsyncResult<JsonArray>> done) {
        Promise<UpdateResult> promise = Promise.promise();
        updateWithFuture(conn, insertSql, para, promise);

        promise.future().compose(v -> {
            Promise<ResultSet> subPromise = Promise.promise();
            long id = v.getKeys().size() != 0 ? v.getKeys().getLong(0) : insertedId;
            queryWithFuture(conn, queryByIdSql, new JsonArray().add(id), subPromise);
            return subPromise.future();
        }).onComplete(v -> {
            Future<ResultSet> future = (Future<ResultSet>) v;
            if (future.failed()) {
                done.handle(Future.failedFuture(future.cause()));
            } else {
                JsonArray jsonArray = (future.result()).getResults().get(0);
                done.handle(Future.succeededFuture(jsonArray));
            }
        });
    }

    protected void commonAddOne(String insertSql, JsonArray para, String queryByIdSql, Handler<AsyncResult<JsonArray>> done) {
        client.getConnection(conn -> {
            if (conn.failed()) {
                done.handle(Future.failedFuture(conn.cause()));
            } else {
                commonAddOne(conn.result(), 0, insertSql, para, queryByIdSql, addDone -> {
                    if (addDone.failed())
                        done.handle(Future.failedFuture(addDone.cause()));
                    else
                        done.handle(Future.succeededFuture(addDone.result()));

                    //close the connection
                    conn.result().close(connDone -> {
                        if (connDone.failed()) {
                            throw new RuntimeException(connDone.cause());
                        }
                    });
                });
            }
        });
    }

}
