package simula.standalone.modelinstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tudresden.ocl20.pivot.essentialocl.types.CollectionType;
import tudresden.ocl20.pivot.model.ModelAccessException;
import tudresden.ocl20.pivot.modelinstance.base.AbstractModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.exception.AsTypeCastException;
import tudresden.ocl20.pivot.modelinstancetype.exception.CopyForAtPreException;
import tudresden.ocl20.pivot.modelinstancetype.exception.OperationAccessException;
import tudresden.ocl20.pivot.modelinstancetype.exception.OperationNotFoundException;
import tudresden.ocl20.pivot.modelinstancetype.exception.PropertyAccessException;
import tudresden.ocl20.pivot.modelinstancetype.exception.PropertyNotFoundException;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceElement;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceFactory;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.modelinstancetype.types.base.AbstractModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.Enumeration;
import tudresden.ocl20.pivot.pivotmodel.Operation;
import tudresden.ocl20.pivot.pivotmodel.PrimitiveType;
import tudresden.ocl20.pivot.pivotmodel.Property;
import tudresden.ocl20.pivot.pivotmodel.Type;

public class RuntimeModelInstanceObject extends AbstractModelInstanceObject
		implements IModelInstanceObject {
	/** The XML {@link Node} adapted by this {@link XmlModelInstanceObject}. */
	protected AbstractUMLModelInstance aumi;

	/**
	 * The {@link IModelInstanceFactory} to adapt properties of this
	 * {@link XmlModelInstanceObject}.
	 */
	protected IModelInstanceFactory modelInstanceFactory;

	protected RuntimeModelInstanceObject(AbstractUMLModelInstance aumi,
			Type type, Type originalType, IModelInstanceFactory factory) {
		super(type, originalType);
		this.aumi = aumi;
		this.modelInstanceFactory = factory;
		if (aumi == null)
			this.myName = null;
		else
			this.myName = aumi.getName();
	}

	@Override
	public IModelInstanceElement asType(Type type) throws AsTypeCastException {
		// TODO Auto-generated method stub
		System.out.println("asType() ");
		if (type == null) {
			throw new IllegalArgumentException(
					"Parameter 'type' must not be null.");
		}
		// no else.

		IModelInstanceElement result;

		Set<Type> types;

		types = new HashSet<Type>();
		types.add(type);

		result = null;

		/* If the type can be casted in the model, cast it. */
		if (this.getOriginalType().conformsTo(type)) {
			result = new RuntimeModelInstanceObject(this.aumi, type,
					this.getOriginalType(), this.modelInstanceFactory);
		}
		// no else.
		// no else.

		return result;
	}

	@Override
	public IModelInstanceElement copyForAtPre() throws CopyForAtPreException {
		// TODO Auto-generated method stub
		System.out.println("copyForAtPre() ");
		IModelInstanceElement result;

		AbstractUMLModelInstance copiedNode = null;
		try {
			copiedNode = (AbstractUMLModelInstance) this.aumi.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result = new RuntimeModelInstanceObject(copiedNode, this.myType,
				this.getOriginalType(), this.modelInstanceFactory);

		return result;
	}

	@Override
	public Object getObject() {
		// TODO Auto-generated method stub
		return this.aumi;
	}

	@Override
	public boolean equals(Object object) {
		System.out.println("equals() ");
		boolean result;

		if (object == null) {
			result = false;
		}

		else if (this == object) {
			result = true;
		}

		else if (this.getClass() != object.getClass()) {
			result = false;
		}

		else {
			RuntimeModelInstanceObject other;
			other = (RuntimeModelInstanceObject) object;

			if (this.aumi == null) {
				if (other.aumi != null) {
					result = false;
				}

				else {
					result = true;
				}
			}

			else if (!this.aumi.equals(other.aumi)) {
				result = false;
			}

			else {
				result = true;
			}
		}

		return result;
	}

	@Override
	public IModelInstanceElement getProperty(Property property)
			throws PropertyAccessException, PropertyNotFoundException {
		// TODO Auto-generated method stub
		if (property == null) {
			throw new IllegalArgumentException(
					"Parameter 'property' must not be null.");
		}
		// no else.

		IModelInstanceElement result;
		result = null;

		if (!this.isUndefined()) {

			/* Probably handle non-multiple primitive properties. */
			if ((!(property.getType() instanceof CollectionType))
					&& (property.getType() instanceof PrimitiveType)
					|| property.getType() instanceof Enumeration) {

				UMLObjectInstance uoi = (UMLObjectInstance) this.aumi;

				AbstractUMLModelInstance property_aumi = (AbstractUMLModelInstance) uoi
						.getPropertyObject(property.getName());

				if (property_aumi != null) {
					result = AbstractModelInstance.adaptInvocationResult(
							property_aumi, property.getType(),
							this.modelInstanceFactory);
				}
				// no else.

				// end for.
			}
			// no else.

			if (result == null) {

				UMLObjectInstance uoi = (UMLObjectInstance) this.aumi;

				Object property_auois = uoi.getPropertyObject(property.getName());

				if (property_auois != null
						&& property.getType() instanceof CollectionType) {

					List<IModelInstanceElement> imiList = new ArrayList<IModelInstanceElement>();

					for (AbstractUMLModelInstance auoi : (List<AbstractUMLModelInstance>) property_auois) {
						if (auoi.getName().equalsIgnoreCase(
								((CollectionType) property.getType())
										.getElementType().getName())) {
							imiList.add(this.modelInstanceFactory
									.createModelInstanceElement(auoi,
											((CollectionType) property
													.getType())
													.getElementType()));
						}
					}
					// end for.

					result = AbstractModelInstance.adaptInvocationResult(
							imiList, property.getType(),
							this.modelInstanceFactory);
				} else {

					// end for (on nodes).
					if (property_auois != null) {
						result = AbstractModelInstance.adaptInvocationResult(
								(AbstractUMLModelInstance) property_auois,
								property.getType(), this.modelInstanceFactory);
					} else {
						result = AbstractModelInstance.adaptInvocationResult(
								null, property.getType(),
								this.modelInstanceFactory);
					}
				}
			}
			// no else.
		}
		// no else (undefined).

		return result;
	}

	@Override
	public IModelInstanceElement invokeOperation(Operation operation,
			List<IModelInstanceElement> args)
			throws OperationNotFoundException, OperationAccessException {
		// TODO Auto-generated method stub
		if (operation == null) {
			throw new IllegalArgumentException(
					"Parameter 'operation' must not be null.");
		}
		// no else.

		else if (args == null) {
			throw new IllegalArgumentException(
					"Parameter 'args' must not be null.");
		}
		// no else.

		throw new OperationNotFoundException("aaaaaaaaaaaaaaaaaaaaaaaa");
	}

	public boolean isUndefined() {

		return this.aumi == null;
	}

	public int hashCode() {

		final int prime = 31;

		int result;
		result = 42;

		result = prime * result;

		if (this.aumi != null) {
			result += this.aumi.hashCode();
		}
		// no else.

		return result;
	}

}