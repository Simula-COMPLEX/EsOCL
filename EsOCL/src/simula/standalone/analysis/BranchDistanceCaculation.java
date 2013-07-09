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

public class BranchDistanceCaculation {

	OclInterpreter interpreter;

	List<IModelInstanceObject> modelInstanceObjects;

	/** singleton instance */
	private static BranchDistanceCaculation instance;

	/**
	 * Returns the single instance of the {@link StandaloneFacade}.
	 */
	public static BranchDistanceCaculation INSTANCE = instance();

	private Utility utility = Utility.INSTANCE;

	private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

	private static BranchDistanceCaculation instance() {

		if (instance == null) {
			instance = new BranchDistanceCaculation();
		}
		return instance;
	}

	public void setOclInterpreter(OclInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public void setModelInstanceObjects(
			List<IModelInstanceObject> modelInstanceObjects) {
		this.modelInstanceObjects = modelInstanceObjects;
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

	public Double handleCollectionEquality(IModelInstanceObject env,
			OperationCallExpImpl opCallexp) {
		this.interpreter.setEnviromentVariable("self", env);
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
			return 0.5 + 0.25 * utility.normalize(compareOp4Numeric(
					leftColSize, rightColSize, "="));
		} else {
			double temp = 0.0;
			for (int i = 0; i < leftColSize; i++) {
				double leftResult = Double.valueOf(leftInsCol.toArray()[i]
						.toString());
				double rightResult = Double.valueOf(rightInsCol.toArray()[i]
						.toString());
				temp += utility.normalize(compareOp4Numeric(leftResult,
						rightResult, "="));
			}
			return utility.formatValue(temp * 0.5 / leftColSize);
		}
	}

	/**
	 * Handle the boolean expression like comparison expression
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

	public double handleCompareOp(IModelInstanceObject env,
			OclExpression leftExp, OclExpression rightExp, String opName) {
		this.interpreter.setEnviromentVariable("self", env);
		OclAny left = this.interpreter.doSwitch(leftExp);
		OclAny right = this.interpreter.doSwitch(rightExp);
		Double leftResult = 0.0, rightResult = 0.0;
		leftResult = oclExpUtility.getResultNumericValue(left);
		rightResult = oclExpUtility.getResultNumericValue(right);
		if (left.oclIsUndefined().isTrue() || right.oclIsUndefined().isTrue()) {
			return Utility.K;
		}

		String complexType = oclExpUtility.isComplexType(leftExp);
		if (complexType != null
				&& complexType.equals(OCLExpUtility.OP_COMPLEX_SELECT_SIZE)) {
			return handleComplexSelectSizeOp(env,
					(OperationCallExpImpl) leftExp, leftResult, rightResult,
					opName);
		}

		return compareOp4Numeric(leftResult, rightResult, opName);
	}

	public double handleCheckOp(IModelInstanceObject env,
			OperationCallExpImpl opCallexp) {
		this.interpreter.setEnviromentVariable("self", env);
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
				ex_temp += utility.normalize(compareOp4Numeric(leftResult,
						rightResult, "<>"));
				// includes
				in_temp = utility.normalize(compareOp4Numeric(leftResult,
						rightResult, "="));
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
					double temp_j = utility.normalize(compareOp4Numeric(
							leftResult_i, rightResult_j, "="));
					if (minValue_i - temp_j < 0) {
						minValue_i = temp_j;
					}
					// excludesAll
					exAll_temp += utility.normalize(compareOp4Numeric(
							leftResult_i, rightResult_j, "<>"));
				}
				inAll_temp += minValue_i;
			}
			if (opName.equals("includesAll"))
				return inAll_temp;
			else
				return exAll_temp;
		} else if (opName.equals("isEmpty")) {
			return utility.normalize(compareOp4Numeric(leftColSize, 0, "="));
		} else if (opName.equals("notEmpty")) {
			return utility.normalize(compareOp4Numeric(leftColSize, 0, "<>"));
		}
		return -1.0;
	}

	public double handleIteratorOp(IModelInstanceObject env,
			IteratorExpImpl iteratorExp) {
		this.interpreter.setEnviromentVariable("self", env);
		String opName = iteratorExp.getName();
		EList<EObject> contents = iteratorExp.eContents();
		Collection<IModelInstanceElement> envCol = oclExpUtility
				.getResultCollection(this.interpreter.doSwitch(contents.get(0)));
		IModelInstanceElement[] envArray = (IModelInstanceElement[]) envCol
				.toArray();
		int envColsize = envCol.size();
		int[] input = new int[envColsize];
		for (int i = 0; i < envColsize; i++) {
			input[i] = i;
		}
		List<Variable> iterators = iteratorExp.getIterator();
		// handle the complex situation
		if (oclExpUtility.isComplexType(iteratorExp).equals(
				OCLExpUtility.OP_COMPLEX_SELECT_ITERATE)) {
			handleComplexSelectIterateOp(env, iteratorExp);
		}

		OperationCallExpImpl oce = (OperationCallExpImpl) contents.get(1);
		OperationCallExpImpl p1 = (OperationCallExpImpl) oce.eContents().get(0);
		OperationCallExpImpl p2 = (OperationCallExpImpl) oce.eContents().get(1);
		String oceOpName = oce.getReferredOperation().getName();
		if (opName.equals("forAll")) {
			if (oclExpUtility.isBelongToOp(oceOpName, OCLExpUtility.OP_COMPARE)) {
				return forAllOp(env, envArray, iterators, oce, null, oceOpName);
			} else if (oclExpUtility.isBelongToOp(opName,
					OCLExpUtility.OP_BOOLEAN)) {
				if (opName.equals("not")) {
					return forAllOp(env, envArray, iterators, p1, null,
							oceOpName);
				} else {
					return forAllOp(env, envArray, iterators, p1, p2, oceOpName);
				}
			}

		} else if (opName.equals("exists")) {
			if (oclExpUtility.isBelongToOp(oceOpName, OCLExpUtility.OP_COMPARE)) {
				return existsOp(env, envArray, iterators, oce, null, oceOpName);
			} else if (oclExpUtility.isBelongToOp(opName,
					OCLExpUtility.OP_BOOLEAN)) {
				if (opName.equals("not")) {
					return existsOp(env, envArray, iterators, p1, null,
							oceOpName);
				} else {
					return existsOp(env, envArray, iterators, p1, p2, oceOpName);
				}
			}
		} else if (opName.equals("isUnique")) {
			double temp = 0;
			int[][] envComb = Utility.INSTANCE.getArrangeOrCombine(input);
			for (int i = 0; i < envComb.length; i++) {
				this.interpreter.setEnviromentVariable(iterators.get(0)
						.getName(), envArray[envComb[i][0]]);
				double leftValue = oclExpUtility
						.getResultNumericValue(this.interpreter
								.doSwitch(contents.get(1)));
				this.interpreter.setEnviromentVariable(iterators.get(0)
						.getName(), envArray[envComb[i][1]]);
				double rightValue = oclExpUtility
						.getResultNumericValue(this.interpreter
								.doSwitch(contents.get(1)));
				temp += compareOp4Numeric(leftValue, rightValue, "<>");
			}
			return Utility.INSTANCE.formatValue(2 * temp
					/ (envColsize * (envColsize - 1)));
		} else if (opName.equals("one")) {
			int count = 0;
			OclAny result = this.interpreter.doSwitch(contents.get(1));
			if (((OclBoolean) result).isTrue())
				count++;
			return compareOp4Numeric(count, 1, "=");
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

	private double forAllOp(IModelInstanceObject env,
			IModelInstanceElement[] envArray, List<Variable> iterators,
			OperationCallExpImpl p1, OperationCallExpImpl p2, String opName) {
		int envColsize = envArray.length;
		int[] input = new int[envColsize];
		for (int i = 0; i < envColsize; i++) {
			input[i] = i;
		}
		int iteratorSize = iterators.size();
		if (envColsize == 0)
			return 0;
		else {
			double temp = 0;
			// 可重复组合
			int[][] envComb = utility.combInArrayDup(input, iteratorSize);
			for (int i = 0; i < envComb.length; i++) {
				for (int j = 0; j < iteratorSize; j++) {
					this.interpreter.setEnviromentVariable(iterators.get(j)
							.getName(), envArray[envComb[i][j]]);
				}
				temp += classifyValue(env, p1, p2, opName);
			}
			return utility.formatValue(temp / envComb.length);
		}
	}

	private double classifyValue(IModelInstanceObject env, OclExpression A_exp,
			OclExpression B_exp, String opName) {
		if (oclExpUtility.isBelongToOp(opName, OCLExpUtility.OP_COMPARE)) {
			return handleCompareOp(env, A_exp, B_exp, opName);
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

	private double existsOp(IModelInstanceObject env,
			IModelInstanceElement[] envArray, List<Variable> iterators,
			OperationCallExpImpl p1, OperationCallExpImpl p2, String opName) {
		int envColsize = envArray.length;
		int[] input = new int[envColsize];
		for (int i = 0; i < envColsize; i++) {
			input[i] = i;
		}
		int iteratorSize = iterators.size();
		double temp = 0, min_value = 1;
		// 可重复组合
		int[][] envComb = utility.combInArrayDup(input, iteratorSize);
		for (int i = 0; i < envComb.length; i++) {
			for (int j = 0; j < iteratorSize; j++) {
				this.interpreter.setEnviromentVariable(iterators.get(j)
						.getName(), envArray[envComb[i][j]]);
			}
			temp = classifyValue(env, p1, p2, opName);
			if (min_value - temp < 0) {
				min_value = temp;
			}
		}
		return min_value;
	}

	private double handleComplexSelectIterateOp(IModelInstanceObject env,
			IteratorExpImpl iteratorExp) {
		String opName = iteratorExp.getName();
		EObject selectExp = iteratorExp.eContents().get(0);
		Collection<IModelInstanceElement> envCol = oclExpUtility
				.getResultCollection(this.interpreter.doSwitch(selectExp
						.eContents().get(0)));
		IModelInstanceElement[] envArray = (IModelInstanceElement[]) envCol
				.toArray();
		List<Variable> iterators = ((IteratorExpImpl) selectExp).getIterator();
		iterators.addAll(iteratorExp.getIterator());
		OperationCallExpImpl iterate_p2 = (OperationCallExpImpl) iteratorExp
				.eContents().get(1);
		OperationCallExpImpl select_p1 = (OperationCallExpImpl) selectExp
				.eContents().get(1);
		if (opName.equals("forAll"))
			return forAllOp(env, envArray, iterators, select_p1, iterate_p2,
					"implies");
		else if (opName.equals("exists"))
			return existsOp(env, envArray, iterators, select_p1, iterate_p2,
					"and");
		return -1;
	}
}
