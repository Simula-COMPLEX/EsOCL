package simula.api;


import org.dresdenocl.interpreter.OclInterpreterPlugin;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;
import org.dresdenocl.model.IModel;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.standalone.facade.StandaloneFacade;
import simula.oclga.Problem;
import simula.oclga.Search;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


@WebService
public class WebServiceEndPoint {

    @WebMethod(operationName = "getResult")
    public boolean getResult(String constraint, byte[] model) {


        System.out.println(constraint);
        System.out.println("mode : " + model.length);

        try {

            File inputModel = new File(System.currentTimeMillis() + ".uml");

            FileOutputStream fos = new FileOutputStream(inputModel);
            fos.write(model);
            fos.close();

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

            return getValidInstance(umlModel, constraintList, inputModel.getAbsolutePath());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    public static boolean getValidInstance(IModel model, List<Constraint> constraintList, String inputModelPath) {

        Search search = new simula.oclga.AVM();
        search.setMaxIterations(2000);

        Problem problem = new simula.ocl.distance.OclUmlProblem((UML2Model) model, constraintList);

        problem.processProblem(new File(inputModelPath));

        Search.validateConstraints(problem);

        int[] v = search.search(problem);


        return problem.getFitness(v) == 0d;


    }

}
