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

package no.simula.esocl.standalone.analysis;

import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class BDC4CheckOp {

    private OclInterpreter interpreter;

    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

    public BDC4CheckOp(OclInterpreter interpreter) {

        this.interpreter = interpreter;
    }

    public double handleCheckOp(IModelInstanceObject env,
                                OperationCallExpImpl opCallexp) {
        this.interpreter.setEnviromentVariable("self", env);
        BDC4CompareOp bdc4CompOp = new BDC4CompareOp(this.interpreter);
        String opName = opCallexp.getReferredOperation().getName();
        EList<EObject> contents = opCallexp.eContents();
        // obtain the result elements of left part in the check expression e.g. the self part of self->includes
        Collection<IModelInstanceElement> leftInsCol = oclExpUtility
                .getResultCollection(this.interpreter.doSwitch(contents.get(0)));
        int leftColSize = leftInsCol.size();
        switch (opName) {
            case "includes":
            case "excludes":
                double in_minValue = 1, in_temp = 0, ex_temp = 0;
                for (int i = 0; i < leftColSize; i++) {
                    // obtain the double value of result element
                    double leftResult = oclExpUtility
                            .getResultNumericValue((IModelInstanceElement) leftInsCol
                                    .toArray()[i]);
                    double rightResult = oclExpUtility
                            .getResultNumericValue(this.interpreter.doSwitch(
                                    contents.get(1)).getModelInstanceElement());
                    // calculate the distance for each element in the excludes expression
                    ex_temp += bdc4CompOp.compareOp4Numeric(leftResult,
                            rightResult, "<>");
                    // calculate the distance for each element in the includes expression
                    in_temp = bdc4CompOp.compareOp4Numeric(leftResult, rightResult,
                            "=");
                    if (in_temp - in_minValue <= 0) {
                        in_minValue = in_temp;
                    }
                }
                if (opName.equals("includes"))
                    return in_minValue;
                else
                    return ex_temp;
            case "includesAll":
            case "excludesAll":
                double inAll_temp = 0.0, exAll_temp = 0.0;
                Collection<IModelInstanceElement> rightInsCol = oclExpUtility
                        .getResultCollection(this.interpreter.doSwitch(contents
                                .get(1)));
                int rightColSize = rightInsCol.size();
                for (int i = 0; i < leftColSize; i++) {
                    double leftResult_i = oclExpUtility
                            .getResultNumericValue((IModelInstanceElement) leftInsCol
                                    .toArray()[i]);
                    double minValue_i = 1;
                    for (int j = 0; j < rightColSize; j++) {
                        double rightResult_j = oclExpUtility
                                .getResultNumericValue((IModelInstanceElement) rightInsCol
                                        .toArray()[j]);
                        // for includesAll expression
                        double temp_j = bdc4CompOp.compareOp4Numeric(leftResult_i,
                                rightResult_j, "=");
                        if (temp_j - minValue_i <= 0) {
                            minValue_i = temp_j;
                        }
                        // for excludesAll expression
                        exAll_temp += bdc4CompOp.compareOp4Numeric(leftResult_i,
                                rightResult_j, "<>");
                    }
                    inAll_temp += minValue_i;
                }
                if (opName.equals("includesAll")) {
                    if (inAll_temp - 0 == 0)
                        System.out.println();
                    return inAll_temp;
                } else
                    return exAll_temp;
            case "isEmpty":
                return bdc4CompOp.compareOp4Numeric(leftColSize, 0, "=");
            case "notEmpty":
                return bdc4CompOp.compareOp4Numeric(leftColSize, 0, "<>");
        }
        return -1.0;
    }
}
