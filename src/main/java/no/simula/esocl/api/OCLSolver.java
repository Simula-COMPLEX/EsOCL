package no.simula.esocl.api;

import no.simula.esocl.api.dataobject.Result;
import no.simula.esocl.experiment.SearchEngineDriver;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;

import java.util.HashMap;
import java.util.Map;

public class OCLSolver {


    public static void main(String[] args) {
        String inputModelPath = "/Users/hammad/git/EsOCLWeb/src/main/resources/RoyalAndLoyal.uml";
        String constraint = "context LoyaltyAccount inv: self.points > 0 implies self.transactions->exists(t | t.points > 0 )";
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 5000);
            System.out.println(result.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
