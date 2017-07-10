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

import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;

import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
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
