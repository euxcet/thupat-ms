package com.euxcet.thupat;

import com.euxcet.thupat.config.SysConfigPara;
import com.futureinteraction.bas.event.EventConst;
import com.google.gson.Gson;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.TcpIpConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Entry {
    private static Logger logger = LoggerFactory.getLogger(Entry.class.getName());

    private static Vertx CLUSTER_VERTX;
    //key, id; value, name
    private static Map<String, String> verticles = new HashMap<>();

    private static SysConfigPara config;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        Verinfo.dumpVersion();

        Options options = new Options();
        Option urlInput = new Option("c", "config", true, "config file path");
        urlInput.setRequired(true);
        options.addOption(urlInput);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        try {
            String path = cmd.getOptionValue("c");
            config = SysConfigPara.load(path);

            int core = Runtime.getRuntime().availableProcessors();
            int ratio = 8;
            logger.info("worker pool size: " + core * ratio);

            String local = InetAddress.getLocalHost().getHostAddress();
            logger.info("localhost {}", local);

            EventBusOptions eventBusOptions = new EventBusOptions();
            eventBusOptions.setHost(local);

            Config hazelcastConfig = new Config();

            if (config.cluster != null) {
                JoinConfig joinConfig = new JoinConfig();
                MulticastConfig mc = new MulticastConfig();
                mc.setEnabled(false);
                joinConfig.setMulticastConfig(mc);

                if (config.cluster.members != null) {
                    TcpIpConfig tcpIpConfig = new TcpIpConfig();
                    tcpIpConfig.setEnabled(true);
                    tcpIpConfig.setMembers(config.cluster.members);
                    joinConfig.setTcpIpConfig(tcpIpConfig);
                }

                hazelcastConfig.getNetworkConfig().setJoin(joinConfig);

                if (config.cluster.host != null) {
                    hazelcastConfig.getNetworkConfig().setPublicAddress(config.cluster.host);
                    hazelcastConfig.getNetworkConfig().setPort(config.cluster.port);

                    eventBusOptions.setClusterPublicHost(config.cluster.host);
                    eventBusOptions.setClusterPublicPort(config.cluster.port);
                }
                else {
                    eventBusOptions.setClusterPublicHost(local);
                    hazelcastConfig.getNetworkConfig().setPublicAddress(local);
                }
            }

            HazelcastClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);

            VertxOptions vertxOptions = new VertxOptions();
            vertxOptions.setHAEnabled(true)
                    .setClusterManager(mgr)
                    .setEventBusOptions(eventBusOptions)
                    .setWorkerPoolSize(core * ratio);


            Vertx.clusteredVertx(vertxOptions, handler -> {
                if (handler.failed()) {
                    logger.error("failed to init clustered vertx: {}", handler.cause().getMessage());
                } else {
                    int workerPoolSize = core * ratio;
                    logger.info("worker pool size: {}", workerPoolSize);

                    CLUSTER_VERTX = handler.result();

                    // THUPAT
                    DeploymentOptions deploymentOptions = new DeploymentOptions()
                            .setInstances(config.thupat_verticle.instance)
                            .setWorker(true)
                            .setWorkerPoolSize(vertxOptions.getWorkerPoolSize());

                    CLUSTER_VERTX.deployVerticle(THUPatVerticle.class, deploymentOptions, h -> {
                        if (h.failed())
                            logger.error("failed to deploy push proxy vertical: " + h.cause());
                        else {
                            logger.info("succeeded to deploy push proxy vertical: " + h.result());
                            verticles.put(h.result(), "thupat vertical");
                        }
                    });


                    // REST
                    JsonObject restVerticlePara = new JsonObject();
                    restVerticlePara.put("port", config.rest_verticle.port);
                    if (config.rest_verticle.host != null)
                        restVerticlePara.put("host", config.rest_verticle.host);

                    DeploymentOptions restVerticleOptions = new DeploymentOptions()
                            .setConfig(restVerticlePara)
                            .setInstances(config.rest_verticle.instance)
                            .setWorkerPoolSize(config.rest_verticle.worker_pool_size == 0 ? workerPoolSize : config.rest_verticle.worker_pool_size);

                    CLUSTER_VERTX.deployVerticle(RestVerticle.class, restVerticleOptions, h -> {
                        if (h.failed())
                            logger.error("failed to deploy rest server verticle: {}", h.cause().getMessage());
                        else {
                            logger.info("succeeded to deploy rest server verticle: {}", h.result());
                            verticles.put(h.result(), "rest server verticle");
                        }
                    });
                }
            });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("to shutdown " + verticles.size() + " verticles ...");
                CountDownLatch latch = new CountDownLatch(verticles.size());

                if (CLUSTER_VERTX != null) {
                    Set<String> verticleIdSet = verticles.keySet();

                    for (String verticleId : verticleIdSet) {
                        CLUSTER_VERTX.undeploy(verticleId, h -> {
                            String name = verticles.get(verticleId);
                            if (h.failed()) {
                                logger.error("failed to undeploy {}", name);
                            } else {
                                logger.info("succeeded to undeploy {}", name);
                            }

                            latch.countDown();
                        });
                    }
                }
                try {
                    latch.await(5, TimeUnit.SECONDS);
                    System.out.println("shut down ...");
                } catch (Exception ignored) {
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("failed to load the configuration: {}", e.getMessage());
            System.exit(1);
        }
    }
}
