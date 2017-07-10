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
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclAny;
import org.dresdenocl.essentialocl.standardlibrary.OclCollection;
import org.dresdenocl.essentialocl.standardlibrary.OclString;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class BDC4CompareOp {
    private OclInterpreter interpreter;
    private Utility utility = Utility.INSTANCE;
    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

    public BDC4CompareOp(OclInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public double handleCompareOp(IModelInstanceObject env,
                                  OclExpression leftExp, OclExpression rightExp, String opName) {
        this.interpreter.setEnviromentVariable("self", env);
        // to interpret the two parts of comparison operator
        OclAny left = this.interpreter.doSwitch(leftExp);
        OclAny right = this.interpreter.doSwitch(rightExp);
        if (left.oclIsUndefined().isTrue() || right.oclIsUndefined().isTrue()) {
            return Utility.K;
        }
        // handle the String with relation operator
        if (left instanceof OclString && right instanceof OclString) {
            // obtain the ASCII codes of left and right string
            int[] left_asc = oclExpUtility.getASC4String(((OclString) left)
                    .getModelInstanceString().getString());
            int[] right_asc = oclExpUtility.getASC4String(((OclString) right)
                    .getModelInstanceString().getString());
            double diversity = 0;
            if (left_asc.length - right_asc.length == 0) {
                for (int i = 0; i < left_asc.length; i++) {
                    diversity += Math.abs(left_asc[i] - right_asc[i]);
                }
                if (diversity == 0) {
                    if (opName.equals("="))
                        return 0;
                    else
                        return utility.formatValue(Utility.K
                                * utility.normalize(diversity));
                } else {
                    if (opName.equals("="))
                        return utility.formatValue((Utility.K / 2)
                                * utility.normalize(diversity));
                    else
                        return 0;
                }
            } else {
                if (opName.equals("="))
                    return utility.formatValue((Utility.K / 2)
                            * Math.abs(left_asc.length - right_asc.length)
                            + (Utility.K / 2));
                else
                    return utility.K;
            }
        }
        // handle the collection with relation operator
        if (left instanceof OclCollection && right instanceof OclCollection) {
            handleCollectionEquality(env, left, right, opName);
        }
        Double leftResult = 0.0, rightResult = 0.0;
        // obtain the double value based on the different types of evaluated
        // results
        leftResult = oclExpUtility.getResultNumericValue(left
                .getModelInstanceElement());
        rightResult = oclExpUtility.getResultNumericValue(right
                .getModelInstanceElement());
        // whether the left part of operator is the select->size
        String complexType = oclExpUtility.isComplexType(leftExp);
        if (complexType != null
                && complexType.equals(OCLExpUtility.OP_COMPLEX_SELECT_SIZE)) {
            // handle the select->size expression
            return handleComplexSelectSizeOp(env,
                    (OperationCallExpImpl) leftExp, leftResult, rightResult,
                    opName);
        }
        // handle the Real with "=" operator
        if (left.getClass().getSimpleName().equals("JavaOclReal")
                && right.getClass().getSimpleName().equals("JavaOclReal")
                && opName.equals("=")) {
            double leftResult_beforeDecimal = Double.valueOf(leftResult
                    .toString().split(".")[0]);
            double leftResult_afterDecimal = Utility.INSTANCE.formatRealValueWithoutZero(leftResult
                    .toString().split(".")[1]);
            double rightResult_beforeDecimal = Double.valueOf(rightResult
                    .toString().split(".")[0]);
            double rightResult_afterDecimal = Double.valueOf(rightResult
                    .toString().split(".")[1]);
            double diversity = leftResult - rightResult;
            double diversty_before = leftResult_beforeDecimal
                    - rightResult_beforeDecimal;
            double diversity_after = leftResult_afterDecimal
                    - rightResult_afterDecimal;
            if (diversity == 0)
                return 0.0;
            else
                return utility.formatValue(Utility.K
                        * utility.normalize(Math.abs(diversty_before)
                        + Math.abs(diversity_after)));
        }

        return compareOp4Numeric(leftResult, rightResult, opName);
    }

    public Double handleCollectionEquality(IModelInstanceObject env,
                                           OclAny left, OclAny right, String opName) {
        this.interpreter.setEnviromentVariable("self", env);
        // obtain the collection elements from the interpretation result
        Collection<IModelInstanceElement> leftInsCol = oclExpUtility
                .getResultCollection(left);
        Collection<IModelInstanceElement> rightInsCol = oclExpUtility
                .getResultCollection(right);
        // size of collection
        int leftColSize = leftInsCol.size();
        int rightColSize = rightInsCol.size();
        if (left.oclIsUndefined().isTrue() || right.oclIsUndefined().isTrue()) {
            return 1.0;
        }
        // check the conformance of types
        if (!left.oclType().getType().conformsTo(right.oclType().getType())) {
            return 0.8;
        } else if (leftColSize != rightColSize) {
            return 0.5 + 0.25 * utility.normalize(compareOp4Numeric(
                    leftColSize, rightColSize, "="));
        } else {
            double temp = 0.0;
            for (int i = 0; i < leftColSize; i++) {
                double leftResult = oclExpUtility
                        .getResultNumericValue((IModelInstanceElement) leftInsCol
                                .toArray()[i]);
                double rightResult = oclExpUtility
                        .getResultNumericValue((IModelInstanceElement) rightInsCol
                                .toArray()[i]);
                temp += utility.normalize(compareOp4Numeric(leftResult,
                        rightResult, "="));
            }
            // formalize the double value
            return utility.formatValue(temp * 0.5 / leftColSize);
        }
    }

    public double handleComplexSelectSizeOp(IModelInstanceObject env,
                                            OperationCallExpImpl sizeExp, double leftResult,
                                            double rightResult, String opName) {
        // obtain the select part of select->size expression
        EObject selectExp = sizeExp.eContents().get(0);
        // obtain the source elements for selection e.g. the self part of self->select
        Collection<IModelInstanceElement> envCol = oclExpUtility
                .getResultCollection(this.interpreter.doSwitch(selectExp
                        .eContents().get(0)));
        int selfSize = envCol.size();
        // obtain the iterators used in the select expression
        List<Variable> iterators = ((IteratorExpImpl) selectExp).getIterator();
        double temp = 0, temp_not = 0;
        BDC4BooleanOp bdc4BooleanOp = new BDC4BooleanOp(interpreter);
        for (int i = 0; i < envCol.size(); i++) {
            // Map the iterator into a source element
            this.interpreter.setEnviromentVariable(iterators.get(0).getName(),
                    (IModelInstanceElement) envCol.toArray()[i]);
            // argument expression of select expression e.g. the "p" part of select(p)
            OclExpression selectParaExp = (OclExpression) selectExp.eContents()
                    .get(1);
            // calculate the distance of argument expression of select expression
            temp += bdc4BooleanOp.handleBooleanOp(env, selectParaExp);
            // calculate the distance of argument expression with not operator
            temp_not += bdc4BooleanOp.notOp(env, selectParaExp);
        }
        // if the expression "select->size op value" is evaluated as true, return 0
        if (new BDC4CompareOp(this.interpreter).compareOp4Numeric(leftResult,
                rightResult, opName) == 0)
            return 0;
        // if not, calculate the distance
        if (opName.equals(">") || opName.equals(">=")) {
            if ((opName.equals(">") && (selfSize - rightResult) <= 0)
                    || (opName.equals(">=") && (selfSize - rightResult) < 0))
                return rightResult - selfSize + Utility.K;
            else {
                return utility.normalize(rightResult - leftResult + Utility.K
                        + utility.normalize(temp));
            }// end else
        }// end if
        else if (opName.equals("<") || opName.equals("<=")) {
            if ((opName.equals("<") && (selfSize - rightResult) >= 0)
                    || (opName.equals("<=") && (selfSize - rightResult) > 0))
                return rightResult - selfSize + Utility.K;
            else {
                return utility.normalize(rightResult - leftResult + Utility.K
                        + utility.normalize(temp_not));
            }// end else
        }// end else if
        else if (opName.equals("<>")) {
            if (leftResult == 0)
                return temp;
            else if ((leftResult - selfSize) == 0)
                return temp_not;
            else if (leftResult >= 0 && (leftResult - selfSize) <= 0)
                return Math.min(temp, temp_not);
        }// end else if
        else if (opName.equals("=")) {
            if ((leftResult - rightResult) > 0)
                return leftResult - rightResult + Utility.K
                        + utility.normalize(temp_not);
            else if ((leftResult - rightResult) < 0)
                return rightResult - selfSize + Utility.K
                        + utility.normalize(temp);
        } // end else if

        return -1;
    }

    public Double compareOp4Numeric(double leftResult, double rightResult,
                                    String opName) {
        // calculate the distance of comparison expression
        double diversity = leftResult - rightResult;
        if (opName.equals("=")) {
            if (diversity == 0)
                return 0.0;
            else
                return utility.formatValue(Utility.K
                        * utility.normalize(Math.abs(diversity)));
        } else if (opName.equals("<>")) {
            if (diversity != 0)
                return 0.0;
            else if (diversity == 0)
                return utility.formatValue(Utility.K
                        * utility.normalize(1 + Math.abs(diversity)));
            else
                return Utility.K;
        } else if (opName.equals("<")) {
            if (diversity < 0)
                return 0.0;
            else
                return utility.formatValue(Utility.K
                        * utility.normalize(1 + Math.abs(diversity)));
        } else if (opName.equals("<=")) {
            if (diversity <= 0)
                return 0.0;
            else
                return utility.formatValue(Utility.K
                        * utility.normalize(1 + Math.abs(diversity)));
        } else if (opName.equals(">")) {
            if (diversity > 0)
                return 0.0;
            else
                return utility.formatValue(Utility.K
                        * utility.normalize(1 + Math.abs(diversity)));
        } else if (opName.equals(">=")) {
            if (diversity >= 0)
                return 0.0;
            else
                return utility.formatValue(Utility.K
                        * utility.normalize(1 + Math.abs(diversity)));
        } else
            return -1.0;
    }

}
