package com.appdynamics.monitors.cacti.config;


import java.util.List;

public class RRD {

    private String rraPath;
    private List<String> rrdFiles;

    public String getRraPath() {
        return rraPath;
    }

    public void setRraPath(String rraPath) {
        this.rraPath = rraPath;
    }

    public List<String> getRrdFiles() {
        return rrdFiles;
    }

    public void setRrdFiles(List<String> rrdFiles) {
        this.rrdFiles = rrdFiles;
    }
}
