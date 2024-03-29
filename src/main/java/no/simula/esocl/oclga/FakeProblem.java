/* ****************************************************************************
 * Copyright (c) 2017 Simula Research Laboratory AS.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Shaukat Ali  shaukat@simula.no
 **************************************************************************** */

package no.simula.esocl.oclga;

import no.simula.esocl.ocl.distance.ValueElement4Search;
import org.dresdenocl.modelinstance.IModelInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class FakeProblem implements Problem {

    @Override
    public ValueElement4Search[] getConstraintElements4Search() {
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
    public List<String> getStringInstances() {
        return null;
    }

    @Override
    public ArrayList<GeneValueScope> getGeneConstraints() {

        return null;
    }

    @Override
    public String[] decoding(int v[]) {

        return null;
    }


    @Override
    public List<IModelInstance> getObjects() {
        return null;
    }

}
