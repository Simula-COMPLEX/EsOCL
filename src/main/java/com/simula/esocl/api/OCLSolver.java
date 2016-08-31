package com.simula.esocl.api;

import com.simula.esocl.distance.OclUmlProblem;
import com.simula.esocl.oclga.AVM;
import com.simula.esocl.oclga.OpOEA;
import com.simula.esocl.oclga.Problem;
import com.simula.esocl.oclga.Search;
import org.dresdenocl.interpreter.OclInterpreterPlugin;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;
import org.dresdenocl.model.IModel;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.standalone.facade.StandaloneFacade;

import java.io.File;
import java.util.List;

public class OCLSolver {


    private static UML2Model model;

    public static void init(UML2Model model) {
        OCLSolver.model = model;
    }

    public static void main(String[] args) {
        String inputModelPath = "resources/model/RoyalAndLoyal2.uml";
        String constraint = "context Transaction inv: self.amount >  50  and  self.points >  50";
        File inputModel = new File(inputModelPath);
        OCLSolver oclSolver = new OCLSolver();
        try {
            boolean result = oclSolver.solveConstraint(inputModel, constraint, new String[]{"AVM", "OpOEA"}, 50000);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean solveConstraint(File inputModel, String constraint, String alogs[], Integer iterations) throws Exception {


        IModel umlModel = null;
        List<Constraint> constraintList = null;


        System.setProperty("DRESDENOCL_LOCATION_LOG4J",
                new File("log4j.properties").getAbsolutePath());

        System.setProperty("DRESDENOCL_LOCATION_ECLIPSE",
                new File("templates").getAbsolutePath() + File.separator);

        StandaloneFacade.INSTANCE.initialize();

        umlModel = StandaloneFacade.INSTANCE.loadUMLModel(inputModel,
                new File(getClass().getClassLoader().getResource("lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar").getFile()));

        System.out.println(new File(getClass().getClassLoader().getResource("lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar").getFile()).getAbsolutePath());


        new OclInterpreterPlugin();

        constraintList = StandaloneFacade.INSTANCE.parseOclConstraints(umlModel, constraint);

        return getValidInstance(umlModel, constraintList, inputModel.getAbsolutePath(), alogs, iterations);


    }


    public boolean getValidInstance(IModel model, List<Constraint> constraintList, String inputModelPath, String alogs[], Integer iterations) {


        for (String algo : alogs) {

            Search search = null;
            if (algo.equals("AVM")) {
                search = new AVM();
            } else if (algo.equals("OpOEA")) {
                search = new OpOEA();
            }

            if (search != null) {

                search.setMaxIterations(iterations);

                Problem problem = new OclUmlProblem((UML2Model) model, constraintList);

                problem.processProblem(new File(inputModelPath));

                Search.validateConstraints(problem);

                int[] v = search.search(problem);

                if (problem.getFitness(v) == 0d) {
                    return true;
                }
            }
        }


        return false;


    }


}
