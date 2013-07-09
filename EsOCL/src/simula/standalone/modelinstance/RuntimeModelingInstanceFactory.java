package simula.standalone.modelinstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import tudresden.ocl20.pivot.essentialocl.types.CollectionType;
import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.model.ModelAccessException;
import tudresden.ocl20.pivot.modelinstancetype.exception.TypeNotFoundInModelException;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceBoolean;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceElement;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceEnumerationLiteral;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceInteger;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceReal;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceString;
import tudresden.ocl20.pivot.modelinstancetype.types.base.BasisJavaModelInstanceFactory;
import tudresden.ocl20.pivot.modelinstancetype.xml.internal.modelinstance.XmlModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.Enumeration;
import tudresden.ocl20.pivot.pivotmodel.EnumerationLiteral;
import tudresden.ocl20.pivot.pivotmodel.PrimitiveType;
import tudresden.ocl20.pivot.pivotmodel.Type;

public class RuntimeModelingInstanceFactory extends
		BasisJavaModelInstanceFactory {

	/**
	 * The {@link IModel} for whose {@link Type}s {@link IModelInstanceElement}s
	 * shall be created.
	 */
	private IModel model;

	private Map<Object, IModelInstanceObject> cacheModelInstanceObjects = new WeakHashMap<Object, IModelInstanceObject>();

	public RuntimeModelingInstanceFactory(IModel model) {

		if (model == null) {
			throw new IllegalArgumentException(
					"Parameter 'model' must not be null.");
		}
		// no else.

		this.model = model;
	}

	@Override
	public IModelInstanceElement createModelInstanceElement(Object adapted)
			throws TypeNotFoundInModelException {
		// TODO Auto-generated method stub
		IModelInstanceElement result = null;

		UMLObjectInstance auoi = (UMLObjectInstance) adapted;
		List<String> pathname = new ArrayList<String>();
		pathname.add(auoi.getName());
		Type type = null;
		try {
			type = this.model.findType(pathname);
		} catch (ModelAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = (IModelInstanceElement) this.createModelInstanceElement(auoi,
				type);

		return result;
	}

	private RuntimeModelInstanceObject createModelInstanceObject(
			AbstractUMLModelInstance auoi, Type type) {

		RuntimeModelInstanceObject result = new RuntimeModelInstanceObject(
				auoi, type, type, this);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.dresdenocl.modelbus.modelinstance.types.IModelInstanceFactory
	 * #createModelInstanceElement(java.lang.Object,
	 * org.dresdenocl.pivotmodel.Type)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IModelInstanceElement createModelInstanceElement(Object adapted,
			Type type) {

		IModelInstanceElement result = null;
		if (adapted == null || adapted instanceof AbstractUMLModelInstance) {

			Object temp = null;

			if (adapted != null) {
				temp = adapted;
			}

			/* Probably adapt a literal. */
			if (type instanceof Enumeration) {

				result = this.createModelInstanceEnumerationLiteral(
						(UMLPrimitivePropertyInstance) temp,
						(Enumeration) type);
			}

			/* Else probably adapt a primitive type. */
			else if (type instanceof PrimitiveType) {

				switch (((PrimitiveType) type).getKind()) {

				case BOOLEAN:
					result = this.createModelInstanceBoolean(
							(UMLPrimitivePropertyInstance) temp, type);
					break;

				case INTEGER:
					result = this.createModelInstanceInteger(
							(UMLPrimitivePropertyInstance) temp, type);
					break;

				case REAL:
					result = this.createModelInstanceReal(
							(UMLPrimitivePropertyInstance) temp, type);
					break;

				case STRING:
					result = this.createModelInstanceString(
							(UMLPrimitivePropertyInstance) temp, type);
					break;
				}
				// end select.
			}

			else {
				/* Probably use a cached result. */
				if (this.cacheModelInstanceObjects.containsKey(temp)) {
					result = this.cacheModelInstanceObjects.get(temp);
				}

				else {
					result = this.createModelInstanceObject(
							(AbstractUMLModelInstance) temp, type);

					/* Add the result to the cache. */
					this.cacheModelInstanceObjects.put(temp,
							(IModelInstanceObject) result);
				}
				// end else.
			}
		}

		if (type instanceof CollectionType && adapted instanceof Collection<?>) {
			result = BasisJavaModelInstanceFactory
					.createModelInstanceCollection(
							(Collection<IModelInstanceElement>) adapted,
							(CollectionType) type);
		}

		return result;
	}

	/**
	 * <p>
	 * Creates an {@link IModelInstanceBoolean} for a given {@link Node} and a
	 * given {@link Type}.
	 * 
	 * @param uppe
	 *            The {@link Node} that shall be adapted.
	 * @param type
	 *            The {@link Type} of the {@link IModelInstanceBoolean} in the
	 *            {@link IModel}.
	 * @return The created {@link IModelInstanceBoolean}.
	 */
	private IModelInstanceBoolean createModelInstanceBoolean(
			UMLPrimitivePropertyInstance uppe, Type type) {

		IModelInstanceBoolean result;

		/*
		 * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to boolean and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * 'true', 'false' or null in all other cases!
		 */
		if (uppe == null || uppe.getValue() == null) {
			result = super.createModelInstanceBoolean(null);
		}

		else if (uppe.getValue().trim().equalsIgnoreCase("true")) {
			result = super.createModelInstanceBoolean(true);
		}

		else if (uppe.getValue().trim().equalsIgnoreCase("false")) {
			result = super.createModelInstanceBoolean(false);
		}

		else {
			result = super.createModelInstanceBoolean(null);
		}

		return result;
	}

	/**
	 * <p>
	 * Creates an {@link IModelInstanceEnumerationLiteral} for the given
	 * {@link Node} and the given {@link Enumeration}.
	 * </p>
	 * 
	 * @param uppe
	 *            The {@link Node} for that an
	 *            {@link IModelInstanceEnumerationLiteral} shall be created.
	 * @param enumeration
	 *            The {@link Enumeration} type for that the
	 *            {@link IModelInstanceEnumerationLiteral} shall be created.
	 * @return The created {@link IModelInstanceEnumerationLiteral}.
	 */
	private IModelInstanceEnumerationLiteral createModelInstanceEnumerationLiteral(
			UMLPrimitivePropertyInstance uppe, Enumeration enumeration) {

		IModelInstanceEnumerationLiteral result;

		if (uppe == null || uppe.getValue() == null) {
			result = super.createModelInstanceEnumerationLiteral(null);
		}

		else {
			EnumerationLiteral literal;
			literal = null;

			/* Try to find a literal that matches to the node's value. */
			for (EnumerationLiteral aLiteral : enumeration.getOwnedLiteral()) {
				if (aLiteral.getName().equalsIgnoreCase(uppe.getValue().trim())) {
					literal = aLiteral;
					break;
				}
				// no else.
			}
			// end for.

			result = super.createModelInstanceEnumerationLiteral(literal);
		}

		return result;
	}

	/**
	 * <p>
	 * Creates an {@link IModelInstanceInteger} for a given {@link Node} and a
	 * given {@link Type}.
	 * 
	 * @param uppe
	 *            The {@link Node} that shall be adapted.
	 * @param type
	 *            The {@link Type} of the {@link IModelInstanceInteger} in the
	 *            {@link IModel}.
	 * @return The created {@link IModelInstanceInteger}.
	 */
	private IModelInstanceInteger createModelInstanceInteger(
			UMLPrimitivePropertyInstance uppe, Type type) {

		IModelInstanceInteger result;

		/*
		 * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to integer and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * '1' except of '1.23', or null in many cases!
		 */
		if (uppe == null || uppe.getValue() == null) {
			result = super.createModelInstanceInteger(null);
		}

		else {
			Long longValue;
			try {
				longValue = new Double(Double.parseDouble(uppe.getValue()))
						.longValue();
			}

			catch (NumberFormatException e) {
				longValue = null;
			}

			result = super.createModelInstanceInteger(longValue);
		}

		return result;
	}

	/**
	 * <p>
	 * Creates an {@link IModelInstanceReal} for a given {@link Node} and a
	 * given {@link Type}.
	 * 
	 * @param uppe
	 *            The {@link Node} that shall be adapted.
	 * @param type
	 *            The {@link Type} of the {@link IModelInstanceReal} in the
	 *            {@link IModel}.
	 * @return The created {@link IModelInstanceReal}.
	 */
	private IModelInstanceReal createModelInstanceReal(
			UMLPrimitivePropertyInstance uppe, Type type) {

		IModelInstanceReal result;

		/*
		 * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to real and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * '1' except of '1.0', or null in many cases!
		 */
		if (uppe == null || uppe.getValue() == null) {
			result = super.createModelInstanceReal(null);
		}

		else {
			Double doubleValue;

			try {
				doubleValue = new Double(Double.parseDouble(uppe.getValue()));
			}

			catch (NumberFormatException e) {
				doubleValue = null;
			}

			result = super.createModelInstanceReal(doubleValue);
		}

		return result;
	}

	/**
	 * <p>
	 * Creates an {@link IModelInstanceString} for a given {@link Node} and a
	 * given {@link Type}.
	 * 
	 * @param uppe
	 *            The {@link Node} that shall be adapted.
	 * @param type
	 *            The {@link Type} of the {@link IModelInstanceString} in the
	 *            {@link IModel}.
	 * @return The created {@link IModelInstanceString}.
	 */
	private IModelInstanceString createModelInstanceString(
			UMLPrimitivePropertyInstance uppe, Type type) {

		IModelInstanceString result;

		/*
		 * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to integer and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * 'truefalse' except of null!
		 */
		if (uppe == null || uppe.getValue() == null) {
			result = super.createModelInstanceString(null);
		}

		else {
			result = super.createModelInstanceString(uppe.getValue());
		}

		return result;
	}

}
