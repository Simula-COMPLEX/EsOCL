package simula.standalone.analysis;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import tudresden.ocl20.pivot.essentialocl.expressions.OclExpression;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclBoolean;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;

public class BDC4BooleanOp {

	private Utility utility = Utility.INSTANCE;

	OclInterpreter interpreter;

	private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

	public BDC4BooleanOp(OclInterpreter interpreter) {
		super();
		this.interpreter = interpreter;
	}

	/**
	 * Handle the boolean expression like comparison expression
	 * 
	 * @param env
	 * @param opCallexp
	 * @return
	 */
	public double handleBooleanOp(IModelInstanceObject env,
			OperationCallExpImpl opCallexp) {
		this.interpreter.setEnviromentVariable("self", env);
		String opName = opCallexp.getReferredOperation().getName();
		EList<EObject> contents = opCallexp.eContents();
		OclExpression A_exp = (OclExpression) contents.get(0);
		OclExpression B_exp = (OclExpression) contents.get(1);
		return classifyValue(env, A_exp, B_exp, opName);
	}

	public double classifyValue(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp, String opName) {
		if (oclExpUtility.isBelongToOp(opName, OCLExpUtility.OP_COMPARE)) {
			BDC4CompareOp bdc4CompOp = new BDC4CompareOp(this.interpreter);
			return bdc4CompOp.handleCompareOp(env, A_exp, B_exp, opName);
		} else if (oclExpUtility.isBelongToOp(opName, OCLExpUtility.OP_BOOLEAN)) {
			OperationCallExpImpl A_opCallExp = (OperationCallExpImpl) A_exp;
			OperationCallExpImpl B_opCallExp = (OperationCallExpImpl) B_exp;
			if (opName.equals("not")) {
				return notOp(env, A_opCallExp);
			} else if (opName.equals("and")) {
				return andOp(env, A_opCallExp, B_opCallExp);
			} else if (opName.equals("or")) {
				return orOp(env, A_opCallExp, B_opCallExp);
			} else if (opName.equals("implies")) {
				return impliesOp(env, A_opCallExp, B_opCallExp);
			} else if (opName.equals("xor")) {
				return xorOp(env, A_opCallExp, B_opCallExp);
			}
		}
		return -1;
	}

	public double notOp(IModelInstanceObject env, OperationCallExpImpl A_exp) {
		OclAny A_Result = this.interpreter.doSwitch(A_exp);
		if (((OclBoolean) A_Result).isTrue()) {
			return handleBooleanOp(env, A_exp);
		} else
			return 0;
	}

	public double andOp(IModelInstanceObject env, OperationCallExpImpl A_exp,
			OperationCallExpImpl B_exp) {
		int numOfUndClauses = 0;
		double A_bdc = handleBooleanOp(env, A_exp);
		double B_bdc = handleBooleanOp(env, B_exp);
		return numOfUndClauses + utility.normalize(A_bdc + B_bdc);
	}

	public double orOp(IModelInstanceObject env, OperationCallExpImpl A_exp,
			OperationCallExpImpl B_exp) {
		int numOfUndClauses = 0;
		double A_bdc = handleBooleanOp(env, (OperationCallExpImpl) A_exp);
		double B_bdc = handleBooleanOp(env, (OperationCallExpImpl) B_exp);
		return numOfUndClauses + utility.normalize(Math.min(A_bdc, B_bdc));
	}

	public double impliesOp(IModelInstanceObject env,
			OperationCallExpImpl A_exp, OperationCallExpImpl B_exp) {
		int numOfUndClauses = 0;
		double notA_bdc = notOp(env, A_exp);
		double B_bdc = handleBooleanOp(env, B_exp);
		return numOfUndClauses + utility.normalize(Math.min(notA_bdc, B_bdc));
	}

	public double xorOp(IModelInstanceObject env, OperationCallExpImpl A_exp,
			OperationCallExpImpl B_exp) {
		int numOfUndClauses = 0;

		double A_bdc = handleBooleanOp(env, A_exp);
		double notA_bdc = notOp(env, A_exp);
		double B_bdc = handleBooleanOp(env, B_exp);
		double notB_bdc = notOp(env, B_exp);
		double A_and_notB_bdc = numOfUndClauses
				+ utility.normalize(A_bdc + notB_bdc);
		double notA_and_B_bdc = numOfUndClauses
				+ utility.normalize(notA_bdc + B_bdc);
		return numOfUndClauses
				+ utility.normalize(Math.min(A_and_notB_bdc, notA_and_B_bdc));
	}

}
