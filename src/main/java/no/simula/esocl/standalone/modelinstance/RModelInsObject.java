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
import org.dresdenocl.modelinstance.base.AbstractModelInstance;
import org.dresdenocl.modelinstancetype.exception.*;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceFactory;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.modelinstancetype.types.base.AbstractModelInstanceObject;
import org.dresdenocl.pivotmodel.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class RModelInsObject extends AbstractModelInstanceObject implements
        IModelInstanceObject {

    private AbstUMLModelIns aumi;


    private IModelInstanceFactory modelInstanceFactory;

    public RModelInsObject(AbstUMLModelIns aumi, Type type,
                           Type originalType, IModelInstanceFactory factory) {
        super(type, originalType);
        this.aumi = aumi;
        this.modelInstanceFactory = factory;
        if (aumi == null)
            this.myName = null;
        else
            this.myName = aumi.getQualifiedName();
    }

    @Override
    public IModelInstanceElement asType(Type type) throws AsTypeCastException {

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
            result = new RModelInsObject(this.aumi, type,
                    this.getOriginalType(), this.modelInstanceFactory);
        }
        // no else.
        // no else.

        return result;
    }

    @Override
    public IModelInstanceElement copyForAtPre() throws CopyForAtPreException {

        System.out.println("copyForAtPre() ");
        IModelInstanceElement result;

        AbstUMLModelIns copiedNode = null;
        try {
            copiedNode = (AbstUMLModelIns) this.aumi.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        result = new RModelInsObject(copiedNode, this.myType,
                this.getOriginalType(), this.modelInstanceFactory);

        return result;
    }

    @Override
    public Object getObject() {

        return this.aumi;
    }

    @Override
    public boolean equals(Object object) {
        boolean result;
        if (object == null) {
            result = false;
        } else if (this == object) {
            result = true;
        } else if (this.getClass() != object.getClass()) {
            result = false;
        } else {
            RModelInsObject other;
            other = (RModelInsObject) object;

            if (this.aumi == null) {
                if (other.aumi != null) {
                    result = false;
                } else {
                    result = true;
                }
            } else if (!this.aumi.equals(other.aumi)) {
                result = false;
            } else {
                result = true;
            }
        }

        return result;
    }

    @Override
    public IModelInstanceElement getProperty(Property property)
            throws PropertyAccessException, PropertyNotFoundException {

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
                    && (property.getType() instanceof PrimitiveType || property
                    .getType() instanceof Enumeration)) {

                UMLObjectIns uoi = (UMLObjectIns) this.aumi;

                AbstUMLModelIns property_aumi = (AbstUMLModelIns) uoi
                        .getPropertyObject(property.getQualifiedName());

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

                UMLObjectIns uoi = (UMLObjectIns) this.aumi;

                Object property_auois = uoi.getPropertyObject(property
                        .getQualifiedName());

                if (property_auois != null
                        && property.getType() instanceof CollectionType) {

                    List<IModelInstanceElement> imiList = new ArrayList<IModelInstanceElement>();

                    for (AbstUMLModelIns auoi : (List<AbstUMLModelIns>) property_auois) {
                        if (auoi.getQualifiedName().equalsIgnoreCase(
                                ((CollectionType) property.getType())
                                        .getElementType().getQualifiedName())) {
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
                        AbstUMLModelIns auois = ((List<AbstUMLModelIns>) property_auois)
                                .get(0);
                        result = AbstractModelInstance.adaptInvocationResult(
                                auois, property.getType(),
                                this.modelInstanceFactory);
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

        throw new OperationNotFoundException("Operation Not Found Exception ");
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


    public AbstUMLModelIns getAumi() {
        return aumi;
    }

    public void setAumi(AbstUMLModelIns aumi) {
        this.aumi = aumi;
    }
}
