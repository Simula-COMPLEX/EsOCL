package no.simula.esocl.oclga;

import no.simula.esocl.ocl.distance.ValueElement4Search;

import java.util.ArrayList;
import java.util.List;

public class FakeProblem implements Problem {

    @Override
    public ValueElement4Search[] getConstraints() {
        ValueElement4Search[] m = new ValueElement4Search[2];

        return m;
    }

    @Override
    public double getFitness(String[] value) {
        double sum = 0;

        //v[0] == c0;
        int c0 = -47;
        if (Integer.parseInt(value[0]) != c0)
            sum += Math.abs(Integer.parseInt(value[0]) - c0) + 1;

        //v[1]>900
        int c1 = 900;
        if (Integer.parseInt(value[1]) <= c1)
            sum += (c1 - Integer.parseInt(value[1])) + 1;

        if (Integer.parseInt(value[2]) != 4)
            sum += 1;

        if (Integer.parseInt(value[3]) != 17)
            sum += 1;

        return sum;
    }

    @Override
    public ArrayList<GeneValueScope> getGeneConstraints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] decoding(int v[]) {
        // TODO Auto-generated method stub
        return null;
    }


    public List<String> getSolutions() {
        return null;
    }

}
