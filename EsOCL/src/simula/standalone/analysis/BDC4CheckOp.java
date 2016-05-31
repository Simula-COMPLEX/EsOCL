package simula.standalone.analysis;

import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;

public class BDC4CheckOp {
    OclInterpreter interpreter;
    private Utility utility = Utility.INSTANCE;
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
        Collection<IModelInstanceElement> leftInsCol = oclExpUtility
                .getResultCollection(this.interpreter.doSwitch(contents.get(0)));
        int leftColSize = leftInsCol.size();
        if (opName.equals("includes") || opName.equals("excludes")) {
            double in_minValue = 1, in_temp = 0, ex_temp = 0;
            for (int i = 0; i < leftColSize; i++) {
                double leftResult = Double.valueOf(leftInsCol.toArray()[i]
                        .toString());
                double rightResult = oclExpUtility
                        .getResultNumericValue(this.interpreter
                                .doSwitch(contents.get(1)));
                // excludes
                ex_temp += utility.normalize(bdc4CompOp.compareOp4Numeric(
                        leftResult, rightResult, "<>"));
                // includes
                in_temp = utility.normalize(bdc4CompOp.compareOp4Numeric(
                        leftResult, rightResult, "="));
                if (in_minValue - in_temp < 0) {
                    in_minValue = in_temp;
                }
            }
            if (opName.equals("includes"))
                return in_minValue;
            else
                return ex_temp;
        } else if (opName.equals("includesAll") || opName.equals("excludesAll")) {
            double inAll_temp = 0.0, exAll_temp = 0.0;
            Collection<IModelInstanceElement> rightInsCol = oclExpUtility
                    .getResultCollection(this.interpreter.doSwitch(contents
                            .get(1)));
            int rightColSize = rightInsCol.size();
            for (int i = 0; i < leftColSize; i++) {
                double leftResult_i = Double.valueOf(leftInsCol.toArray()[i]
                        .toString());
                double minValue_i = 1;
                for (int j = 0; j < rightColSize; j++) {
                    double rightResult_j = Double
                            .valueOf(rightInsCol.toArray()[j].toString());
                    // includesAll
                    double temp_j = utility
                            .normalize(bdc4CompOp.compareOp4Numeric(
                                    leftResult_i, rightResult_j, "="));
                    if (minValue_i - temp_j < 0) {
                        minValue_i = temp_j;
                    }
                    // excludesAll
                    exAll_temp += utility.normalize(bdc4CompOp
                            .compareOp4Numeric(leftResult_i, rightResult_j,
                                    "<>"));
                }
                inAll_temp += minValue_i;
            }
            if (opName.equals("includesAll"))
                return inAll_temp;
            else
                return exAll_temp;
        } else if (opName.equals("isEmpty")) {
            return utility.normalize(bdc4CompOp.compareOp4Numeric(leftColSize,
                    0, "="));
        } else if (opName.equals("notEmpty")) {
            return utility.normalize(bdc4CompOp.compareOp4Numeric(leftColSize,
                    0, "<>"));
        }
        return -1.0;
    }
}
