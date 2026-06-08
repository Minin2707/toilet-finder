package com.toiletfinder.toilet_finder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(
        OsmImportProperties.class
)
@ConfigurationProperties(prefix = "osm.import")
public class OsmImportProperties {

    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}