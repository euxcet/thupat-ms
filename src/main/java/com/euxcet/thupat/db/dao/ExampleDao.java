package com.euxcet.thupat.db.dao;

import com.euxcet.thupat.model.ExampleModel;
import com.euxcet.thupat.model.ServiceModel;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExampleDao extends AbstractDao {
    private static Logger logger = LoggerFactory.getLogger(ExampleDao.class.getName());
    private final String INSERT_SQL = "INSERT INTO example_table (time, location) VALUES(?, ?)";
    private final String QUERY_BY_ID_SQL = "SELECT * FROM example_table WHERE id = ?";
    private final String DELETE_ONE_SQL = "DELETE FROM example_table WHERE id = ?";

    private final String QUERY_SERVICE_BY_TYPE_SQL = "SELECT * FROM service_table WHERE type = ?";

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

    private List<ServiceModel> parseService(List<JsonArray> data) {
        if (data.isEmpty()) {
            return null;
        }
        List<ServiceModel> serviceList = new ArrayList<ServiceModel>();
        logger.info("length: " + data.size());
        for (int i = 0; i < data.size(); i++) {
            ServiceModel model = new ServiceModel();
            JsonArray service = data.get(i);
            model.setId(service.getInteger(0));
            model.setName(service.getString(1));
            model.setType(service.getString(2));
            serviceList.add(model);
        }
        return serviceList;
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

    public void getServices(String type, Handler<AsyncResult<List<ServiceModel>>> done) {
        JsonArray para = new JsonArray();
        para.add(type);

        commonGetOnes(QUERY_SERVICE_BY_TYPE_SQL, para, result -> {
            if (result.failed())
                done.handle(Future.failedFuture(this.getClass().getName() + ": " + result.cause()));
            else {
                List<JsonArray> serviceList = result.result();
                done.handle(Future.succeededFuture(parseService(serviceList)));
            }
        });
    }
}
