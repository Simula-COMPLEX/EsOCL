/* ****************************************************************************
 * Copyright (c) 2017 Simula Research Laboratory AS.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Shaukat Ali  shaukat@simula.no
 **************************************************************************** */

package no.simula.esocl.solver;

import no.simula.esocl.ocl.distance.Result;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class CommandLine {
    public static String umlpath;
    static Logger logger = Logger.getLogger(CommandLine.class);

    public static void main(String[] args) throws ParseException {


        Options options = new Options();


        Option option1 = new Option("m", "model", true, "UML Model (2.3) File Path");
        option1.setArgs(1);
        options.addOption(option1);

        Option option2 = new Option("c", "constraint", true, "OCL Constraint (Example \"Context constraint inv : \")");
        option2.setArgs(1);
        options.addOption(option2);

        Option option3 = new Option("f", "constraintfile", true, "Constraint File Path");
        option3.setArgs(1);
        options.addOption(option3);

        Option option4 = new Option("a", "algorithm", true, "Search Algorithm (AVM, SSGA, OpOEA, RandomSearch) default AVM");
        option4.setArgs(1);
        options.addOption(option4);


        Option option5 = new Option("g", "generations", true, "Max Generations default 5000");
        option5.setArgs(1);
        options.addOption(option5);

        Option option6 = new Option("h", "help", false, "Help");
        option6.setArgs(0);
        options.addOption(option6);

        Option option7 = new Option("u", "uml", false, "UML JAR File");
        option7.setArgs(1);
        options.addOption(option7);

        CommandLineParser parser = new DefaultParser();
        org.apache.commons.cli.CommandLine cmd = parser.parse(options, args);

        if ((cmd.hasOption("u") || cmd.hasOption("uml"))) {
            umlpath = cmd.getOptionValue("u");
            if (umlpath == null || umlpath.isEmpty()) {
                umlpath = cmd.getOptionValue("uml");
            }
        }


        if ((cmd.hasOption("m") || cmd.hasOption("model")) && (cmd.hasOption("c") || cmd.hasOption("constraint") || cmd.hasOption("f") || cmd.hasOption("constraintfile"))) {

            OCLSolver searchEngineDriver = new OCLSolver();

            String model = cmd.getOptionValue("m");
            if (model == null || model.isEmpty()) {
                model = cmd.getOptionValue("model");
            }

            if (model == null || model.isEmpty()) {
                System.out.println("Model File Not Found");
                return;
            } else {
                File file = new File(model);
                if (!file.exists()) {
                    System.out.println("Model File Not Found Please Enter Correct Path");
                }
            }

            String algorithm = cmd.getOptionValue("a");
            if (algorithm == null || algorithm.isEmpty()) {
                algorithm = cmd.getOptionValue("algorithm", "AVM");
            }


            String generations = cmd.getOptionValue("g");
            if (generations == null || generations.isEmpty()) {
                generations = cmd.getOptionValue("generations", "5000");
            }


            if (cmd.hasOption("c") || cmd.hasOption("constraint")) {
                String constraint = cmd.getOptionValue("c");
                if (constraint == null || constraint.isEmpty()) {
                    constraint = cmd.getOptionValue("constraint");
                }

                if (constraint == null || constraint.isEmpty()) {
                    System.out.println("Please Enter Constraint");
                    return;
                }
                System.out.println(constraint);

                try {
                    Result result = searchEngineDriver.solveConstraint(model, constraint, new String[]{algorithm}, Integer.parseInt(generations));

                    searchEngineDriver.printResults(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (cmd.hasOption("f") || cmd.hasOption("constraintfile")) {
                String constraint = cmd.getOptionValue("f");
                if (constraint == null || constraint.isEmpty()) {
                    constraint = cmd.getOptionValue("constraintfile");
                }

                if (constraint == null || constraint.isEmpty()) {
                    System.out.println("Please Enter Constraint File Path");
                    return;
                }


                if (constraint == null || constraint.isEmpty()) {
                    System.out.println("Constraint File Not Found");
                    return;
                } else {
                    File file = new File(constraint);
                    if (!file.exists()) {
                        System.out.println("Constraint File Not Found Please Enter Correct Path");
                        return;
                    }


                    try {


                        Result result = searchEngineDriver.solveConstraint(model, file, new String[]{algorithm}, Integer.parseInt(generations));
                        searchEngineDriver.printResults(result);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }


        } else if (cmd.hasOption("h") || cmd.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("help", options);

        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("help", options);
            return;
        }


    }


}


