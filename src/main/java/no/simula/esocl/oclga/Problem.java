package no.simula.esocl.oclga;

import no.simula.esocl.ocl.distance.ValueElement4Search;

import java.util.ArrayList;
import java.util.List;

public interface Problem {
    public static final int NUMERICAL_TYPE = 0;
    public static final int CATEGORICAL_TYPE = 1;

    //get the constraints need to solve
    public ValueElement4Search[] getConstraints();

    //get the Gene Constraints used for search
    public ArrayList<GeneValueScope> getGeneConstraints();

    //decoding the individual to the String[]
    public String[] decoding(int v[]);

    // list of variables in String[]
    public double getFitness(String[] value);


    public List<String> getSolutions();
}
