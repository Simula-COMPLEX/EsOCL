package no.simula.esocl.standalone.analysis;

import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;

import java.util.List;

public class BDCManager {

    OclInterpreter interpreter;

    List<IModelInstanceObject> modelInstanceObjects;


    private Utility utility = Utility.INSTANCE;

    private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;


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


}
