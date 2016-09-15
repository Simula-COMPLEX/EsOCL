package no.simula.esocl.api.bean;


import no.simula.esocl.api.OCLSolver;
import no.simula.esocl.experiment.Result;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;


@ManagedBean(name = "bean", eager = true)
@SessionScoped
public class QueryListener implements Serializable {
    private static final long serialVersionUID = 1L;
    public String[] algos = {"AVM", "OpOEA"};
    public Integer iterations = 5000;
    private Part diagram;
    private File inputModel;
    private String constraint;
    private String resultConstraint;
    private Result result;
    private String modelName;

    public void preRenderView() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
        }
    }


    String type = "";


    public void reset() {
        diagram = null;
        inputModel = null;
        constraint = null;
        resultConstraint = null;
        result = null;
        modelName = null;
    }

    public void royalModel() {
        type = "royalModel";

        modelName = "RoyalAndLoyal.uml";
        constraint = "context Transaction inv: self.amount >  50  and  self.points <  50";


        loadModel("RoyalAndLoyal.uml");

        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Model Uploaded", "");
        facesContext.addMessage(null, message);

    }


    public void exportModel() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext ec = facesContext.getExternalContext();

        ec.responseReset();

        try {

            InputStream model = getClass().getClassLoader().getResourceAsStream("RoyalAndLoyal.uml");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = model.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            byte[] dataArray = buffer.toByteArray();

            ec.setResponseContentType("application/download");
            ec.setResponseContentLength(dataArray.length);
            String attachmentName =
                    "attachment; filename=\"" + "RoyalAndLoyal.uml\"";
            ec.setResponseHeader("Content-Disposition", attachmentName);

            OutputStream output = ec.getResponseOutputStream();
            output.write(dataArray);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        facesContext.responseComplete();
    }


    private void loadModel(String modelName) {


        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            String path = getClass().getClassLoader().getResource(File.separator ).getPath()  + System.currentTimeMillis() + modelName;
            inputModel = new File(path);
            if (inputModel.exists()) {
                inputModel.delete();
            }
            inputModel.createNewFile();

            String modelPath = getClass().getClassLoader().getResource(modelName).getPath();
            File modelFile = new File(modelPath);
            inStream = new FileInputStream(modelFile);
            outStream = new FileOutputStream(inputModel);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);

            }
            inStream.close();
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadModel() {
        type = "loadModel";
        try {
            FacesContext fCtx = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
            String sessionId = session.getId();
            String path = getClass().getClassLoader().getResource(File.separator ).getPath() + System.currentTimeMillis() + diagram.getSubmittedFileName();
            modelName = diagram.getSubmittedFileName();
            File modelFile = new File(path);
            if (!modelFile.exists()) {
                modelFile.createNewFile();
            }
            diagram.write(modelFile.getAbsolutePath());

            System.out.println(path);
            inputModel = new File(path);

            FacesContext facesContext = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Model Uploaded", "");
            facesContext.addMessage(null, message);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void doNothing(AjaxBehaviorEvent event) {
        System.out.println("doNothing");
    }


    public void validateConstraint(AjaxBehaviorEvent event) {


        result = null;
        resultConstraint = constraint;
        System.out.println("validate Constraint");
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (inputModel == null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Model not found", "Please Upload UML Model");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return;
        }


        if (!inputModel.exists()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Model not found", "Model File Not Found");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return;
        }


        if (constraint == null || constraint.isEmpty()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Constraint not found", "Please Enter the Constraint");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return;
        }


        if (algos == null || algos.length < 1) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "algos not found", "Please selects algos");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return;
        }


        if (iterations == null || iterations < 1) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "iterations not found", "Please Enter Iterations");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            return;
        }

        try {

            OCLSolver oclSolver = new OCLSolver();
            result = oclSolver.solveConstraint(inputModel.getAbsolutePath(), constraint, algos, iterations);

            if (result != null && result.getResult()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Result", "Constraint Solved");
                facesContext.addMessage(null, message);
                RequestContext.getCurrentInstance().showMessageInDialog(message);
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Result", "Constraint Not Solved");
                facesContext.addMessage(null, message);
                RequestContext.getCurrentInstance().showMessageInDialog(message);
            }
        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Result", "Constraint Not Solved");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            e.printStackTrace();
        }


        if (type.equals("loadModel")) {
            inputModel = null;
            modelName = null;
            constraint = "";

        } else if (type.equals("royalModel")) {
            loadModel("RoyalAndLoyal.uml");
            constraint = "";
        }

    }


    public Part getDiagram() {
        return diagram;
    }

    public void setDiagram(Part diagram) {
        this.diagram = diagram;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String[] getAlgos() {
        return algos;
    }

    public void setAlgos(String[] algos) {
        this.algos = algos;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getResultConstraint() {
        return resultConstraint;
    }

    public void setResultConstraint(String resultConstraint) {
        this.resultConstraint = resultConstraint;
    }
}
