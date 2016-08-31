package com.simula.esocl.api.bean;


import com.simula.esocl.api.OCLSolver;
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


    private Part diagram;
    private File inputModel;
    private String result;
    private String constraint;
    public String[] algos = {"AVM", "OpOEA"};
    public Integer iterations = 50000;

    public void preRenderView() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
        }
    }

    public void loadModel() {
        try {
            FacesContext fCtx = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
            String sessionId = session.getId();
            String path = getClass().getClassLoader().getResource("/").getPath() + File.pathSeparator + sessionId + diagram.getSubmittedFileName();
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
            boolean constraintresult = oclSolver.solveConstraint(inputModel, constraint, algos, iterations);
            System.out.println("Result: " + constraintresult);
            result = "" + constraintresult;
        } catch (Exception e) {
            result = "false";
            e.printStackTrace();
        }

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Result", result);
        facesContext.addMessage(null, message);
        RequestContext.getCurrentInstance().showMessageInDialog(message);

    }


    public Part getDiagram() {
        return diagram;
    }

    public void setDiagram(Part diagram) {
        this.diagram = diagram;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
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

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    public void setAlgos(String[] algos) {
        this.algos = algos;
    }
}
