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

    public double handleCompareOp(IModelInstanceObject env, OclExpression leftExp, OclExpression rightExp,
                                  String opName) {
        interpreter.setEnviromentVariable("self", env);

        // to interpret the two parts of comparison operator
        OclAny left = interpreter.doSwitch(leftExp);
        OclAny right = interpreter.doSwitch(rightExp);
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
                    if (opName.equals("=")) {
                        return 0;
                    } else {
                        return utility.formatValue(Utility.K
                                * utility.normalize(diversity));
                    }
                } else {
                    if (opName.equals("=")) {
                        return utility.formatValue((Utility.K / 2)
                                * utility.normalize(diversity));
                    } else {
                        return 0;
                    }
                }
            } else {
                if (opName.equals("=")) {
                    return utility.formatValue((Utility.K / 2)
                            * Math.abs(left_asc.length - right_asc.length)
                            + (Utility.K / 2));
                } else {
                    return Utility.K;
                }
            }
        }

        // handle the collection with relation operator
        if (left instanceof OclCollection && right instanceof OclCollection) {
            return handleCollectionEquality(env, left, right, opName);
        }


        // obtain the double value based on the different types of evaluated results
        Double leftResult = oclExpUtility.getResultNumericValue(left.getModelInstanceElement());
        Double rightResult = oclExpUtility.getResultNumericValue(right.getModelInstanceElement());

        // whether the left part of operator is the select->size
        String complexType = oclExpUtility.isComplexType(leftExp);
        if (complexType != null && complexType.equals(OCLExpUtility.OP_COMPLEX_SELECT_SIZE)) {

            // handle the select->size expression
            return handleComplexSelectSizeOp(env, (OperationCallExpImpl) leftExp, leftResult, rightResult, opName);
        }

        // handle the Real with "=" operator
        if (left.getClass().getSimpleName().equals("JavaOclReal")
                && right.getClass().getSimpleName().equals("JavaOclReal") && opName.equals("=")) {


            double leftResultBeforeDecimal = Double.valueOf(leftResult.toString().split(".")[0]);
            double leftResultAfterDecimal = Utility.INSTANCE.formatRealValueWithoutZero(leftResult.toString().split(".")[1]);


            double rightResultBeforeDecimal = Double.valueOf(rightResult.toString().split(".")[0]);
            double rightResultAfterDecimal = Double.valueOf(rightResult.toString().split(".")[1]);

            double diversity = leftResult - rightResult;
            double diversityBefore = leftResultBeforeDecimal - rightResultBeforeDecimal;
            double diversityAfter = leftResultAfterDecimal - rightResultAfterDecimal;

            if (diversity == 0) {
                return 0.0;
            } else {
                return utility.formatValue(Utility.K * utility.normalize(Math.abs(diversityBefore)
                        + Math.abs(diversityAfter)));
            }
        }

        return compareOp4Numeric(leftResult, rightResult, opName);
    }


    private double handleCollectionEquality(IModelInstanceObject env, OclAny left, OclAny right, String opName) {
        interpreter.setEnviromentVariable("self", env);


        if (left.oclIsUndefined().isTrue() || right.oclIsUndefined().isTrue()) {
            return 1.0;
        }

        // obtain the collection elements from the interpretation result
        Collection<IModelInstanceElement> leftInstanceElements = oclExpUtility.getResultCollection(left);
        Collection<IModelInstanceElement> rightInstanceElements = oclExpUtility.getResultCollection(right);

        // size of collection
        int leftColSize = leftInstanceElements.size();
        int rightColSize = rightInstanceElements.size();

        // check the conformance of types
        if (!left.oclType().getType().conformsTo(right.oclType().getType())) {
            return 0.8;

        } else if (leftColSize != rightColSize) {
            return 0.5 + 0.25 * utility.normalize(compareOp4Numeric(leftColSize, rightColSize, "="));

        } else {
            double temp = 0.0;

            for (int i = 0; i < leftColSize; i++) {
                double leftResult = oclExpUtility.getResultNumericValue((IModelInstanceElement) leftInstanceElements.toArray()[i]);

                double rightResult = oclExpUtility.getResultNumericValue((IModelInstanceElement) rightInstanceElements.toArray()[i]);

                temp += utility.normalize(compareOp4Numeric(leftResult, rightResult, "="));

            }

            // formalize the double value
            return utility.formatValue(temp * 0.5 / leftColSize);
        }
    }

    private double handleComplexSelectSizeOp(IModelInstanceObject env, OperationCallExpImpl sizeExp,
                                             double leftResult, double rightResult, String opName) {

        // obtain the select part of select->size expression
        EObject selectExp = sizeExp.eContents().get(0);

        // obtain the source elements for selection e.g. the self part of self->select
        Collection<IModelInstanceElement> envCol = oclExpUtility
                .getResultCollection(interpreter.doSwitch(selectExp.eContents().get(0)));

        int selfSize = envCol.size();

        // obtain the iterators used in the select expression
        List<Variable> iterators = ((IteratorExpImpl) selectExp).getIterator();

        double booleanOp = 0;
        double notOp = 0;

        BDC4BooleanOp bdc4BooleanOp = new BDC4BooleanOp(interpreter);
        for (int i = 0; i < envCol.size(); i++) {

            // Map the iterator into a source element
            interpreter.setEnviromentVariable(iterators.get(0).getName(), (IModelInstanceElement) envCol.toArray()[i]);
            // argument expression of select expression e.g. the "p" part of select(p)
            OclExpression selectParaExp = (OclExpression) selectExp.eContents()
                    .get(1);
            // calculate the distance of argument expression of select expression
            booleanOp += bdc4BooleanOp.handleBooleanOp(env, selectParaExp);
            // calculate the distance of argument expression with not operator
            notOp += bdc4BooleanOp.notOp(env, selectParaExp);

        }

        // if the expression "select->size op value" is evaluated as true, return 0
        if (new BDC4CompareOp(interpreter).compareOp4Numeric(leftResult, rightResult, opName) == 0) {
            return 0;
        }


        // if not, calculate the distance
        switch (opName) {
            case ">":
            case ">=": {
                if ((opName.equals(">") && (selfSize - rightResult) <= 0) ||
                        (opName.equals(">=") && (selfSize - rightResult) < 0))
                    return rightResult - selfSize + Utility.K;
                else {
                    return utility.normalize(rightResult - leftResult + Utility.K
                            + utility.normalize(booleanOp));
                }
            }
            case "<":
            case "<=": {
                if ((opName.equals("<") && (selfSize - rightResult) >= 0)
                        || (opName.equals("<=") && (selfSize - rightResult) > 0)) {
                    return rightResult - selfSize + Utility.K;
                } else {
                    return utility.normalize(rightResult - leftResult + Utility.K
                            + utility.normalize(notOp));
                }
            }
            case "<>": {
                if (leftResult == 0)
                    return booleanOp;
                else if ((leftResult - selfSize) == 0)
                    return notOp;
                else if (leftResult >= 0 && (leftResult - selfSize) <= 0)
                    return Math.min(booleanOp, notOp);

            }
            case "=": {
                if ((leftResult - rightResult) > 0) {
                    return leftResult - rightResult + Utility.K
                            + utility.normalize(notOp);
                } else if ((leftResult - rightResult) < 0) {
                    return rightResult - selfSize + Utility.K
                            + utility.normalize(booleanOp);
                }
            }

        }

        return -1;
    }

    public Double compareOp4Numeric(double leftResult, double rightResult, String opName) {
        // calculate the distance of comparison expression
        double diversity = leftResult - rightResult;
        switch (opName) {
            case "=":
                if (diversity == 0)
                    return 0.0;
                else
                    return utility.formatValue(Utility.K
                            * utility.normalize(Math.abs(diversity)));
            case "<>":
                if (diversity != 0)
                    return 0.0;
                else return utility.formatValue(Utility.K
                        * utility.normalize(1 + Math.abs(diversity)));
            case "<":
                if (diversity < 0)
                    return 0.0;
                else
                    return utility.formatValue(Utility.K
                            * utility.normalize(1 + Math.abs(diversity)));
            case "<=":
                if (diversity <= 0)
                    return 0.0;
                else
                    return utility.formatValue(Utility.K
                            * utility.normalize(1 + Math.abs(diversity)));
            case ">":
                if (diversity > 0)
                    return 0.0;
                else
                    return utility.formatValue(Utility.K
                            * utility.normalize(1 + Math.abs(diversity)));
            case ">=":
                if (diversity >= 0)
                    return 0.0;
                else
                    return utility.formatValue(Utility.K
                            * utility.normalize(1 + Math.abs(diversity)));
            default:
                return -1.0;
        }
    }


}
