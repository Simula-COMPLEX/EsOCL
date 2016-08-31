package com.simula.esocl.api.service;


import com.simula.esocl.api.OCLSolver;

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

            File inputModel = new File(System.currentTimeMillis() + ".uml");
            FileOutputStream fos = new FileOutputStream(inputModel);
            fos.write(model);
            fos.close();


            OCLSolver oclSolver = new OCLSolver();

            boolean result = oclSolver.solveConstraint(inputModel, constraint, new String[]{"AVM", "OpOEA"}, 50000);
            System.out.println(result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}


