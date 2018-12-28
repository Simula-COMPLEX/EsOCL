package simula.standalone.analysis;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import tudresden.ocl20.pivot.essentialocl.expressions.OclExpression;
import tudresden.ocl20.pivot.essentialocl.expressions.Variable;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclBoolean;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceElement;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.Expression;

public class BDCManager {

	OclInterpreter interpreter;

	List<IModelInstanceObject> modelInstanceObjects;

	
	private Utility utility = Utility.INSTANCE;

	private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;
	

	public void setOclInterpreter(OclInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public void setModelInstanceObjects(
			List<IModelInstanceObject> modelInstanceObjects) {
		this.modelInstanceObjects = modelInstanceObjects;
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



}
