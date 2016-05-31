package simula.standalone.analysis;

import org.dresdenocl.essentialocl.expressions.OclExpression;
import org.dresdenocl.essentialocl.expressions.Variable;
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclAny;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;

public class BDC4CompareOp {
    OclInterpreter interpreter;
    private Utility utility = Utility.INSTANCE;
    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

    public BDC4CompareOp(OclInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public double handleCompareOp(IModelInstanceObject env,
                                  OclExpression leftExp, OclExpression rightExp, String opName) {
        this.interpreter.setEnviromentVariable("self", env);
        OclAny left = this.interpreter.doSwitch(leftExp);
        OclAny right = this.interpreter.doSwitch(rightExp);
        if (left.oclIsUndefined().isTrue() || right.oclIsUndefined().isTrue()) {
            return Utility.K;
        }
        Double leftResult = 0.0, rightResult = 0.0;
        //obtain the int value based on the different types of evaluated results
        leftResult = oclExpUtility.getResultNumericValue(left);
        rightResult = oclExpUtility.getResultNumericValue(right);
        String complexType = oclExpUtility.isComplexType(leftExp);
        if (complexType != null
                && complexType.equals(OCLExpUtility.OP_COMPLEX_SELECT_SIZE)) {
            return handleComplexSelectSizeOp(env,
                    (OperationCallExpImpl) leftExp, leftResult, rightResult,
                    opName);
        }

        return compareOp4Numeric(leftResult, rightResult, opName);
    }

    public double handleComplexSelectSizeOp(IModelInstanceObject env,
                                            OperationCallExpImpl sizeExp, double leftResult,
                                            double rightResult, String opName) {
        // handle the complex situation
        EObject selectExp = sizeExp.eContents().get(0);
        Collection<IModelInstanceElement> envCol = oclExpUtility
                .getResultCollection(this.interpreter.doSwitch(selectExp
                        .eContents().get(0)));
        IModelInstanceElement[] envArray = (IModelInstanceElement[]) envCol
                .toArray();
        List<Variable> iterators = ((IteratorExpImpl) selectExp).getIterator();
        double temp = 0, temp_not = 0;
        for (int i = 0; i < envArray.length; i++) {
            this.interpreter.setEnviromentVariable(iterators.get(0).getName(),
                    envArray[i]);
            OperationCallExpImpl oce = (OperationCallExpImpl) selectExp
                    .eContents().get(1);
            OclExpression leftExp = (OclExpression) oce.eContents().get(0);
            OclExpression rightExp = (OclExpression) oce.eContents().get(1);
            String oce_opName = oce.getReferredOperation().getName();
            // d(p)
            temp += handleCompareOp(env, leftExp, rightExp, oce_opName);
            // d(not p)
            temp_not += handleCompareOp(env, leftExp, rightExp,
                    oclExpUtility.getOppositeOp(oce_opName));
        }
        double sourceSize = oclExpUtility
                .getResultNumericValue(this.interpreter.doSwitch(selectExp
                        .eContents().get(0)));
        if (opName.equals(">") || opName.equals(">=")) {
            if (sourceSize - rightResult <= 0)
                return rightResult - sourceSize + Utility.K;
            else {

                return utility.normalize(rightResult - leftResult) + Utility.K
                        + utility.normalize(temp);
            }// end else
        }// end if
        else if (opName.equals(">") || opName.equals(">=")) {
            if (sourceSize - rightResult >= 0)
                return rightResult - sourceSize + Utility.K;
            else {
                return utility.normalize(rightResult - leftResult) + Utility.K
                        + utility.normalize(temp_not);
            }// end else
        }// end else if
        else if (opName.equals("<>")) {
            if (leftResult == 0)
                return temp;
            else if ((leftResult - sourceSize) == 0)
                return temp_not;
            else if (leftResult >= 0 && (leftResult - sourceSize) <= 0)
                return Math.min(temp, temp_not);
        }// end else if
        else if (opName.equals("=")) {
            if ((leftResult - sourceSize) > 0)
                return leftResult - sourceSize + Utility.K
                        + utility.normalize(temp_not);
            else
                return sourceSize - leftResult + Utility.K
                        + utility.normalize(temp);
        } // end else if

        return -1;
    }

    public Double compareOp4Numeric(double leftResult, double rightResult,
                                    String opName) {
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
                        * utility.normalize(Math.abs(diversity)));
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
            if (diversity <= 0)
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
