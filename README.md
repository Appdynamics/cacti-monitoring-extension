# Cacti Monitoring Extension

This extension works only with the standalone machine agent.

##Use Case

Cacti is a complete network graphing solution designed to harness the power of RRDTool's data storage and graphing functionality. Cacti provides a fast poller, advanced graph templating, multiple data acquisition methods, and user management features out of the box. All of this is wrapped in an intuitive, easy to use interface that makes sense for LAN-sized installations up to complex networks with hundreds of devices.

##Prerequisite

This extension uses `rrdtool fetch` command to get stats from rrdfiles. So it should have access to the rrd files.

##Installation

1. Run "mvn clean install"
2. Download and unzip the file 'target/CactiMonitor.zip' to \<machineagent install dir\>/monitors
3. Open <b>monitor.xml</b> and configure yml path
4. Open <b>config.yml</b> and configure the cacti details

<b>monitor.xml</b>
~~~
<argument name="config-file" is-required="true" default-value=""monitors/CactiMonitor/config.yml" />
~~~

<b>config.yml</b>
~~~
# Zabbix particulars
# Cacti RRD particulars
rrd:
    rraPath: "/home/satish/AppDynamics/cacti/cacti-0.8.8c/rra"
    rrdFiles: [localhost_mem_buffers_8.rrd, localhost_hdd_free_13.rrd, localhost_mem_swap_9.rrd, localhost_proc_12.rrd, localhost_users_11.rrd]

#prefix used to show up metrics in AppDynamics
metricPathPrefix:  "Custom Metrics|Cacti|""
~~~
`rraPath`: Folder where all the rrd files are stored <br/>
`rrdFiles`: List of rrd files to get stats from.

##Metrics
The following metrics are reported.

As rrd tool saves the data in 5 min interval, the metric value what we get is the last stored value (5 min earlier)

| Metric Path  |
|----------------|
| Cacti/{rrd file name}/{metric name} |

##Contributing

Always feel free to fork and contribute any changes directly here on GitHub.

##Community

Find out more in the [AppSphere]() community.

##Support

For any questions or feature request, please contact [AppDynamics Center of Excellence](mailto:help@appdynamics.com).
