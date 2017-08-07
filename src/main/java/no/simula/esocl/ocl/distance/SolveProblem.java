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

import no.simula.esocl.oclga.EnAndDecoding;
import no.simula.esocl.oclga.GeneValueScope;
import no.simula.esocl.oclga.Problem;
import no.simula.esocl.solver.CommandLine;
import no.simula.esocl.standalone.analysis.*;
import no.simula.esocl.standalone.modelinstance.RModelIns;
import no.simula.esocl.standalone.modelinstance.UMLObjectIns;
import org.apache.log4j.Logger;
import org.dresdenocl.essentialocl.expressions.OclExpression;
import org.dresdenocl.essentialocl.expressions.impl.IteratorExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.LetExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.essentialocl.standardlibrary.OclBoolean;
import org.dresdenocl.interpreter.IInterpretationResult;
import org.dresdenocl.interpreter.OclInterpreterPlugin;
import org.dresdenocl.interpreter.internal.OclInterpreter;
import org.dresdenocl.model.IModel;
import org.dresdenocl.modelinstance.IModelInstance;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Expression;
import org.dresdenocl.standalone.facade.StandaloneFacade;
import org.dresdenocl.tools.template.exception.TemplateException;
import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class SolveProblem implements Problem {

    private static Logger logger = Logger.getLogger(SolveProblem.class);

    private IModel model;

    private Constraint constraint = null;

    private ValueElement4Search[] valueOfConstraints;

    private List<ValueElement4Search> initialVesForSearchList;

    private ArrayList<GeneValueScope> geneValueScopeList;

    private EnAndDecoding encodingAndDecoding = new EnAndDecoding();

    private VESGenerator vesGenerator;

    private UMLModelInsGenerator umlModelInsGenerator;

    private ValueElement4Search[] OptimizedValueofAttributes;

    private String[] optiValue;

    private List<String> solutions = new ArrayList<>();
    private List<IModelInstanceObject> instancesObjects = new ArrayList<>();

    public SolveProblem(File inputModelPath,
                        File inputOclConstraintsPath) {

        String[] inputProfilePaths = {};
        Utility.INSTANCE.initialUMLDoc(inputModelPath, inputProfilePaths);
        for (String inputProfileFilePath : inputProfilePaths) {
            logger.debug("The profile file path:: " + inputProfileFilePath);
        }
        try {

            initDresden();
            model = StandaloneFacade.INSTANCE.loadUMLModel(
                    inputModelPath, UMLRecourse());


            // obtain the ocl constraints based on the model
            this.constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
                    model, inputOclConstraintsPath).get(0);


            Expression ex = constraint.getSpecification();

            this.vesGenerator = new VESGenerator(this.constraint);
            this.umlModelInsGenerator = new UMLModelInsGenerator(
                    this.vesGenerator);
            OCLExpUtility.INSTANCE.setVesGenerator(vesGenerator);
            new OclInterpreterPlugin();

            OCLExpUtility.INSTANCE.printOclClause4Depth(constraint);

        } catch (Exception e) {
            logger.error(e);
        }
    }


    public SolveProblem(File inputModelPath, String inputOclConstraintsPath) {

        String[] inputProfilePaths = {};
        Utility.INSTANCE.initialUMLDoc(inputModelPath, inputProfilePaths);
        for (String inputProfileFilePath : inputProfilePaths) {
            logger.debug("The profile file path:: " + inputProfileFilePath);
        }
        try {

            initDresden();
            model = StandaloneFacade.INSTANCE.loadUMLModel(
                    inputModelPath, UMLRecourse());


            this.constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
                    model, inputOclConstraintsPath).get(0);


            Expression ex = constraint.getSpecification();

            this.vesGenerator = new VESGenerator(this.constraint);
            this.umlModelInsGenerator = new UMLModelInsGenerator(
                    this.vesGenerator);
            OCLExpUtility.INSTANCE.setVesGenerator(vesGenerator);
            new OclInterpreterPlugin();

            OCLExpUtility.INSTANCE.printOclClause4Depth(constraint);

        } catch (Exception e) {

            logger.error(e);
        }
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
        if (e instanceof LetExpImpl) {
            // if it is a let expression, we only handle the expression after the key word "in"
            e = e.eContents().get(0);
        }
        if (e instanceof OperationCallExpImpl || e instanceof IteratorExpImpl) {
            // we consider the expression as a expression which returns true or false
            BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(bdc.getInterpreter());
            return bdc4BoolOp.handleBooleanOp(imiObject, (OclExpression) e);
        }
        return -1;
    }

    public double getFitness(String[] solutionValues) {
        try {
            this.optiValue = solutionValues;

            umlModelInsGenerator.reAssignedUMLObjects(solutionValues);


            List<UMLObjectIns> objects = umlModelInsGenerator.getUmlObjectInsList();
            String solution = umlModelInsGenerator.getSolution();


            IModelInstance modelInstance = new RModelIns(model, objects);
            OclInterpreter interpreter = new OclInterpreter(modelInstance);

            // Initial the calculator
            BDCManager bdc = new BDCManager();
            bdc.setInterpreter(interpreter);
            bdc.setModelInstanceObjects(modelInstance.getAllModelInstanceObjects());

            logger.debug("---Calculate the fitness---");

            for (IModelInstanceObject imiObject : modelInstance.getAllModelInstanceObjects()) {

                try {


                    // Interpret the OCL constraint
                    IInterpretationResult result = interpreter.interpretConstraint(this.constraint,
                            imiObject);

                    boolean resultBool = false;

                    if (result != null) {

                        if (result.getResult() instanceof OclBoolean) {
                            if (((OclBoolean) result.getResult()).isTrue()) {
                                resultBool = true;

                                instancesObjects.add(imiObject);
                                solutions.add(solution);
                            }

                        }


                        logger.debug(result.getConstraint().getSpecification().getBody() + ": " + result.getResult());

                        // Get the OCL expression
                        Expression exp = this.constraint.getSpecification();

                        // Classify the OCL expression
                        double distance = classifyExp(exp, imiObject, bdc);
                        logger.debug("the fitness: " + distance);


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

    public Constraint getConstraint() {
        return constraint;
    }

    //4)	Generate the model instances and initial the attribute array
    public void processProblem() {

        // obtain the initial attribute array
        this.initialVesForSearchList = this.vesGenerator.buildInitialVes(model);

        // after fixing the association number, we can get the final attribute array
        this.valueOfConstraints = getAllAttributeConstraints();

        this.geneValueScopeList = encodingAndDecoding.encoding(this
                .getConstraints());
    }


    public void processProblem(ValueElement4Search[] assgnedValue4Attribute,
                               ValueElement4Search[] OptimizedValueofAttributes) {

        this.OptimizedValueofAttributes = OptimizedValueofAttributes;
        // obtain the initial attribute array
        this.initialVesForSearchList = this.vesGenerator.buildInitialVes(model);

        // assign the value
        for (ValueElement4Search ves : this.initialVesForSearchList) {
            for (ValueElement4Search assign_ves : assgnedValue4Attribute) {
                if (ves.getAttributeName()
                        .equals(assign_ves.getAttributeName())) {
                    ves.setValue(assign_ves.getValue());
                    ves.setMinValue(Integer.parseInt(assign_ves.getValue()));
                    ves.setMaxValue(Integer.parseInt(assign_ves.getValue()));
                }
            }
        }

        // after fixing the association number, we can get the final attribute array
        this.valueOfConstraints = getAllAttributeConstraints();

        this.geneValueScopeList = encodingAndDecoding.encoding(this
                .getConstraints());
    }

    public void getAssignVlue() {
        for (ValueElement4Search ves : this.OptimizedValueofAttributes) {
            List<Integer> result = new ArrayList<Integer>();
            for (int i = 0; i < this.valueOfConstraints.length; i++) {
                if (ves.getAttributeName().equals(
                        this.valueOfConstraints[i].getAttributeName()))
                    result.add(i);
            }
            ves.setValue(this.optiValue[result.get(0)]);
        }
    }


    /*
     * get all the attributes after the class is instanced based on "classInstanceValue"
     */
    private ValueElement4Search[] getAllAttributeConstraints() {
        logger.debug("*******************Build the concreate number of class instance*******************");

        for (ValueElement4Search initialVes : this.initialVesForSearchList) {
            if (!initialVes.getSourceClass().equals(
                    initialVes.getDestinationClass()))
                logger.debug("the value of Association:: "
                        + initialVes.getSourceClass() + "->"
                        + initialVes.getDestinationClass() + " number: "
                        + initialVes.getValue());
        }
        return this.umlModelInsGenerator.getVes4InsNumberArray();
    }

    public List<ValueElement4Search> getInitialVesList() {
        return initialVesForSearchList;
    }

    public ValueElement4Search[] getConstraints() {

        return this.valueOfConstraints;
    }

    public UMLModelInsGenerator getUmlModelInsGenerator() {
        return umlModelInsGenerator;
    }

    @Override
    public ArrayList<GeneValueScope> getGeneConstraints() {

        return this.geneValueScopeList;
    }

    @Override
    public String[] decoding(int v[]) {


        return encodingAndDecoding.decoding(v, this.getConstraints());
    }

    @Override
    public List<String> getSolutions() {
        return solutions;
    }

    @Override
    public List<IModelInstanceObject> getObjects() {
        return instancesObjects;
    }


    private void initDresden() throws TemplateException {
        System.setProperty("DRESDENOCL_LOCATION_LOG4J",
                new File("log4j.properties").getAbsolutePath());

        System.setProperty("DRESDENOCL_LOCATION_ECLIPSE",
                new File("templates").getAbsolutePath() + File.separator);

        StandaloneFacade.INSTANCE.initialize();

    }

    /**
     * return UML jar file use to parse UML model
     */

    private File UMLRecourse() {

        String UML_JAR = "org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar";
        URL url = getClass().getClassLoader().getResource("lib/" + UML_JAR);
        if (url != null) {
            File file = new File(url.getFile());
            if (file.exists()) {
                return file;
            } else {
                file = new File(getPath() + UML_JAR);
                if (file.exists()) {
                    return file;
                } else {
                    return new File(CommandLine.umlpath + UML_JAR);
                }
            }
        } else {
            File file = new File(getPath() + UML_JAR);
            if (file.exists()) {
                return file;
            } else {
                return new File(CommandLine.umlpath + UML_JAR);
            }
        }
    }

    /**
     * build the path of jar file File From class path
     */
    private String getPath() {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.lastIndexOf(File.separator) != path.length()) {
            try {
                path = path.substring(0, path.lastIndexOf(File.separator)) + File.separator;
            } catch (Exception e) {
                path = path.substring(0, path.lastIndexOf("/")) + File.separator;
            }
        } else {
            path = path.substring(0, path.length() - 2);
            try {
                path = path.substring(0, path.lastIndexOf(File.separator)) + File.separator;
            } catch (Exception e) {
                path = path.substring(0, path.lastIndexOf("/")) + File.separator;
            }
        }

        return path;

    }

}