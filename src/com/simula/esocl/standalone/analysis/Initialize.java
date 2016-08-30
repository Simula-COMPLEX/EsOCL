package com.simula.esocl.standalone.analysis;

import org.dresdenocl.essentialocl.expressions.impl.PropertyCallExpImpl;
import org.dresdenocl.metamodels.uml2.internal.model.UML2PrimitiveType;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Property;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Property;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

public class Initialize {
    /**
     * singleton instance
     */
    private static Initialize instance;

    /**
     * Returns the single instance of the {@link StandaloneFacade}.
     */
    public static Initialize INSTANCE = instance();

    private static Initialize instance() {

        if (instance == null) {
            instance = new Initialize();
        }
        return instance;
    }

    public void getVariablesOfConstraint(Constraint constraint) {
        TreeIterator<EObject> it = constraint.getSpecification().eAllContents();
        while (it.hasNext()) {

            EObject e = (EObject) it.next();

            if (e instanceof PropertyCallExpImpl) {
                Property p = ((PropertyCallExpImpl) e).getReferredProperty();
                if ((p instanceof UML2Property)
                        && p.getType() instanceof UML2PrimitiveType) {
                    System.out.println(((UML2PrimitiveType) p.getType())
                            .getKind());
                }
            }
        }
    }
}
