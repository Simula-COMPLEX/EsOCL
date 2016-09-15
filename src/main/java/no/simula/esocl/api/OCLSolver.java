package no.simula.esocl.api;

import no.simula.esocl.experiment.Result;
import no.simula.esocl.experiment.SearchEngineDriver;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class OCLSolver {

    static Logger logger = Logger.getLogger(OCLSolver.class);



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


