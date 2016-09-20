package no.simula.esocl.api;

import no.simula.esocl.experiment.Result;
import no.simula.esocl.experiment.SearchEngineDriver;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OCLSolver {
    public static String umlpath;
    static Logger logger = Logger.getLogger(OCLSolver.class);

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
        CommandLine cmd = parser.parse(options, args);

        if ((cmd.hasOption("u") || cmd.hasOption("uml"))) {
            umlpath = cmd.getOptionValue("u");
            if (umlpath == null || umlpath.isEmpty()) {
                umlpath = cmd.getOptionValue("uml");
            }
        }


        if ((cmd.hasOption("m") || cmd.hasOption("model")) && (cmd.hasOption("c") || cmd.hasOption("constraint") || cmd.hasOption("f") || cmd.hasOption("constraintfile"))) {
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

                OCLSolver oclSolver = new OCLSolver();
                try {
                    Result result = oclSolver.solveConstraint(model, constraint, new String[]{algorithm}, Integer.parseInt(generations));
                    //   System.out.println(result.getResult());
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

                    OCLSolver oclSolver = new OCLSolver();
                    try {
                        Result result = oclSolver.solveConstraint(model, file, new String[]{algorithm}, Integer.parseInt(generations));
                        //   System.out.println(result.getResult());
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


    public Result solveConstraint(String inputModel, File constraint, String alogs[], Integer iterations) throws Exception {

        Map<String, Integer> alogkeys = new HashMap<>();
        alogkeys.put("AVM", 0);
        alogkeys.put("SSGA", 1);
        alogkeys.put("OpOEA", 2);
        alogkeys.put("RandomSearch", 3);

        for (String algo : alogs) {

            int alogokey = alogkeys.get(algo);
            SearchEngineDriver searchEngineDriver = new SearchEngineDriver();
            Result result = searchEngineDriver.solveConstraint(inputModel, constraint, alogokey, 0, 500);
            if (result.getResult()) {
                return result;
            }
        }

        Result result = new Result();
        result.setResult(false);
        return result;

    }

    public Result solveConstraint(String inputModel, String constraint, String alogs[], Integer iterations) throws Exception {

        Map<String, Integer> alogkeys = new HashMap<>();
        alogkeys.put("AVM", 0);
        alogkeys.put("SSGA", 1);
        alogkeys.put("OpOEA", 2);
        alogkeys.put("RandomSearch", 3);


        for (String algo : alogs) {

            int alogokey = alogkeys.get(algo);
            SearchEngineDriver searchEngineDriver = new SearchEngineDriver();
            Result result = searchEngineDriver.solveConstraint(inputModel, constraint, alogokey, 0, 500);
            if (result.getResult()) {
                return result;
            }
        }

        Result result = new Result();
        result.setResult(false);
        return result;

    }


}


