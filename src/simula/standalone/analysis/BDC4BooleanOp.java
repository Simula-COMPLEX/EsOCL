package simula.standalone.analysis;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import simula.experiment.SearchEngineDriver;
import tudresden.ocl20.pivot.essentialocl.expressions.OclExpression;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.PropertyCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclBoolean;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.impl.EnumerationLiteralImpl;

public class BDC4BooleanOp {

	private Utility utility = Utility.INSTANCE;

	OclInterpreter interpreter;

	private int numOfUndClauses = 0;

	private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

	public BDC4BooleanOp(OclInterpreter interpreter) {
		super();
		this.interpreter = interpreter;
	}

	/**
	 * Handle the boolean expression and comparison expression
	 * 
	 * @param env
	 * @param exp
	 *            boolean expression
	 * @return
	 */
	public double handleBooleanOp(IModelInstanceObject env, OclExpression exp) {
		// Map the EnviromentVariable into the concrete model instance object
		this.interpreter.setEnviromentVariable("self", env);
		// this expression may be a property call expression
		if (exp instanceof PropertyCallExpImpl) {
			return simplePropOrMiscOp(env, exp);
		}
		// this expression may be a operation call expression
		else if (exp instanceof OperationCallExpImpl) {
			OperationCallExpImpl opCallexp = (OperationCallExpImpl) exp;
			// obtain the operation name
			String opName = opCallexp.getReferredOperation().getName();
			// handle the MISCELLANEOUS operation
			if (oclExpUtility.isBelongToOp(opName,
					oclExpUtility.OP_MISCELLANEOUS))
				return simplePropOrMiscOp(env, exp);
			else if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_CHECK)) {
				// "includes", "excludes", "includesAll","excludesAll",
				// "isEmpty"
				BDC4CheckOp bdc4CheckOp = new BDC4CheckOp(this.interpreter);
				return bdc4CheckOp.handleCheckOp(env,
						(OperationCallExpImpl) exp);
			}
			EList<EObject> contents = opCallexp.eContents();
			/**
			 * divide the whole expression into the left and right part
			 * according to the operator
			 * 
			 * e.g. A_exp op B_exp
			 */
			OclExpression A_exp = (OclExpression) contents.get(0);
			if (opName.equals("not")) {
				// the not operation does not have two parts, so the A_exp is
				// the only part of not expression
				return notOp(env, (OperationCallExpImpl) A_exp);
			}
			OclExpression B_exp = (OclExpression) contents.get(1);
			return classifyValue(env, A_exp, B_exp, opName);
		} else if (exp instanceof IteratorExpImpl) {
			// "forAll", "exists", "isUnique", "one"
			BDC4IterateOp bdc4IterOp = new BDC4IterateOp(this.interpreter);
			return bdc4IterOp.handleIteratorOp(env, (IteratorExpImpl) exp);
		}
		return -1;
	}

	/**
	 * classify the expression into the Comparison or Boolean type
	 * 
	 * @param env
	 * @param A_exp
	 *            leftpart of operator
	 * @param B_exp
	 *            rightpart of operator
	 * @param opName
	 * @return
	 */
	public double classifyValue(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp, String opName) {
		this.interpreter.setEnviromentVariable("self", env);
		if (oclExpUtility.isBelongToOp(opName, OCLExpUtility.OP_COMPARE)) {
			// "=", "<>", "<", "<=", ">", ">="
			BDC4CompareOp bdc4CompOp = new BDC4CompareOp(this.interpreter);
			if (SearchEngineDriver.boundValueStratergy == 1) {
				if (!(B_exp instanceof EnumerationLiteralImpl)) {
					opName = "=";
				}
			}
			return bdc4CompOp.handleCompareOp(env, A_exp, B_exp, opName);
		} else if (oclExpUtility.isBelongToOp(opName, OCLExpUtility.OP_BOOLEAN)) {
			this.numOfUndClauses = 0;
			// use the interpreter to interpret the two parts
			OclAny a_result = this.interpreter.doSwitch(A_exp);
			OclAny b_result = this.interpreter.doSwitch(B_exp);
			if (a_result.oclIsUndefined().isTrue())
				this.numOfUndClauses++;
			else if (b_result.oclIsUndefined().isTrue())
				this.numOfUndClauses++;
			// distance calculation for and, or, implies, xor expression
			if (opName.equals("and")) {
				// handle the condition of bound value
				if (SearchEngineDriver.boundValueStratergy == 1) {
					if (A_exp instanceof OperationCallExpImpl
							&& B_exp instanceof OperationCallExpImpl) {
						OperationCallExpImpl leftExp = (OperationCallExpImpl) A_exp;
						OperationCallExpImpl rightExp = (OperationCallExpImpl) B_exp;
						if (isBelongToOp(leftExp.getReferredOperation()
								.getName(), OCLExpUtility.OP_COMPARE)
								&& isBelongToOp(rightExp.getReferredOperation()
										.getName(), OCLExpUtility.OP_COMPARE)) {
							if (hasSameOpPart(leftExp, rightExp)) {
								return orOp(env, A_exp, B_exp);
							}
						}
					}

				}
				return andOp(env, A_exp, B_exp);
			} else if (opName.equals("or")) {
				return orOp(env, A_exp, B_exp);
			} else if (opName.equals("implies")) {
				return impliesOp(env, A_exp, B_exp);
			} else if (opName.equals("xor")) {
				return xorOp(env, A_exp, B_exp);
			}
		}
		return -1;
	}

	public double simplePropOrMiscOp(IModelInstanceObject env, OclExpression exp) {
		this.interpreter.setEnviromentVariable("self", env);
		OclAny result = this.interpreter.doSwitch(exp);
		if (result.oclIsUndefined().isTrue())
			return Utility.K;
		else if (((OclBoolean) result).isTrue())
			return 0;
		else
			// d(exp) >0 && d(exp) < k
			return 0.8;
	}

	public double notOp(IModelInstanceObject env, OclExpression A_exp) {
		OclAny A_Result = this.interpreter.doSwitch(A_exp);
		if (((OclBoolean) A_Result).isTrue()) {
			return handleBooleanOp(env, A_exp);
		} else
			return 0;
	}

	public double andOp(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp) {
		double A_bdc = handleBooleanOp(env, A_exp);
		double B_bdc = handleBooleanOp(env, B_exp);
		return numOfUndClauses + utility.normalize(A_bdc + B_bdc);
	}

	public double orOp(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp) {
		double A_bdc = handleBooleanOp(env, A_exp);
		double B_bdc = handleBooleanOp(env, B_exp);
		return numOfUndClauses + utility.normalize(Math.min(A_bdc, B_bdc));
	}

	public double impliesOp(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp) {
		double notA_bdc = notOp(env, A_exp);
		double B_bdc = handleBooleanOp(env, B_exp);
		return numOfUndClauses + utility.normalize(Math.min(notA_bdc, B_bdc));
	}

	public double xorOp(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp) {
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

	public boolean hasSameOpPart(OclExpression exp1, OclExpression exp2) {

		if (!(exp1 instanceof OperationCallExpImpl)) {
			return false;
		}
		if (!(exp2 instanceof OperationCallExpImpl)) {
			return false;
		}

		if (!isBelongToOp(((OperationCallExpImpl) exp1).getReferredOperation()
				.getName(), OCLExpUtility.OP_COMPARE)
				|| !isBelongToOp(((OperationCallExpImpl) exp2)
						.getReferredOperation().getName(),
						OCLExpUtility.OP_COMPARE)) {
			return false;
		}

		OclExpression exp11 = (OclExpression) exp1.eContents().get(0);
		OclExpression exp12 = (OclExpression) exp1.eContents().get(1);
		OclExpression exp21 = (OclExpression) exp2.eContents().get(0);
		OclExpression exp22 = (OclExpression) exp2.eContents().get(1);

		if (isSame(exp11, exp21)) {
			return true;
		}
		if (isSame(exp11, exp22)) {
			return true;
		}
		if (isSame(exp12, exp21)) {
			return true;
		}
		if (isSame(exp12, exp22)) {
			return true;
		}
		return false;

	}

	private boolean isSame(OclExpression exp1, OclExpression exp2) {

		EList<EObject> children1 = exp1.eContents();
		EList<EObject> children2 = exp2.eContents();

		if (children1.size() != children2.size()) {
			return false;
		}

		for (int i = 0; i < children1.size(); i++) {
			OclExpression child1 = (OclExpression) children1.get(i);
			OclExpression child2 = (OclExpression) children2.get(i);
			if (!child1.getName().equals(child2.getName())) {
				return false;
			}
			if (!isSame(child1, child2)) {
				return false;
			}
		}
		return true;
	}

	public boolean isBelongToOp(String opName, String[] ops) {
		for (int i = 0; i < ops.length; i++) {
			if (ops[i].equals(opName))
				return true;
		}
		return false;
	}

}
