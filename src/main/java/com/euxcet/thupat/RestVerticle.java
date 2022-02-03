package com.euxcet.thupat;

import com.euxcet.thupat.resources.THUPatResource;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(RestVerticle.class.getName());
    private HttpServer server;
    private Gson gson = new Gson();

    /**
     * This method constructs the router factory, mounts services and handlers and starts the http server with built router
     *
     * @return
     */
    private Future<Void> startHttpServer() {
        // Generate the router
        Router router = Router.router(vertx);
        enableCorsSupport(router);

        router.route().handler(BodyHandler.create());
        router.route().handler(StaticHandler.create());

        regRestApi(router);

        JsonObject config = config();
        int port = config.getInteger("port");
        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions
                .setPort(port)
                .setCompressionSupported(true);

        logger.info("start http server at port: {}", port);

        server = vertx.createHttpServer(serverOptions).requestHandler(router);
        Promise<Void> promise = Promise.promise();
        server.listen(handler -> {
            if (handler.failed())
                promise.fail(handler.cause());
            else
                promise.complete();
        });
        return promise.future();
    }

    private void enableCorsSupport(Router router) {
        CorsHandler corsHandler = CorsHandler.create(".*");
        corsHandler.allowedMethod(HttpMethod.GET);
        corsHandler.allowedMethod(HttpMethod.POST);
        corsHandler.allowedMethod(HttpMethod.PUT);
        corsHandler.allowedMethod(HttpMethod.DELETE);
        corsHandler.allowedMethod(HttpMethod.OPTIONS);
        corsHandler.allowedHeader(HttpHeaders.AUTHORIZATION);
        corsHandler.allowedHeader(HttpHeaders.CONTENT_TYPE);
        corsHandler.allowedHeader("Origin");
        corsHandler.allowedHeader("Access-Control-Allow-Origin");
        corsHandler.allowedHeader("Access-Control-Allow-Headers");
        corsHandler.allowedHeader("Access-Control-Allow-Method");
        corsHandler.allowedHeader("Access-Control-Allow-Credentials");
        corsHandler.allowedHeader("Access-Control-Expose-Headers");;

        corsHandler.allowCredentials(true);

        router.route().handler(corsHandler);
    }

    private void regRestApi(Router mainRouter) {
        Router router = Router.router(vertx);
        new THUPatResource().register(mainRouter, router);
    }

    @Override
    public void start(Promise<Void> promise) {
        startHttpServer().onComplete(promise);
    }

    /**
     * This method closes the http server and unregister all services loaded to Event Bus
     */
    @Override
    public void stop() {
        server.close();
        System.out.println("stop rest server verticle");
    }
}
