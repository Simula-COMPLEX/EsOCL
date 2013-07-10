package simula.standalone.analysis;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import tudresden.ocl20.pivot.essentialocl.expressions.Variable;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclBoolean;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceElement;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;

public class BDC4IterateOp {
	private Utility utility = Utility.INSTANCE;

	OclInterpreter interpreter;

	private OCLExpUtility oclExpUtility = OCLExpUtility.INSTANCE;

	public BDC4IterateOp(OclInterpreter interpreter) {
		this.interpreter = interpreter;
	}

	public double handleIteratorOp(IModelInstanceObject env,
			IteratorExpImpl iteratorExp) {
		this.interpreter.setEnviromentVariable("self", env);
		BDC4CompareOp bdc4CompOp = new BDC4CompareOp(this.interpreter);
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
				temp += bdc4CompOp.compareOp4Numeric(leftValue, rightValue,
						"<>");
			}
			return Utility.INSTANCE.formatValue(2 * temp
					/ (envColsize * (envColsize - 1)));
		} else if (opName.equals("one")) {
			int count = 0;
			OclAny result = this.interpreter.doSwitch(contents.get(1));
			if (((OclBoolean) result).isTrue())
				count++;
			return bdc4CompOp.compareOp4Numeric(count, 1, "=");
		}

		return -1;
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
				BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(this.interpreter);
				temp += bdc4BoolOp.classifyValue(env, p1, p2, opName);
			}
			return utility.formatValue(temp / envComb.length);
		}
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
		// Composition with replication
		int[][] envComb = utility.combInArrayDup(input, iteratorSize);
		for (int i = 0; i < envComb.length; i++) {
			for (int j = 0; j < iteratorSize; j++) {
				this.interpreter.setEnviromentVariable(iterators.get(j)
						.getName(), envArray[envComb[i][j]]);
			}
			BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(this.interpreter);
			temp = bdc4BoolOp.classifyValue(env, p1, p2, opName);
			if (min_value - temp < 0) {
				min_value = temp;
			}
		}
		return min_value;
	}
}
