package com.appdynamics.monitors.cacti;


import com.appdynamics.extensions.PathResolver;
import com.appdynamics.extensions.util.metrics.Metric;
import com.appdynamics.extensions.util.metrics.MetricFactory;
import com.appdynamics.extensions.yml.YmlReader;
import com.appdynamics.monitors.cacti.config.Configuration;
import com.appdynamics.monitors.cacti.config.RRD;
import com.google.common.base.Strings;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CactiMonitor extends AManagedMonitor {

    private static final Logger logger = Logger.getLogger(CactiMonitor.class);

    public static final String METRIC_SEPARATOR = "|";
    private static final String CONFIG_ARG = "config-file";
    private static final String FILE_NAME = "monitors/CactiMonitor/config.yml";


    public CactiMonitor() {
        String details = CactiMonitor.class.getPackage().getImplementationTitle();
        String msg = "Using Monitor Version [" + details + "]";
        logger.info(msg);
        System.out.println(msg);
    }

    public TaskOutput execute(Map<String, String> taskArgs, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        if (taskArgs != null) {
            logger.info("Starting the Cacti Monitoring task.");
            String configFilename = getConfigFilename(taskArgs.get(CONFIG_ARG));
            try {
                Configuration config = YmlReader.readFromFile(configFilename, Configuration.class);
                Map<String, Integer> metrics = populateStats(config);
                //metric overrides
                MetricFactory<Integer> metricFactory = new MetricFactory<Integer>(config.getMetricOverrides());
                List<Metric> allMetrics = metricFactory.process(metrics);
                printStats(config, allMetrics);
                logger.info("Completed the Cacti Monitoring Task successfully");
                return new TaskOutput("Cacti Monitor executed successfully");
            } catch (Exception e) {
                logger.error("Metrics Collection Failed: ", e);
            }
        }
        throw new TaskExecutionException("Cacti Monitor completed with failures");
    }

    private void printStats(Configuration config, List<Metric> metrics) {
        String metricPathPrefix = config.getMetricPathPrefix();
        for (Metric aMetric : metrics) {
            printMetric(metricPathPrefix + aMetric.getMetricPath(), aMetric.getMetricValue().toString(), aMetric.getAggregator(), aMetric.getTimeRollup(), aMetric.getClusterRollup());
        }
    }

    private void printMetric(String metricName, String metricValue, String aggType, String timeRollupType, String clusterRollupType) {
        MetricWriter metricWriter = getMetricWriter(metricName,
                aggType,
                timeRollupType,
                clusterRollupType
        );
        //System.out.println("Sending [" + aggType + METRIC_SEPARATOR + timeRollupType + METRIC_SEPARATOR + clusterRollupType
        //        + "] metric = " + metricName + " = " + metricValue);
        if (logger.isDebugEnabled()) {
            logger.debug("Sending [" + aggType + METRIC_SEPARATOR + timeRollupType + METRIC_SEPARATOR + clusterRollupType
                    + "] metric = " + metricName + " = " + metricValue);
        }
        metricWriter.printMetric(metricValue);
    }

    private Map<String, Integer> populateStats(Configuration config) throws TaskExecutionException {
        RRD rrd = config.getRrd();
        String rraPath = rrd.getRraPath();
        List<String> rrdFiles = rrd.getRrdFiles();
        Map<String, Integer> stats = new HashMap<String, Integer>();
        for(String rrdFile : rrdFiles) {
            RRDToolFetchExecutor rrdToolFetchExecutor = new RRDToolFetchExecutor();
            stats.putAll(rrdToolFetchExecutor.execute(rraPath, rrdFile));
        }
        return stats;
    }


    private String getConfigFilename(String filename) {
        if (filename == null) {
            return "";
        }

        if ("".equals(filename)) {
            filename = FILE_NAME;
        }
        // for absolute paths
        if (new File(filename).exists()) {
            return filename;
        }
        // for relative paths
        File jarPath = PathResolver.resolveDirectory(AManagedMonitor.class);
        String configFileName = "";
        if (!Strings.isNullOrEmpty(filename)) {
            configFileName = jarPath + File.separator + filename;
        }
        return configFileName;
    }
}