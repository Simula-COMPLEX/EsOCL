package no.simula.esocl.api.dataobject;

import java.util.List;

public class Result {

    private Boolean result;
    private String algo;
    private Integer iternations;
    private String solution;
    private List<String> solutions;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getAlgo() {
        return algo;
    }

    public void setAlgo(String algo) {
        this.algo = algo;
    }

    public Integer getIternations() {
        return iternations;
    }

    public void setIternations(Integer iternations) {
        this.iternations = iternations;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<String> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<String> solutions) {
        this.solutions = solutions;
    }
}
