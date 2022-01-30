package com.euxcet.thupat;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Verinfo {
    private static Logger logger = LoggerFactory.getLogger(Verinfo.class.getName());
    /**
     * 获取版本信息
     * @return
     */
    public static Pair<String, String> getAppVersion() {
        Properties properties = new Properties();
        try {
            properties.load(Verinfo.class.getClassLoader().getResourceAsStream("app.properties"));
            if (!properties.isEmpty()) {
                String ver = properties.getProperty("version");
                String buildTime = properties.getProperty("build.time");

                return Pair.of(ver, buildTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void dumpVersion() {
        try {
            InputStream is = Verinfo.class.getClassLoader().getResourceAsStream("banner.txt");
            byte[] bytes = is.readAllBytes();
            logger.info(new String(bytes));
        }
        catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        //read version
        Pair<String, String> ver = Verinfo.getAppVersion();
        logger.info("VER: {}; BUILD TIME: {}", ver.getLeft(), ver.getRight());
    }
}
