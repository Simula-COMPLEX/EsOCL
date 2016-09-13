package no.simula.esocl.api;

import no.simula.esocl.api.dataobject.Result;
import no.simula.esocl.experiment.SearchEngineDriver;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;

import java.util.HashMap;
import java.util.Map;

public class OCLSolver {


    private static UML2Model model;

    public static void init(UML2Model model) {
        OCLSolver.model = model;
    }

    public static void main(String[] args) {
        String inputModelPath = "/Users/hammad/git/EsOCLWeb/src/main/resources/model/RoyalAndLoyal.uml";
        String constraint = "context Transaction inv: self.amount >  50  and  self.points <  50";
        OCLSolver oclSolver = new OCLSolver();
        try {
            Result result = oclSolver.solveConstraint(inputModelPath, constraint, new String[]{"AVM", "OpOEA"}, 50000);
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
            Result result = searchEngineDriver.solveConstraint(inputModel, constraint, alogokey, 0, 5000);
            if (result.getResult()) {
                return result;
            }
        }


        Result result = new Result();
        result.setResult(false);
        return result;

    }


}
