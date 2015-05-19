package com.appdynamics.monitors.cacti;

import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import net.stamfest.rrd.CommandResult;
import net.stamfest.rrd.RRDp;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RRDToolFetchExecutor {

    private static final Logger logger = Logger.getLogger(RRDToolFetchExecutor.class);
    private RRDp rrd = null;
    private static final String TMP_PATH = "/tmp";

    public RRDToolFetchExecutor() throws TaskExecutionException {

        try {
            rrd = new RRDp(TMP_PATH, null);
        } catch (IOException e) {
            logger.error("Not able to create RRDp");
            throw new TaskExecutionException("Not able to create RRDp");
        }
    }

    public Map<String, Integer> execute(String rrdPath, String rrdFile) throws TaskExecutionException {
        Map<String, Integer> rrdStats = new HashMap<String, Integer>();
        String rrdFilePath = rrdPath + File.separator + rrdFile;
        String[] command = {"fetch", rrdFilePath, "AVERAGE", "-s", "now-10m", "-e", "now"};
        try {
            CommandResult result = rrd.command(command);
            if (!result.isOk()) {
                logger.error("RRD Fetch execution for file [" + rrdFilePath + "] failed with error [ " + result.getError() + " ]");
                throw new TaskExecutionException("RRD Fetch execution for file [" + rrdFilePath + "] failed with error [ " + result.getError() + " ]");
            } else {
                List<FetchOutput> fetchOutputs = parseOutput(result);
                parseResult(rrdFile, fetchOutputs, rrdStats);
            }
        } catch (Exception e) {
            logger.error("Error executing rrd file [" + rrdFilePath + "]", e);
            throw new TaskExecutionException("Error executing rrd file [" + rrdFilePath + "]", e);
        }
        return rrdStats;
    }

    private void parseResult(String rrdFile, List<FetchOutput> outputs, Map<String, Integer> rrdStats) {
        FetchOutput second = outputs.get(1); //Get the latest result
        String name = rrdFile.substring(0, rrdFile.lastIndexOf("."));
        Map<String, Integer> values = second.getValues();
        for (Map.Entry<String, Integer> curVal : values.entrySet()) {
            rrdStats.put(name + "|" + curVal.getKey(), curVal.getValue());
        }
    }

    private List<FetchOutput> parseOutput(CommandResult result) {

        List<FetchOutput> fetchOutputs = new ArrayList<FetchOutput>();
        String output = result.getOutput();
        String[] split = output.trim().split("\n");
        String[] indexes = split[0].split(" +");
        for (String n : split) {
            if (n.contains(":") && !n.contains("nan")) {
                Long one = Long.parseLong(n.split(":")[0].trim());
                FetchOutput fetchOutput = new FetchOutput(new Date(one * 1000));
                String trim = n.split(":")[1].trim();
                int size = indexes.length;
                if (size > 1) {
                    String[] split1 = trim.split(" ");
                    int index = 0;
                    for (String curVal : split1) {
                        Integer value = Math.round(Float.parseFloat(curVal));
                        fetchOutput.addValue(indexes[index++], value);
                    }
                } else {
                    Integer value = Math.round(Float.parseFloat(trim));
                    fetchOutput.addValue(indexes[0], value);
                }

                fetchOutputs.add(fetchOutput);
            }
        }
        return fetchOutputs;
    }

    class FetchOutput {
        private Date date;
        private Map<String, Integer> values;

        public FetchOutput(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public Map<String, Integer> getValues() {
            return values;
        }

        public void addValue(String index, Integer value) {
            if (values == null) {
                values = new HashMap<String, Integer>();
            }
            values.put(index, value);
        }
    }
}