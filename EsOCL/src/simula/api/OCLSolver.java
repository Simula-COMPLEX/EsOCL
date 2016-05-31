package simula.api;

import org.dresdenocl.interpreter.OclInterpreterPlugin;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;
import org.dresdenocl.model.IModel;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.standalone.facade.StandaloneFacade;
import simula.oclga.Problem;
import simula.oclga.Search;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OCLSolver {




    private static UML2Model model;

    public static void init(UML2Model model) {
        OCLSolver.model = model;
    }

    public static void main(String[] args) {


         String inputModelPath = "resources/model/RoyalAndLoyal2.uml";
         //String inputOclConstraintsPath = "resources/constraints/royal.ocl";


        String constraint="context Transaction inv: self.amount >  50  and  self.points >  50";

        File inputModel = new File(inputModelPath);
       // File inputOclConstraints = new File(inputOclConstraintsPath);

        IModel umlModel = null;
        List<Constraint> constraintList = null;

        try {
            System.setProperty("DRESDENOCL_LOCATION_LOG4J",
                    new File("log4j.properties").getAbsolutePath());

            System.setProperty("DRESDENOCL_LOCATION_ECLIPSE",
                    new File("templates").getAbsolutePath() + File.separator);

            StandaloneFacade.INSTANCE.initialize();

            umlModel = StandaloneFacade.INSTANCE.loadUMLModel(inputModel,
                    new File("lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar"));

            constraintList = StandaloneFacade.INSTANCE.parseOclConstraints(umlModel, constraint);
            new OclInterpreterPlugin();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test api
        OCLSolver.init((UML2Model) umlModel);
        Map<String, Object> result = OCLSolver.getValidInstance(constraintList,inputModelPath);

    }


    public static HashMap<String, Object> getValidInstance(List<Constraint> constraintList,String inputModelPath) {

        Search search = new simula.oclga.AVM();
        search.setMaxIterations(2000);

        Problem problem = new simula.ocl.distance.OclUmlProblem(model, constraintList);

        problem.processProblem(new File(inputModelPath));

        Search.validateConstraints(problem);

        int[] v = search.search(problem);


        System.out.println(problem.getFitness(v) == 0d);

        return null;
    }



}
