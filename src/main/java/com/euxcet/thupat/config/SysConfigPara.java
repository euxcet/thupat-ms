package com.euxcet.thupat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SysConfigPara {
    private static Logger logger = LoggerFactory.getLogger(SysConfigPara.class.getName());

    public interface VerticleParaKey {
        interface Verticle {
            String DB = "db";
            String CANAL = "canal";
        }
    }

    public static class RestVerticlePara {
        public String host;
        public int port;
        public int instance;
        public int worker_pool_size;
    }

    public static class DatabaseVerticlePara {
        public String database_para;
        public int instance;
        public int worker_pool_size;
    }

    public static class THUPatVerticlePara {
        public int instance;
    }

    public static SysConfigPara load(String path) throws FileNotFoundException {
        try {
            logger.debug("config:\n {}", new String(new FileInputStream(path).readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Yaml yaml = new Yaml();
        InputStream in = new FileInputStream(path);
        SysConfigPara config = yaml.loadAs(in, SysConfigPara.class);
        return config;
    }

    public static class ClusterPara {
        public String host;
        public int port;
        public List<String> members;
    }

    public RestVerticlePara rest_verticle;

    public DatabaseVerticlePara database_verticle;

    public ClusterPara cluster;

    public THUPatVerticlePara thupat_verticle;
}
