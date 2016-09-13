package no.simula.esocl.standalone.modelinstance;

import org.dresdenocl.model.IModel;
import org.dresdenocl.modelinstance.base.AbstractModelInstance;
import org.dresdenocl.modelinstancetype.exception.*;
import org.dresdenocl.modelinstancetype.types.IModelInstanceCollection;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.pivotmodel.Operation;
import org.dresdenocl.pivotmodel.Property;

import java.util.List;

public class RModelIns extends AbstractModelInstance {

    private static int nameCounter = 0;

    public RModelIns(IModel model, List<UMLObjectIns> umis) {

		/* Initialize the instance. */
        this.myModel = model;

        this.myName = model.getDisplayName() + "_UMLInstance" + (++nameCounter);

        this.myModelInstanceFactory = new RModelInsFactory(
                this.myModel);
        addObjects(umis);

    }

    private void addObjects(List<UMLObjectIns> umis) {
        for (UMLObjectIns umi : umis) {
            try {
                this.addModelInstanceElement(umi);
            } catch (TypeNotFoundInModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public IModelInstanceElement addModelInstanceElement(Object object)
            throws TypeNotFoundInModelException {
        // TODO Auto-generated method stub
        if (object == null) {
            throw new IllegalArgumentException(
                    "Parameter 'object' must not be null.");
        }
        // no else.

        IModelInstanceElement result;

        result = this.myModelInstanceFactory.createModelInstanceElement((UMLObjectIns) object);

        if (result instanceof IModelInstanceObject) {
            this.addModelInstanceObject((IModelInstanceObject) result);
        }

        // no else.

        return result;
    }

    /**
     * <p>
     * Adds an already adapted {@link IModelInstanceObject} to this
     * {@link IModelInstance}.
     * </p>
     */
    protected void addModelInstanceObject(IModelInstanceObject imiObject) {

        this.myModelInstanceObjects.add(imiObject);
//		this.addAssociatedElementsAsWell(imiObject);
        this.initializeTypeMapping();
    }

    /**
     * <p>
     * A helper method that recursively adds the contained
     * {@link IModelInstanceElement}s if they are {@link IModelInstanceObject}s
     * and have not been added yet.
     * </p>
     *
     * @param collection The {@link IModelInstanceCollection} to be checked.
     */
    @SuppressWarnings("unchecked")
    private void addModelInstanceCollection(
            IModelInstanceCollection<IModelInstanceElement> collection) {

        for (IModelInstanceElement element : collection.getCollection()) {

            if (element instanceof IModelInstanceObject
                    && !this.myModelInstanceObjects
                    .contains((IModelInstanceObject) element)) {
                this.addModelInstanceObject((IModelInstanceObject) element);
            } else if (element instanceof IModelInstanceCollection<?>) {
                this.addModelInstanceCollection((IModelInstanceCollection<IModelInstanceElement>) element);
            }
        }
        // end for.
    }

    @Override
    public IModelInstanceElement getStaticProperty(Property arg0)
            throws PropertyAccessException, PropertyNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IModelInstanceElement invokeStaticOperation(Operation arg0,
                                                       List<IModelInstanceElement> arg1) throws OperationAccessException,
            OperationNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
