package no.simula.esocl.api.service;


import no.simula.esocl.api.OCLSolver;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.File;
import java.io.FileOutputStream;


@WebService
public class WebServiceEndPoint {

    @WebMethod(operationName = "getResult")
    public boolean getResult(String constraint, byte[] model) {


        System.out.println(constraint);
        System.out.println("mode : " + model.length);

        try {

            String path = getClass().getClassLoader().getResource(File.separator).getPath()+ System.currentTimeMillis() + ".uml";
            File modelFile = new File(path);
            FileOutputStream fos = new FileOutputStream(modelFile);
            fos.write(model);
            fos.close();
            System.out.println(path);
            File inputModel = new File(path);
            if (!inputModel.exists()) {
                System.out.println("Model Not Found");
                return false;
            }

            OCLSolver oclSolver = new OCLSolver();

            boolean result = oclSolver.solveConstraint(inputModel.getAbsolutePath(), constraint, new String[]{"AVM", "OpOEA"}, 5000).getResult();
            System.out.println(result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}


