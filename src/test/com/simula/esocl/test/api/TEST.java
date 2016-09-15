package com.simula.esocl.test.api;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TEST {

    public static void main(String[] args) {
        String constraint = "context Transaction inv: self.amount >  50  and  self.points >  50";
        String inputModelPath = "/Users/hammad/git/EsOCLWeb/src/main/resources/RoyalAndLoyal.uml";
        try {
            Path path = Paths.get(inputModelPath);
            byte[] model = Files.readAllBytes(path);


            System.out.println(model.length);
             String url = "http://esocl.herokuapp.com/EsOCLService";
            //String url = "http://zen.simula.no:8080/EsOCL/EsOCLService";
            //String url = "http://localhost:8090/EsOCLService";
            WebServiceEndPointService service = new WebServiceEndPointServiceLocator(url);
            WebServiceEndPoint port = service.getWebServiceEndPointPort();
            boolean response = port.getResult(constraint, model);


            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}


