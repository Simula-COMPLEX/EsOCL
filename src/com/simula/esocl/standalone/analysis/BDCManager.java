package com.simula.esocl.standalone.analysis;

import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclAny;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;

public class BDCManager {

    /**
     * singleton instance
     */
    private static BDCManager instance;
    public static BDCManager INSTANCE = instance();
    OclInterpreter interpreter;
    List<IModelInstanceObject> modelInstanceObjects;
    private Utility utility = Utility.INSTANCE;

    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

    private static BDCManager instance() {

        if (instance == null) {
            instance = new BDCManager();
        }
        return instance;
    }

    public void setOclInterpreter(OclInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public OclInterpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(OclInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public List<IModelInstanceObject> getModelInstanceObjects() {
        return modelInstanceObjects;
    }

    public void setModelInstanceObjects(
            List<IModelInstanceObject> modelInstanceObjects) {
        this.modelInstanceObjects = modelInstanceObjects;
    }

    public Double handleCollectionEquality(IModelInstanceObject env,
                                           OperationCallExpImpl opCallexp) {
        this.interpreter.setEnviromentVariable("self", env);
        BDC4CompareOp bdc4CompareOp = new BDC4CompareOp(this.interpreter);
        EList<EObject> contents = opCallexp.eContents();
        OclAny left = this.interpreter.doSwitch(contents.get(0));
        OclAny right = this.interpreter.doSwitch(contents.get(1));
        Collection<IModelInstanceElement> leftInsCol = oclExpUtility
                .getResultCollection(left);
        Collection<IModelInstanceElement> rightInsCol = oclExpUtility
                .getResultCollection(right);
        int leftColSize = leftInsCol.size();
        int rightColSize = rightInsCol.size();
        if (left.oclIsUndefined().isTrue() || right.oclIsUndefined().isTrue()) {
            return 1.0;
        }
        if (left.oclType().oclIsKindOf(right.oclType()).isTrue()) {
            return 0.5;
        } else if (leftColSize != rightColSize) {
            return 0.5 + 0.25 * utility.normalize(bdc4CompareOp
                    .compareOp4Numeric(leftColSize, rightColSize, "="));
        } else {
            double temp = 0.0;
            for (int i = 0; i < leftColSize; i++) {
                double leftResult = Double.valueOf(leftInsCol.toArray()[i]
                        .toString());
                double rightResult = Double.valueOf(rightInsCol.toArray()[i]
                        .toString());
                temp += utility.normalize(bdc4CompareOp.compareOp4Numeric(
                        leftResult, rightResult, "="));
            }
            return utility.formatValue(temp * 0.5 / leftColSize);
        }
    }

}
