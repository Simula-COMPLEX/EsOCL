/* ****************************************************************************
 * Copyright (c) 2017 Simula Research Laboratory AS.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Shaukat Ali  shaukat@simula.no
 **************************************************************************** */

package no.simula.esocl.standalone.modelinstance;

import org.dresdenocl.essentialocl.types.CollectionType;
import org.dresdenocl.model.IModel;
import org.dresdenocl.model.ModelAccessException;
import org.dresdenocl.modelinstancetype.exception.TypeNotFoundInModelException;
import org.dresdenocl.modelinstancetype.types.*;
import org.dresdenocl.modelinstancetype.types.base.BasisJavaModelInstanceFactory;
import org.dresdenocl.pivotmodel.Enumeration;
import org.dresdenocl.pivotmodel.EnumerationLiteral;
import org.dresdenocl.pivotmodel.PrimitiveType;
import org.dresdenocl.pivotmodel.Type;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class RModelInsFactory extends BasisJavaModelInstanceFactory {

    /**
     * The {@link IModel} for whose {@link Type}s {@link IModelInstanceElement}s
     * shall be created.
     */
    private IModel model;

    private Map<Object, IModelInstanceObject> cacheModelInstanceObjects = new WeakHashMap<Object, IModelInstanceObject>();

    public RModelInsFactory(IModel model) {

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

        IModelInstanceElement result = null;

        UMLObjectIns auoi = (UMLObjectIns) adapted;
        Type type = null;
        try {
            type = this.model.findType(auoi.getQualifiedNameList());
        } catch (ModelAccessException e) {
            e.printStackTrace();
        }
        result = createModelInstanceElement(auoi, type);

        return result;
    }

    private RModelInsObject createModelInstanceObject(AbstUMLModelIns auoi,
                                                      Type type) {

        RModelInsObject result = new RModelInsObject(auoi, type, type, this);
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
        if (adapted == null || adapted instanceof AbstUMLModelIns) {

            Object temp = null;

            if (adapted != null) {
                temp = adapted;
            }

			/* Probably adapt a literal. */
            if (type instanceof Enumeration) {

                result = createModelInstanceEnumerationLiteral((UMLAttributeIns) temp, (Enumeration) type);
            } else if (type instanceof PrimitiveType) {

                switch (((PrimitiveType) type).getKind()) {

                    case BOOLEAN:
                        result = this.createModelInstanceBoolean(
                                (UMLAttributeIns) temp, type);
                        break;

                    case INTEGER:
                        result = this.createModelInstanceInteger(
                                (UMLAttributeIns) temp, type);
                        break;

                    case REAL:
                        result = this.createModelInstanceReal(
                                (UMLAttributeIns) temp, type);
                        break;

                    case STRING:
                        result = this.createModelInstanceString(
                                (UMLAttributeIns) temp, type);
                        break;
                }

            } else {
                /* Probably use a cached result. */
                if (cacheModelInstanceObjects.containsKey(temp)) {
                    result = cacheModelInstanceObjects.get(temp);
                } else {
                    result = this.createModelInstanceObject(
                            (AbstUMLModelIns) temp, type);
                    cacheModelInstanceObjects.put(temp,
                            (IModelInstanceObject) result);
                }
            }
        }

        if (type instanceof CollectionType && adapted instanceof Collection<?>) {
            result = BasisJavaModelInstanceFactory.createModelInstanceCollection(
                    (Collection<IModelInstanceElement>) adapted,
                    (CollectionType) type);
        }

        return result;
    }

    /**
     * <p>
     * Creates an {@link IModelInstanceBoolean} for a given {@link UMLAttributeIns} and a
     * given {@link Type}.
     *
     * @param uppe The {@link UMLAttributeIns} that shall be adapted.
     * @param type The {@link Type} of the {@link IModelInstanceBoolean} in the
     *             {@link IModel}.
     * @return The created {@link IModelInstanceBoolean}.
     */
    private IModelInstanceBoolean createModelInstanceBoolean(
            UMLAttributeIns uppe, Type type) {

        IModelInstanceBoolean result;

		/*
         * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to boolean and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * 'true', 'false' or null in all other cases!
		 */
        if (uppe == null || uppe.getValue() == null) {
            result = createModelInstanceBoolean(null);
        } else if (uppe.getValue().trim().equalsIgnoreCase("true")) {
            result = createModelInstanceBoolean(true);
        } else if (uppe.getValue().trim().equalsIgnoreCase("false")) {
            result = createModelInstanceBoolean(false);
        } else {
            result = createModelInstanceBoolean(null);
        }

        return result;
    }

    /**
     * <p>
     * Creates an {@link IModelInstanceEnumerationLiteral} for the given
     * {@link UMLAttributeIns} and the given {@link Enumeration}.
     * </p>
     *
     * @param uppe        The {@link UMLAttributeIns} for that an
     *                    {@link IModelInstanceEnumerationLiteral} shall be created.
     * @param enumeration The {@link Enumeration} type for that the
     *                    {@link IModelInstanceEnumerationLiteral} shall be created.
     * @return The created {@link IModelInstanceEnumerationLiteral}.
     */
    private IModelInstanceEnumerationLiteral createModelInstanceEnumerationLiteral(
            UMLAttributeIns uppe, Enumeration enumeration) {

        IModelInstanceEnumerationLiteral result;

        if (uppe == null || uppe.getValue() == null) {
            result = createModelInstanceEnumerationLiteral(null);
        } else {
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

            result = createModelInstanceEnumerationLiteral(literal);
        }

        return result;
    }

    /**
     * <p>
     * Creates an {@link IModelInstanceInteger} for a given {@link UMLAttributeIns} and a
     * given {@link Type}.
     *
     * @param uppe The {@link UMLAttributeIns} that shall be adapted.
     * @param type The {@link Type} of the {@link IModelInstanceInteger} in the
     *             {@link IModel}.
     * @return The created {@link IModelInstanceInteger}.
     */
    private IModelInstanceInteger createModelInstanceInteger(
            UMLAttributeIns uppe, Type type) {

        IModelInstanceInteger result;

		/*
         * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to integer and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * '1' except of '1.23', or null in many cases!
		 */
        if (uppe == null || uppe.getValue() == null) {
            result = createModelInstanceInteger(null);
        } else {
            Long longValue;
            try {
                longValue = new Double(Double.parseDouble(uppe.getValue()))
                        .longValue();
            } catch (NumberFormatException e) {
                longValue = null;
            }

            result = createModelInstanceInteger(longValue);
        }

        return result;
    }

    /**
     * <p>
     * Creates an {@link IModelInstanceReal} for a given {@link UMLAttributeIns} and a
     * given {@link Type}.
     *
     * @param uppe The {@link UMLAttributeIns} that shall be adapted.
     * @param type The {@link Type} of the {@link IModelInstanceReal} in the
     *             {@link IModel}.
     * @return The created {@link IModelInstanceReal}.
     */
    private IModelInstanceReal createModelInstanceReal(UMLAttributeIns uppe,
                                                       Type type) {

        IModelInstanceReal result;

		/*
         * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to real and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * '1' except of '1.0', or null in many cases!
		 */
        if (uppe == null || uppe.getValue() == null) {
            result = createModelInstanceReal(null);
        } else {
            Double doubleValue;

            try {
                doubleValue = Double.parseDouble(uppe.getValue());
            } catch (NumberFormatException e) {
                doubleValue = null;
            }

            result = createModelInstanceReal(doubleValue);
        }

        return result;
    }

    /**
     * <p>
     * Creates an {@link IModelInstanceString} for a given {@link UMLAttributeIns} and a
     * given {@link Type}.
     *
     * @param uppe The {@link UMLAttributeIns} that shall be adapted.
     * @param type The {@link Type} of the {@link IModelInstanceString} in the
     *             {@link IModel}.
     * @return The created {@link IModelInstanceString}.
     */
    private IModelInstanceString createModelInstanceString(
            UMLAttributeIns uppe, Type type) {

        IModelInstanceString result;

		/*
         * Use the java basis types here because the adaptation of a node would
		 * not help. If you adapt a node, cast it to integer and then to string,
		 * you have to alter the nodes' value to get the right result such as
		 * 'truefalse' except of null!
		 */
        if (uppe == null || uppe.getValue() == null) {
            result = createModelInstanceString(null);
        } else {
            result = createModelInstanceString(uppe.getValue());
        }

        return result;
    }

}
