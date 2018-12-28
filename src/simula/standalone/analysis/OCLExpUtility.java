package simula.standalone.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import scala.Array;
import tudresden.ocl20.pivot.essentialocl.expressions.OclExpression;
import tudresden.ocl20.pivot.essentialocl.expressions.VariableExp;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.BooleanLiteralExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IntegerLiteralExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IterateExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.PropertyCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.RealLiteralExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.StringLiteralExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.VariableExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.VariableImpl;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclBoolean;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclCollection;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2Class;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2Enumeration;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2PrimitiveType;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceElement;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.modelinstancetype.types.base.JavaModelInstanceBoolean;
import tudresden.ocl20.pivot.modelinstancetype.types.base.JavaModelInstanceInteger;
import tudresden.ocl20.pivot.modelinstancetype.types.base.JavaModelInstanceReal;
import tudresden.ocl20.pivot.modelinstancetype.types.base.ModelInstanceEnumerationLiteral;
import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.pivotmodel.EnumerationLiteral;
import tudresden.ocl20.pivot.pivotmodel.PrimitiveType;
import tudresden.ocl20.pivot.pivotmodel.Property;
import tudresden.ocl20.pivot.pivotmodel.Type;
import tudresden.ocl20.pivot.pivotmodel.impl.OperationImpl;
import tudresden.ocl20.pivot.standardlibrary.java.internal.library.JavaOclBoolean;
import tudresden.ocl20.pivot.standardlibrary.java.internal.library.JavaOclEnumLiteral;
import tudresden.ocl20.pivot.standardlibrary.java.internal.library.JavaOclInteger;

public class OCLExpUtility {

	public static String OP_COMPLEX_SELECT_SIZE = "op_complex_select_size";
	public static String OP_COMPLEX_SELECT_ITERATE = "op_complex_select_iterate";
	public static String[] OP_BOOLEAN = { "implies", "not", "and", "or", "xor" };
	public static String[] OP_COMPARE = { "=", "<>", "<", "<=", ">", ">=" };
	public static String[] OP_BOUND = { "<", "<=", ">", ">=" };
	public static String[] OP_ITERATE = { "forAll", "exists", "isUnique", "one" };
	public static String[] OP_CHECK = { "includes", "excludes", "includesAll",
			"excludesAll", "isEmpty" };
	public static String[] OP_SELECT = { "select", "reject", "collect" };
	public static String[] OP_MISCELLANEOUS = { "oclIsTypeOf", "oclIsKindOf",
			"oclIsNew", "oclIsUndefined", "oclIsInvalid" };
	/** singleton instance */
	private static OCLExpUtility instance;

	VESGenerator vesGenerator;

	String[][] typeArray;

	int[][] comb;

	Map<OperationCallExpImpl, String> oceMap;

	/**
	 * Returns the single instance of the {@link StandaloneFacade}.
	 */
	public static OCLExpUtility INSTANCE = instance();

	private static OCLExpUtility instance() {

		if (instance == null) {
			instance = new OCLExpUtility();
		}
		return instance;
	}

	public void setVesGenerator(VESGenerator vesGenerator) {
		this.vesGenerator = vesGenerator;
	}

	public String[][] getTypeArray() {
		return typeArray;
	}

	public Collection<IModelInstanceElement> getResultCollection(OclAny oclAny) {
		return ((OclCollection) oclAny).asOrderedSet()
				.getModelInstanceCollection().getCollection();
	}

	public double getResultNumericValue(IModelInstanceElement imiElement) {
		if (imiElement instanceof JavaModelInstanceInteger)
			return ((JavaModelInstanceInteger) imiElement).getDouble();
		else if (imiElement instanceof JavaModelInstanceReal) {
			return ((JavaModelInstanceReal) imiElement).getDouble();
		} else if (imiElement instanceof JavaModelInstanceBoolean) {
			if (((JavaModelInstanceBoolean) imiElement).getBoolean())
				return 1;
			else
				return 0;
		} else if (imiElement instanceof ModelInstanceEnumerationLiteral) {
			UML2Enumeration enumType = this.vesGenerator
					.getEnumeration(((ModelInstanceEnumerationLiteral) imiElement)
							.getType().getQualifiedName());
			for (EnumerationLiteral el : enumType.getOwnedLiteral()) {
				if (el.getName().equals(
						((ModelInstanceEnumerationLiteral) imiElement)
								.getLiteral().getName()))
					return enumType.getOwnedLiteral().indexOf(el);
			}
		}
		return -1;
	}

	private void printChild(EObject pObject, int depth) {
		EList<EObject> contents = pObject.eContents();
		if (contents.size() == 0)
			return;
		int now_depth = depth;
		for (EObject eObject : contents) {
			for (int i = 0; i < depth; i++) {
				System.out.print("***");
			}
			System.out.println(eObject.toString());
			printChild(eObject, ++now_depth);
		}

	}

	public void printOclClause4Depth(EObject e) {
		int depth = 0;
		for (EObject eObject : e.eContents()) {
			System.out.println(eObject.toString());
			printChild(eObject, ++depth);
			depth = 0;
		}
	}

	public String getOppositeOp(String opName) {
		if (opName.equals("=")) {
			return "<>";
		} else if (opName.equals("<>")) {
			return "=";
		} else if (opName.equals("<")) {
			return ">=";
		} else if (opName.equals("<=")) {
			return ">";
		} else if (opName.equals(">")) {
			return "<=";
		} else if (opName.equals(">=")) {
			return "<";
		}
		return null;
	}

	public String isComplexType(OclExpression exp) {
		if (exp instanceof OperationCallExpImpl) {
			if (((OperationCallExpImpl) exp).getReferredOperation().getName()
					.equals("size")) {
				EObject select_size_Exp = exp.eContents().get(0);
				if (select_size_Exp instanceof IteratorExpImpl) {
					String select_size_Exp_opName = ((IteratorExpImpl) select_size_Exp)
							.getName();
					if (isBelongToOp(select_size_Exp_opName, OP_SELECT)) {
						return OP_COMPLEX_SELECT_SIZE;
					}
				}
			}
		}
		if (exp instanceof IteratorExpImpl) {
			EObject select_iterate_Exp = ((IteratorExpImpl) exp).eContents()
					.get(0);
			if (select_iterate_Exp instanceof IteratorExpImpl) {
				String select_iterate_Exp_opName = ((IteratorExpImpl) select_iterate_Exp)
						.getName();
				if (isBelongToOp(select_iterate_Exp_opName, OP_SELECT)) {
					return OP_COMPLEX_SELECT_ITERATE;
				}// end if
			}// end if

		}
		return null;
	}

	public boolean isBelongToOp(String opName, String[] ops) {
		for (int i = 0; i < ops.length; i++) {
			if (ops[i].equals(opName))
				return true;
		}
		return false;
	}

	public int[] getASC4String(String str) {
		int[] asc = new int[str.length()];
		for (int i = 0; i < str.length(); i++) {
			asc[i] = (int) str.charAt(i);
		}
		return asc;

	}

	public List<PropertyCallExpImpl> getPropertyInCons(Constraint constraint) {
		// OCLExpUtility.INSTANCE.printOclClause4Depth(constraint.getSpecification());
		List<PropertyCallExpImpl> propCallList = new ArrayList<PropertyCallExpImpl>();
		TreeIterator<EObject> it = constraint.getSpecification().eAllContents();
		while (it.hasNext()) {
			EObject e = (EObject) it.next();
			if (e instanceof PropertyCallExpImpl) {
				if (((PropertyCallExpImpl) e).getSourceType() instanceof UML2Class) {
					boolean isExisted = false;
					for (PropertyCallExpImpl propertyCallExpImpl : propCallList) {
						PropertyCallExpImpl pce = (PropertyCallExpImpl) e;
						String sourceTypeName = propertyCallExpImpl
								.getSourceType().getQualifiedName();
						String propertyName = propertyCallExpImpl
								.getReferredProperty().getQualifiedName();
						if (pce.getSourceType().getQualifiedName()
								.equals(sourceTypeName)
								&& pce.getReferredProperty().getQualifiedName()
										.equals(propertyName)) {
							isExisted = true;
						}
					}
					if (!isExisted)
						propCallList.add((PropertyCallExpImpl) e);
				}

			}
		}
		return propCallList;
	}

	public int buildIndexArray4Bound(Constraint constraint) {
		this.oceMap = new HashMap<OperationCallExpImpl, String>();
		TreeIterator<EObject> it = constraint.getSpecification().eAllContents();
		int i = 0;
		EObject temp = null;
		while (it.hasNext()) {
			EObject e = (EObject) it.next();
			if (e instanceof OperationCallExpImpl) {
				OperationCallExpImpl oce = (OperationCallExpImpl) e;
				if (isBelongToOp(oce.getReferredOperation().getName(),
						this.OP_COMPARE)) {
					OclExpression rightExp = (OclExpression) oce.eContents()
							.get(1);
					if (rightExp instanceof IntegerLiteralExpImpl) {
						this.oceMap.put(
								oce,
								((IntegerLiteralExpImpl) rightExp)
										.getIntegerSymbol() + "");
						System.out
								.println("======================================");
						this.printOclClause4Depth(temp);
						oce.getReferredOperation().setName("=");
						System.out
								.println("======================================");
						this.printOclClause4Depth(temp);
					} else if (rightExp instanceof StringLiteralExpImpl) {
						this.oceMap.put(oce, ((StringLiteralExpImpl) rightExp)
								.getStringSymbol());
					} else if (rightExp instanceof RealLiteralExpImpl) {
						this.oceMap.put(oce,
								((RealLiteralExpImpl) rightExp).getRealSymbol()
										+ "");
					} else if (rightExp instanceof BooleanLiteralExpImpl) {
						this.oceMap.put(
								oce,
								((BooleanLiteralExpImpl) rightExp)
										.isBooleanSymbol() + "");
					}
				}
			}
		}
		this.typeArray = buildBoundTypeArray(this.oceMap.keySet());
		int[] input = Utility.INSTANCE.genIndexArray(3);
		this.comb = Utility.INSTANCE.combInArrayDup(input, this.oceMap.size());
		return this.comb.length;
	}

	public boolean hasSameOpPart(OclExpression exp1, OclExpression exp2) {

		if (!(exp1 instanceof OperationCallExpImpl)) {
			return false;
		}
		if (!(exp2 instanceof OperationCallExpImpl)) {
			return false;
		}

		if (!isBelongToOp(((OperationCallExpImpl) exp1).getReferredOperation()
				.getName(), this.OP_COMPARE)
				|| !isBelongToOp(((OperationCallExpImpl) exp2)
						.getReferredOperation().getName(), this.OP_COMPARE)) {
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

	public void generateBoundValue(int iterate_index) {
		OperationCallExpImpl[] oceArray = new OperationCallExpImpl[this.oceMap
				.keySet().size()];
		oceArray = this.oceMap.keySet().toArray(oceArray);
		for (int i = 0; i < oceArray.length; i++) {
			OperationCallExpImpl oce = oceArray[i];
			OclExpression rightExp = (OclExpression) oce.eContents().get(1);
			int index = this.comb[iterate_index][i];
			if (rightExp instanceof IntegerLiteralExpImpl) {
				IntegerLiteralExpImpl intExp = (IntegerLiteralExpImpl) rightExp;
				switch (index) {
				case 0:
					intExp.setIntegerSymbol(intExp.getIntegerSymbol() - 1);
					break;
				case 2:
					intExp.setIntegerSymbol(intExp.getIntegerSymbol() + 1);
					break;
				}
			} else if (rightExp instanceof StringLiteralExpImpl) {
				StringLiteralExpImpl stringExp = (StringLiteralExpImpl) rightExp;
				switch (index) {
				case 0:
					stringExp.setStringSymbol("");
					break;
				case 2:
					stringExp
							.setStringSymbol(stringExp.getStringSymbol() + "a");
					break;
				}

			} else if (rightExp instanceof RealLiteralExpImpl) {
				RealLiteralExpImpl realExp = (RealLiteralExpImpl) rightExp;
				String realValue = realExp.getRealSymbol() + "";
				int result_beforeDecimal = Integer.valueOf(realValue
						.split("\\.")[0]);
				int result_afterDecimal = (int) Utility.INSTANCE
						.formatRealValueWithoutZero(realValue.split("\\.")[1]);
				switch (index) {
				case 0:
					result_afterDecimal = result_afterDecimal - 1;
					break;
				case 2:
					result_afterDecimal = result_afterDecimal + 1;
					break;
				}
				realExp.setRealSymbol(Float.valueOf(result_beforeDecimal + "."
						+ result_afterDecimal));
			} else if (rightExp instanceof BooleanLiteralExpImpl) {
				BooleanLiteralExpImpl booleanExp = (BooleanLiteralExpImpl) rightExp;
				if (index == 0 || index == 2) {
					if (booleanExp.isBooleanSymbol())
						booleanExp.setBooleanSymbol(false);
					else
						booleanExp.setBooleanSymbol(true);
				}
			}
		}
	}

	public void restoreOriginalValue() {
		Set<OperationCallExpImpl> oceSet = this.oceMap.keySet();
		for (OperationCallExpImpl oce : oceSet) {
			String original = this.oceMap.get(oce);
			OclExpression rightExp = (OclExpression) oce.eContents().get(1);
			if (rightExp instanceof IntegerLiteralExpImpl) {
				((IntegerLiteralExpImpl) rightExp).setIntegerSymbol(Integer
						.valueOf(original));
			} else if (rightExp instanceof StringLiteralExpImpl) {
				((StringLiteralExpImpl) rightExp).setStringSymbol(original);
			} else if (rightExp instanceof RealLiteralExpImpl) {
				((RealLiteralExpImpl) rightExp).setRealSymbol(Float
						.valueOf(original));
			} else if (rightExp instanceof BooleanLiteralExpImpl) {
				((BooleanLiteralExpImpl) rightExp).setBooleanSymbol(Boolean
						.valueOf(original));
			}

		}
	}

	/**
	 * typeArray[i][0] value-1; typeArray[i][1] value; typeArray[i][2] value+1;
	 * 
	 * @param expList
	 * @return
	 */
	public String[][] buildBoundTypeArray(Set<OperationCallExpImpl> oceSet) {
		String[][] typeArray = new String[oceSet.size()][3];
		OperationCallExpImpl[] oceArray = new OperationCallExpImpl[oceSet
				.size()];
		oceArray = oceSet.toArray(oceArray);
		for (int i = 0; i < oceArray.length; i++) {
			OperationCallExpImpl oce = oceArray[i];
			String opName = oce.getReferredOperation().getName();
			if (opName.equals("=")) {
				typeArray[i][0] = "invalid";
				typeArray[i][1] = "valid";
				typeArray[i][2] = "invalid";
			} else if (opName.equals("<>")) {
				typeArray[i][0] = "valid";
				typeArray[i][1] = "invalid";
				typeArray[i][2] = "valid";
			} else if (opName.equals("<")) {
				typeArray[i][0] = "valid";
				typeArray[i][1] = "invalid";
				typeArray[i][2] = "invalid";
			} else if (opName.equals("<=")) {
				typeArray[i][0] = "valid";
				typeArray[i][1] = "valid";
				typeArray[i][2] = "invalid";
			} else if (opName.equals(">")) {
				typeArray[i][0] = "invalid";
				typeArray[i][1] = "invalid";
				typeArray[i][2] = "valid";
			} else if (opName.equals(">=")) {
				typeArray[i][0] = "invalid";
				typeArray[i][1] = "valid";
				typeArray[i][2] = "valid";
			}
			// oce.getReferredOperation().setName("=");
		}
		return typeArray;
	}
}
