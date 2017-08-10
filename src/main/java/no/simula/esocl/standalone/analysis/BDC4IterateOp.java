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

import org.dresdenocl.essentialocl.expressions.OclExpression;
import org.dresdenocl.essentialocl.expressions.Variable;
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclAny;
import org.dresdenocl.essentialocl.standardlibrary.OclBoolean;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class BDC4IterateOp {
    private OclInterpreter interpreter;
    private Utility utility = Utility.INSTANCE;
    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

    public BDC4IterateOp(OclInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public double handleIteratorOp(IModelInstanceObject env, IteratorExpImpl iteratorExp) {
        interpreter.setEnviromentVariable("self", env);

        // obtain the operator name of iteration expression
        String opName = iteratorExp.getName();
        EList<EObject> contents = iteratorExp.eContents();

        // obtain the result elements of left part in the check expression e.g. the self part of self->forAll
        Collection<IModelInstanceElement> envCol = oclExpUtility.getResultCollection(interpreter.doSwitch(contents.get(0)));

        // transform the collection to array
        IModelInstanceElement[] envArray = envCol.toArray(new IModelInstanceElement[envCol.size()]);

        // obtain the iterators of iteration expression
        List<Variable> iterators = iteratorExp.getIterator();
        String complexType = oclExpUtility.isComplexType(iteratorExp);

        // handle the complex situation e.g. select->forAll
        if (complexType != null && complexType.equals(OCLExpUtility.OP_COMPLEX_SELECT_ITERATE)) {
            return handleComplexSelectIterateOp(env, iteratorExp);
        }

        // argument expression for each iteration
        OclExpression paraExp = (OclExpression) contents.get(1);
        switch (opName) {
            case "forAll": {
                return forAllOp(env, envArray, iterators, null, paraExp, null);
            }
            case "exists": {
                return existsOp(env, envArray, iterators, null, paraExp, null);
            }
            case "isUnique": {
                return isUniqueOp(envArray, iterators, paraExp);
            }
            case "one": {
                return oneOp(env, envArray, iterators, null, paraExp, null);
            }
        }

        return -1;
    }

    private double handleComplexSelectIterateOp(IModelInstanceObject env,
                                                IteratorExpImpl iteratorExp) {
        String opName = iteratorExp.getName();
        EObject selectExp = iteratorExp.eContents().get(0);

        // obtain the result elements of left part in the select expression e.g. the self part of self->select->forAll
        Collection<IModelInstanceElement> envCol = oclExpUtility
                .getResultCollection(interpreter.doSwitch(selectExp.eContents().get(0)));


        IModelInstanceElement[] envArray = envCol.toArray(new IModelInstanceElement[envCol.size()]);

        // obtain the iterator of select expression
        Variable selectIterator = ((IteratorExpImpl) selectExp).getIterator().get(0);

        // obtain the iterators of iteration expression
        List<Variable> iterateIterators = iteratorExp.getIterator();

        // obtain the argument expression of select and iteration expression
        OclExpression iterateParaExp = (OclExpression) iteratorExp.eContents().get(1);

        OclExpression selectParaExp = (OclExpression) selectExp.eContents().get(1);

        double distance = -1;
        switch (opName) {
            case "forAll": {
                distance = forAllOp(env, envArray, iterateIterators, selectIterator, iterateParaExp, selectParaExp);
                break;
            }
            case "exists": {
                distance = existsOp(env, envArray, iterateIterators, selectIterator, iterateParaExp, selectParaExp);
                break;
            }
            case "isUnique": {
                distance = isUniqueOp(envArray, iterateIterators, iterateParaExp);
                break;
            }
            case "one": {
                distance = oneOp(env, envArray, iterateIterators, selectIterator,
                        iterateParaExp, selectParaExp);
                break;
            }
        }
        return distance;
    }

    private double forAllOp(IModelInstanceObject env, IModelInstanceElement[] envArray, List<Variable> forAllIterators,
                            Variable selectIterator, OclExpression forAllParaExp, OclExpression selectParaExp) {

        // build the index array of source elements
        int[] input = utility.genIndexArray(envArray.length);
        int iteratorSize = forAllIterators.size();
        if (envArray.length == 0) {
            return 0;
        } else {
            double temp = 0;
            /*
               build the combination with repetition for index array

               input = {0,1}
               e.g. the result is {0,0},{0,1},{1,0},{1,1}
             */

            int[][] envComb = utility.combInArrayDup(input, iteratorSize);
            for (int[] anEnvComb : envComb) {

                for (int j = 0; j < iteratorSize; j++) {
                    interpreter.setEnviromentVariable(
                            forAllIterators.get(j).getName(),
                            envArray[anEnvComb[j]]);
                }

                BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(this.interpreter);
                if (selectParaExp == null)
                    temp += bdc4BoolOp.handleBooleanOp(env, forAllParaExp);
                else {
                    temp = bdc4BoolOp.handleBooleanOp(env, forAllParaExp);

                    for (int j = 0; j < forAllIterators.size(); j++) {

                        interpreter.setEnviromentVariable(selectIterator.getName(), envArray[anEnvComb[j]]);

                        temp = utility.normalize(Math.min(temp, bdc4BoolOp.notOp(env, selectParaExp)));
                    }
                }
            }
            return utility.formatValue(temp / envComb.length);
        }
    }

    private double existsOp(IModelInstanceObject env, IModelInstanceElement[] envArray, List<Variable> existsIterators,
                            Variable selectIterator, OclExpression existsParaExp, OclExpression selectParaExp) {

        int[] input = utility.genIndexArray(envArray.length);
        int existsIteratorSize = existsIterators.size();

        double temp;
        double minValue = 1;

        // Composition with replication
        int[][] envComb = utility.combInArrayDup(input, existsIteratorSize);
        for (int[] anEnvComb : envComb) {

            for (int j = 0; j < existsIteratorSize; j++) {
                interpreter.setEnviromentVariable(existsIterators.get(j).getName(), envArray[anEnvComb[j]]);
            }

            BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(interpreter);
            if (selectParaExp == null) {
                temp = bdc4BoolOp.handleBooleanOp(env, existsParaExp);
            } else {
                // temp = bdc4BoolOp.classifyValue(env, p1, p2, modifyOp);
                temp = bdc4BoolOp.handleBooleanOp(env, existsParaExp);
                for (int j = 0; j < existsIteratorSize; j++) {
                    interpreter.setEnviromentVariable(selectIterator.getName(), envArray[anEnvComb[j]]);

                    temp = utility.normalize(temp + bdc4BoolOp.handleBooleanOp(env, selectParaExp));
                }
            }

            if (temp - minValue < 0) {
                minValue = temp;
            }
        }
        return minValue;
    }

    private double isUniqueOp(IModelInstanceElement[] envArray,
                              List<Variable> uniqueIterators, OclExpression uniqueParaExp) {
        int[] input = utility.genIndexArray(envArray.length);
        double temp = 0;
        int envColsize = envArray.length;
        BDC4CompareOp bdc4CompOp = new BDC4CompareOp(this.interpreter);
        int[][] envComb = Utility.INSTANCE.getArrangeOrCombine(input);

        for (int[] anEnvComb : envComb) {

            interpreter.setEnviromentVariable(uniqueIterators.get(0).getName(), envArray[anEnvComb[0]]);

            double leftValue = oclExpUtility.getResultNumericValue(interpreter.doSwitch(uniqueParaExp)
                    .getModelInstanceElement());

            interpreter.setEnviromentVariable(uniqueIterators.get(0).getName(), envArray[anEnvComb[1]]);

            double rightValue = oclExpUtility.getResultNumericValue(interpreter.doSwitch(uniqueParaExp)
                    .getModelInstanceElement());

            temp += bdc4CompOp.compareOp4Numeric(leftValue, rightValue, "<>");
        }

        return Utility.INSTANCE.formatValue(2 * temp / (envColsize * (envColsize - 1)));

    }

    private double oneOp(IModelInstanceObject env, IModelInstanceElement[] envArray, List<Variable> oneIterators,
                         Variable selectIterator, OclExpression oneParaExp, OclExpression selectParaExp) {

        BDC4BooleanOp bdc4BooleanOp = new BDC4BooleanOp(interpreter);

        int count = 0;
        double temp = 0;
        double temp_not = 0;

        for (IModelInstanceElement anEnvArray : envArray) {
            this.interpreter.setEnviromentVariable(oneIterators.get(0).getName(), anEnvArray);

            if (selectParaExp != null) {
                this.interpreter.setEnviromentVariable(selectIterator.getName(), anEnvArray);
                // d(p)

                temp += bdc4BooleanOp.andOp(env, selectParaExp, oneParaExp);
                OclAny oneParaResult = interpreter.doSwitch(oneParaExp);
                OclAny selectParaResult = interpreter.doSwitch(selectParaExp);

                // d(not p)
                if (((OclBoolean) oneParaResult).isTrue() && ((OclBoolean) selectParaResult).isTrue()) {
                    temp_not += bdc4BooleanOp.andOp(env, selectParaExp, oneParaExp);
                }

                if (((OclBoolean) oneParaResult).isTrue() && ((OclBoolean) selectParaResult).isTrue()) {
                    count++;
                }

            } else {

                // d(p)
                temp += bdc4BooleanOp.handleBooleanOp(env, oneParaExp);
                // d(not p)
                temp_not += bdc4BooleanOp.notOp(env, oneParaExp);

                OclAny oneParaResult = this.interpreter.doSwitch(oneParaExp);
                if (((OclBoolean) oneParaResult).isTrue()) {
                    count++;
                }

            }
        }

        if ((count - 1) > 0) {
            return count - 1 + Utility.K + utility.normalize(temp_not);
        } else if ((count - 1) < 0) {
            return 1 - envArray.length + Utility.K + utility.normalize(temp);
        } else {
            return 0;
        }
    }

}
