package tudresden.ocl20.pivot.standalone.analysis;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.PropertyCallExpImpl;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2PrimitiveType;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2Property;
import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.pivotmodel.Property;

public class Initialize {
	/** singleton instance */
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
