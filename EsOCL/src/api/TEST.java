package api;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TEST {

    public static void main(String[] args) {
        String constraint = "context Transaction inv: self.amount >  50  and  self.points >  50";
        String inputModelPath = "/Users/hammad/git/EsOCLWeb/EsOCL/resources/model/RoyalAndLoyal2.uml";
        try {
            Path path = Paths.get(inputModelPath);
            byte[] model = Files.readAllBytes(path);


            System.out.println(model.length);
          //  String url = "http://zen.simula.no:8080/EsOCL/";
          String url = "http://localhost:8080";
            WebServiceEndPointService service = new WebServiceEndPointServiceLocator(url);
            api.WebServiceEndPoint port = service.getWebServiceEndPointPort();
            boolean response = port.getResult(constraint, model);


            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
