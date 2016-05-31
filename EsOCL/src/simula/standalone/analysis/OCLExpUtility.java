package simula.standalone.analysis;

import org.dresdenocl.essentialocl.expressions.OclExpression;
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclAny;
import org.dresdenocl.essentialocl.standardlibrary.OclCollection;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.standardlibrary.java.internal.library.JavaOclBoolean;
import org.dresdenocl.standardlibrary.java.internal.library.JavaOclEnumLiteral;
import org.dresdenocl.standardlibrary.java.internal.library.JavaOclInteger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;

import java.util.Collection;

public class OCLExpUtility {

    public static String OP_COMPLEX_SELECT_SIZE = "op_complex_select_size";
    public static String OP_COMPLEX_SELECT_ITERATE = "op_complex_select_iterate";
    public static String[] OP_BOOLEAN = {"implies", "not", "and", "or", "xor"};
    public static String[] OP_COMPARE = {"=", "<>", "<", "<=", ">", ">="};
    public static String[] OP_ITERATE = {"forAll", "exists", "isUnique", "one"};
    public static String[] OP_CHECK = {"includes", "excludes", "includesAll",
            "excludesAll", "isEmpty"};
    public static String[] OP_SELECT = {"select", "reject", "collect"};
    /**
     * singleton instance
     */
    private static OCLExpUtility instance;

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

    public Collection<IModelInstanceElement> getResultCollection(OclAny oclAny) {
        return ((OclCollection) oclAny).asOrderedSet()
                .getModelInstanceCollection().getCollection();
    }

    public double getResultNumericValue(OclAny oclAny) {
        if (oclAny instanceof JavaOclInteger) {
            return Double.valueOf(oclAny.getModelInstanceElement().toString());
        } else if (oclAny instanceof JavaOclBoolean) {
            if (((JavaOclBoolean) oclAny).isTrue())
                return 1;
            else
                return 0;
        } else if (oclAny instanceof JavaOclEnumLiteral) {
            Enumeration enumType = UMLModelInsGenerator.INSTANCE
                    .getEnumeration(((JavaOclEnumLiteral) oclAny)
                            .getModelInstanceEnumerationLiteral().getType()
                            .getName());
            for (EnumerationLiteral el : enumType.getOwnedLiterals()) {
                if (el.getName().equals(
                        ((JavaOclEnumLiteral) oclAny)
                                .getModelInstanceEnumerationLiteral()
                                .getLiteral().getName()))
                    return enumType.getOwnedLiterals().indexOf(el);
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
}
