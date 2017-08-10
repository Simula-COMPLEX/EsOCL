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

package no.simula.esocl.ocl.distance;

import no.simula.esocl.standalone.analysis.BDC4BooleanOp;
import no.simula.esocl.standalone.analysis.BDCManager;
import org.apache.log4j.Logger;
import org.dresdenocl.essentialocl.expressions.OclExpression;
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.LetExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclBoolean;
import org.dresdenocl.interpreter.IInterpretationResult;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.model.IModel;
import org.dresdenocl.modelinstance.IModelInstance;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Expression;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */

public class FitnessCalculator {

    private static Logger logger = Logger.getLogger(FitnessCalculator.class);

    /**
     * @param model         UML model
     * @param constraint    OCL constraint
     * @param modelInstance UML Instance
     * @return solution fitness
     */

    public static double getFitness(IModel model, Constraint constraint, IModelInstance modelInstance) {
        try {


            OclInterpreter interpreter = new OclInterpreter(modelInstance);

            // Initial the calculator
            BDCManager bdcManager = new BDCManager();
            bdcManager.setInterpreter(interpreter);
            bdcManager.setModelInstanceObjects(modelInstance.getAllModelInstanceObjects());

            logger.debug("");
            logger.debug("          *******************Calculate the fitness of solution *******************");

            for (IModelInstanceObject modelInstanceObject : modelInstance.getAllModelInstanceObjects()) {

                try {


                    // Interpret the OCL constraint
                    IInterpretationResult interpretationResult = interpreter.interpretConstraint(constraint, modelInstanceObject);

                    boolean resultBool = false;

                    if (interpretationResult != null) {

                        if (interpretationResult.getResult() instanceof OclBoolean) {
                            if (((OclBoolean) interpretationResult.getResult()).isTrue()) {
                                resultBool = true;
                            }
                        }


                        logger.debug(interpretationResult.getConstraint().getSpecification().getBody() + ": " + interpretationResult.getResult());

                        // Get the OCL expression
                        Expression exp = constraint.getSpecification();

                        // Classify the OCL expression
                        double distance = classifyExp(exp, modelInstanceObject, bdcManager);
                        logger.debug("fitness of Solution: " + distance);


                        if (!resultBool && distance == 0d) {
                            return -1;
                        }

                        return distance;
                    }
                } catch (Exception e) {
                    logger.error("Instance not interpreted");
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return -1;
    }


    /**
     * Identify the different kind of expressions and calculate the distance
     *
     * @param exp                 OCL expression
     * @param modelInstanceObject model instance
     * @param bdcManager          BDC Manager
     */
    private static double classifyExp(Expression exp, IModelInstanceObject modelInstanceObject, BDCManager bdcManager) {
        EObject e = exp.eContents().get(0);
        if (e instanceof LetExpImpl) {
            // if it is a let expression, we only handle the expression after the key word "in"
            e = e.eContents().get(0);
        }
        if (e instanceof OperationCallExpImpl || e instanceof IteratorExpImpl) {
            // we consider the expression as a expression which returns true or false
            BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(bdcManager.getInterpreter());
            return bdc4BoolOp.handleBooleanOp(modelInstanceObject, (OclExpression) e);
        }
        return -1;
    }


}
