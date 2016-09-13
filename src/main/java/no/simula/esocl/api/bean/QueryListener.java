package no.simula.esocl.api.bean;


import no.simula.esocl.api.OCLSolver;
import no.simula.esocl.api.dataobject.Result;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.File;
import java.io.Serializable;


@ManagedBean(name = "bean", eager = true)
@SessionScoped
public class QueryListener implements Serializable {
    private static final long serialVersionUID = 1L;
    public String[] algos = {"AVM", "OpOEA"};
    public Integer iterations = 50000;
    private Part diagram;
    private File inputModel;
    private String constraint;
    private Result result;
    private String modelName;

    public void preRenderView() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
        }
    }


    public void royalModel() {

        FacesContext fCtx = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
        String sessionId = session.getId();
        String path = getClass().getClassLoader().getResource("model/RoyalAndLoyal.uml").getPath();
        modelName = "RoyalAndLoyal.uml";
        constraint = "context Transaction inv: self.amount >  50  and  self.points <  50";
        inputModel = new File(path);

    }


    public void loadModel() {
        try {
            FacesContext fCtx = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
            String sessionId = session.getId();
            String path = getClass().getClassLoader().getResource("/").getPath() + File.pathSeparator + sessionId + diagram.getSubmittedFileName();
            modelName = diagram.getSubmittedFileName();
            inputModel = new File(path);
            if (!inputModel.isFile()) {
                inputModel.createNewFile();
            }
            diagram.write(inputModel.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Model Uploaded", "");
        facesContext.addMessage(null, message);
    }


    public void doNothing(AjaxBehaviorEvent event) {
        System.out.println("doNothing");
    }


    public void validateConstraint(AjaxBehaviorEvent event) {
        result = null;
        System.out.println("validate Constraint");
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (inputModel == null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Model not found", "Please Upload UML Model");
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

            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Result", "Constraint Solved");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Result", "Constraint Not Solved");
            facesContext.addMessage(null, message);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
            e.printStackTrace();
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
}
