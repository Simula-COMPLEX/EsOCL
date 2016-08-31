package com.simula.esocl.distance;

import com.simula.esocl.oclga.Problem;
import com.simula.esocl.standalone.analysis.*;
import com.simula.esocl.standalone.analysis.OCLExpUtility;
import com.simula.esocl.standalone.modelinstance.RModelIns;
import com.simula.esocl.standalone.modelinstance.UMLNonAssPropIns;
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.interpreter.IInterpretationResult;
import org.dresdenocl.interpreter.IOclInterpreter;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.metamodels.uml2.internal.model.UML2Model;
import org.dresdenocl.modelinstance.IModelInstance;
import org.dresdenocl.modelinstancetype.exception.PropertyAccessException;
import org.dresdenocl.modelinstancetype.exception.PropertyNotFoundException;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.modelinstancetype.types.base.JavaModelInstanceInteger;
import org.dresdenocl.modelinstancetype.types.base.JavaModelInstanceString;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Expression;
import org.dresdenocl.pivotmodel.Property;
import org.eclipse.emf.ecore.EObject;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class OclUmlProblem implements Problem {

    int i = 0;
    int values[][];
    private UML2Model model;
    private List<Constraint> constraintList;

    public OclUmlProblem(UML2Model model, List<Constraint> constraintList) {

        this.model = model;
        this.constraintList = constraintList;


    }

    /**
     * Identify the different kind of expressions and calculate the distance
     *
     * @param exp       OCL expression
     * @param imiObject model instance
     * @param bdc
     */
    private static double classifyExp(Expression exp, IModelInstanceObject imiObject, BDCManager bdc) {
        EObject e = exp.eContents().get(0);
        if (e instanceof OperationCallExpImpl) {
            // Get the operator name
            String opName = ((OperationCallExpImpl) e).getReferredOperation().getName();
            // "includes", "excludes", "includesAll","excludesAll", "isEmpty"
            if (OCLExpUtility.INSTANCE.isBelongToOp(opName, OCLExpUtility.OP_CHECK)) {
                BDC4CheckOp bdc4CheckOp = new BDC4CheckOp(bdc.getInterpreter());
                return bdc4CheckOp.handleCheckOp(imiObject, (OperationCallExpImpl) e);
            } // end if
            // "=", "<>", "<", "<=", ">", ">=", "implies", "not", "and",
            // "or", "xor"
            else if (OCLExpUtility.INSTANCE.isBelongToOp(opName, OCLExpUtility.OP_COMPARE)
                    || OCLExpUtility.INSTANCE.isBelongToOp(opName, OCLExpUtility.OP_BOOLEAN)) {
                BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(bdc.getInterpreter());
                return bdc4BoolOp.handleBooleanOp(imiObject, (OperationCallExpImpl) e);
            } // end else if

        } else if (e instanceof IteratorExpImpl) {
            // "forAll", "exists", "isUnique", "one","select", "reject",
            // "collect"
            BDC4IterateOp bdc4IterOp = new BDC4IterateOp(bdc.getInterpreter());
            return bdc4IterOp.handleIteratorOp(imiObject, (IteratorExpImpl) e);
        }
        return -1;
    }

    @Override
    public void processProblem(File inputModel) {
        System.out.println("---Initial the property array---");
        UMLModelInsGenerator uma = UMLModelInsGenerator.INSTANCE;
        // obtain the property array
        UMLNonAssPropIns[] array_properties = uma.getProperties(inputModel
                .getAbsolutePath());
        int valuesOfConstraints[][] = new int[array_properties.length][3];

        for (int i = 0; i < array_properties.length; i++) {


            valuesOfConstraints[i][0] = array_properties[i].getMinValue();
            valuesOfConstraints[i][1] = array_properties[i].getMaxVlaue();
            valuesOfConstraints[i][2] = array_properties[i].getType();

          /*  System.out.println(array_properties[i].getName());
            System.out.println(valuesOfConstraints[i][0]);
            System.out.println(valuesOfConstraints[i][1]);
            System.out.println(valuesOfConstraints[i][2]);
            System.out.println("\n ---- \n");*/


        }

        this.values = valuesOfConstraints;

    }

    @Override
    public int[][] getConstraints() {
        return values;
    }

    @Override
    public double getFitness(int[] value) {


        try {

            // System.out.println("---Generate the concreate model instance---");
            IModelInstance modelInstance = new RModelIns(model,
                    UMLModelInsGenerator.INSTANCE.getModelInstance(value));
            List<IInterpretationResult> resultList = new LinkedList<IInterpretationResult>();

            // Create OCL Constraints Interpreter
            IOclInterpreter interpreter = new OclInterpreter(modelInstance);

            // Initial the calculator
            BDCManager bdc = BDCManager.INSTANCE;
            bdc.setOclInterpreter((OclInterpreter) interpreter);
            bdc.setModelInstanceObjects(modelInstance
                    .getAllModelInstanceObjects());

            //System.out.println("---Interpreter the input ocl constraint---");
            for (IModelInstanceObject imiObject : modelInstance.getAllModelInstanceObjects()) {


                for (Constraint constraint : constraintList) {
                    IInterpretationResult result = null;
                    try {


                        result = interpreter.interpretConstraint(constraint, imiObject);
                        if (result != null) {

/*
                            if (imiObject.getType().getOwnedProperty().size() > 0) {
                                printobject(imiObject);


                            }*/

                            resultList.add(result);
                            // Get the OCL expression
                            Expression exp = constraint.getSpecification();
                            // Classify the OCL expression


//                            if (((OclBoolean) result.getResult()).isTrue()) {

                   /*         System.out.println(result.getConstraint()
                                    .getSpecification().getBody()
                                    + ": " + result.getResult());

                            System.out.println(result.getModelObject().getName());

                            System.out.print((++i) + ":::");
                            for (int i = 0; i < value.length; i++) {
                                System.out.print(" " + value[i]);

                            }
                            System.out.println("\n\n");


                            System.out.println("---Caculate the fitness---");

                            System.out.println("\n\n");
*/


                            //            }


                            return classifyExp(exp, imiObject, bdc);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("\n\n");
        System.out.println();
        System.out.println();
        return -1;


    }


    private void printobject(IModelInstanceElement modelInstanceObject) throws PropertyAccessException, PropertyNotFoundException {


        System.out.println(modelInstanceObject.getType().getName());

        for (Property property : modelInstanceObject.getType().getOwnedProperty()) {

            IModelInstanceObject InstanceObject = (IModelInstanceObject) modelInstanceObject;
            if (property.getType().getName().equals("String")) {


                JavaModelInstanceString instanceString =
                        (JavaModelInstanceString) InstanceObject.getProperty(property);
                System.out.println("Property " + property.getName() + "=" + instanceString.getString());


            } else if (property.getType().getName().equals("Integer")) {

                JavaModelInstanceInteger instanceInteger =
                        (JavaModelInstanceInteger) InstanceObject.getProperty(property);

                Double result = null;
                if (InstanceObject != null) {
                    try {
                        result = instanceInteger.getDouble();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Property " + property.getName() + "=" + result);


            }


        }


    }

}


