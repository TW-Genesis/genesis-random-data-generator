package com.genesis.datagenerator;

import org.apache.commons.cli.*;

public class CmdInputParser {
    public final static String ec = "ec";
    public final static String dir = "dir";
    public final static String nodes = "N";

    public static CommandLine getCmd(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();
        Options options = getOptions();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }

        return cmd;
    }

    private static Options getOptions() {
        Options options = new Options();
        Option ecOpt = Option.builder(ec).longOpt("experimentCount")
                .argName(ec)
                .hasArg()
                .required(true)
                .desc("count of total experiments").build();

        Option dirOpt = Option.builder(dir).longOpt("directory")
                .argName(dir)
                .hasArg()
                .required(false)
                .desc("output directory").build();

        Option nodesOpt = Option.builder(nodes).longOpt("nodes")
                .argName(nodes)
                .hasArg()
                .required(false)
                .desc("number of nodes for experiment data distribution").build();

        options.addOption(ecOpt);
        options.addOption(dirOpt);
        options.addOption(nodesOpt);

        return options;
    }
}
