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
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.PropertyCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclAny;
import org.dresdenocl.essentialocl.standardlibrary.OclBoolean;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class BDC4BooleanOp {

    private OclInterpreter interpreter;
    private Utility utility = Utility.INSTANCE;
    private int numOfUndClauses = 0;

    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

    public BDC4BooleanOp(OclInterpreter interpreter) {
        super();
        this.interpreter = interpreter;
    }

    /**
     * Handle the boolean expression and comparison expression
     *
     * @param env Instance Object
     * @param exp boolean expression
     * @return double
     */
    public double handleBooleanOp(IModelInstanceObject env, OclExpression exp) {
        // Map the Enviroment Variable into the concrete model instance object
        interpreter.setEnviromentVariable("self", env);

        // this expression may be a property call expression
        if (exp instanceof PropertyCallExpImpl) {
            return simplePropOrMiscOp(env, exp);
        } else if (exp instanceof OperationCallExpImpl) {
            // this expression may be a operation call expression
            OperationCallExpImpl operationCallExp = (OperationCallExpImpl) exp;

            // obtain the operation name
            String operationName = operationCallExp.getReferredOperation().getName();
            // handle the MISCELLANEOUS operation
            if (oclExpUtility.isBelongToOp(operationName, OCLExpUtility.OP_MISCELLANEOUS)) {
                return simplePropOrMiscOp(env, exp);
            } else if (OCLExpUtility.INSTANCE.isBelongToOp(operationName, OCLExpUtility.OP_CHECK)) {
                // "includes", "excludes", "includesAll","excludesAll", "isEmpty"
                BDC4CheckOp bdc4CheckOp = new BDC4CheckOp(interpreter);
                return bdc4CheckOp.handleCheckOp(env, (OperationCallExpImpl) exp);
            }

            EList<EObject> contents = operationCallExp.eContents();
            //    divide the whole expression into the left and right part according to the operator  e.g.  left op right
            OclExpression leftExpression = (OclExpression) contents.get(0);
            OclExpression rightExpression = (OclExpression) contents.get(1);

            if (operationName.equals("not")) {
                // the not operation does not have two parts, so the A_exp is the only part of not expression
                return notOp(env, leftExpression);
            }


            return classifyValue(env, leftExpression, rightExpression, operationName);

        } else if (exp instanceof IteratorExpImpl) {
            IteratorExpImpl iteratorExp = (IteratorExpImpl) exp;
            // "forAll", "exists", "isUnique", "one"
            BDC4IterateOp bdc4IterOp = new BDC4IterateOp(interpreter);
            return bdc4IterOp.handleIteratorOp(env, iteratorExp);
        }

        return -1;
    }

    /**
     * classify the expression into the Comparison or Boolean type
     *
     * @param env             Instance Object
     * @param leftExpression  left operand of operator
     * @param rightExpression right operand of operator
     * @param operationName   operation Name
     * @return double
     */

    private double classifyValue(IModelInstanceObject env, OclExpression leftExpression, OclExpression rightExpression,
                                 String operationName) {
        interpreter.setEnviromentVariable("self", env);

        if (oclExpUtility.isBelongToOp(operationName, OCLExpUtility.OP_COMPARE)) {

            // "=", "<>", "<", "<=", ">", ">="
            BDC4CompareOp bdc4CompOp = new BDC4CompareOp(this.interpreter);
            return bdc4CompOp.handleCompareOp(env, leftExpression, rightExpression, operationName);

        } else if (oclExpUtility.isBelongToOp(operationName, OCLExpUtility.OP_BOOLEAN)) {

            numOfUndClauses = 0;
            // use the interpreter to interpret the two parts
            OclAny leftExpressionResult = this.interpreter.doSwitch(leftExpression);
            OclAny rightExpressionResult = this.interpreter.doSwitch(rightExpression);

            if (leftExpressionResult.oclIsUndefined().isTrue()) {
                numOfUndClauses++;
            } else if (rightExpressionResult.oclIsUndefined().isTrue()) {
                this.numOfUndClauses++;
            }


            // distance calculation for and, or, implies, xor expression
            switch (operationName) {
                case "and": {
                    return andOp(env, leftExpression, rightExpression);
                }
                case "or": {
                    return orOp(env, leftExpression, rightExpression);
                }
                case "implies": {
                    return impliesOp(env, leftExpression, rightExpression);
                }
                case "xor": {
                    return xorOp(env, leftExpression, rightExpression);
                }
            }

        }
        return -1;
    }

    private double simplePropOrMiscOp(IModelInstanceObject env, OclExpression exp) {
        interpreter.setEnviromentVariable("self", env);
        OclAny result = interpreter.doSwitch(exp);

        if (result.oclIsUndefined().isTrue()) {
            return Utility.K;
        } else if (((OclBoolean) result).isTrue()) {
            return 0;
        }
        // d(exp) >0 && d(exp) < k

        return 0.8;
    }

    public double notOp(IModelInstanceObject env, OclExpression leftExpression) {
        OclAny A_Result = this.interpreter.doSwitch(leftExpression);

        if (((OclBoolean) A_Result).isTrue()) {
            return handleBooleanOp(env, leftExpression);
        }

        return 0;
    }


    public double andOp(IModelInstanceObject env, OclExpression leftExpression, OclExpression rightExpression) {

        double A_bdc = handleBooleanOp(env, leftExpression);
        double B_bdc = handleBooleanOp(env, rightExpression);

        return numOfUndClauses + utility.normalize(A_bdc + B_bdc);
    }

    private double orOp(IModelInstanceObject env, OclExpression leftExpression, OclExpression rightExpression) {
        double A_bdc = handleBooleanOp(env, leftExpression);
        double B_bdc = handleBooleanOp(env, rightExpression);
        return numOfUndClauses + utility.normalize(Math.min(A_bdc, B_bdc));
    }

    private double impliesOp(IModelInstanceObject env, OclExpression leftExpression, OclExpression rightExpression) {

        double notA_bdc = notOp(env, leftExpression);
        double B_bdc = handleBooleanOp(env, rightExpression);
        return numOfUndClauses + utility.normalize(Math.min(notA_bdc, B_bdc));
    }

    private double xorOp(IModelInstanceObject env, OclExpression leftExpression, OclExpression rightExpression) {
        double A_bdc = handleBooleanOp(env, leftExpression);
        double notA_bdc = notOp(env, leftExpression);

        double B_bdc = handleBooleanOp(env, rightExpression);
        double notB_bdc = notOp(env, rightExpression);

        double A_and_notB_bdc = numOfUndClauses + utility.normalize(A_bdc + notB_bdc);

        double notA_and_B_bdc = numOfUndClauses + utility.normalize(notA_bdc + B_bdc);

        return numOfUndClauses + utility.normalize(Math.min(A_and_notB_bdc, notA_and_B_bdc));
    }

}
