package com.appdynamics.monitors.cacti.config;


import com.appdynamics.extensions.util.metrics.MetricOverride;

public class Configuration {

    private RRD rrd;
    private String metricPathPrefix;
    private MetricOverride[] metricOverrides;


    public RRD getRrd() {
        return rrd;
    }

    public void setRrd(RRD rrd) {
        this.rrd = rrd;
    }

    public String getMetricPathPrefix() {
        return metricPathPrefix;
    }

    public void setMetricPathPrefix(String metricPathPrefix) {
        this.metricPathPrefix = metricPathPrefix;
    }

    public MetricOverride[] getMetricOverrides() {
        return metricOverrides;
    }

    public void setMetricOverrides(MetricOverride[] metricOverrides) {
        this.metricOverrides = metricOverrides;
    }
}
