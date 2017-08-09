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

import no.simula.esocl.oclga.EncodingDecoding;
import no.simula.esocl.oclga.GeneValueScope;
import no.simula.esocl.oclga.Problem;
import no.simula.esocl.solver.CommandLine;
import no.simula.esocl.standalone.analysis.OCLExpUtility;
import no.simula.esocl.standalone.analysis.UMLModelInsGenerator;
import no.simula.esocl.standalone.analysis.Utility;
import no.simula.esocl.standalone.analysis.VESGenerator;
import no.simula.esocl.standalone.modelinstance.RModelIns;
import no.simula.esocl.standalone.modelinstance.UMLObjectIns;
import org.apache.log4j.Logger;
import org.dresdenocl.interpreter.OclInterpreterPlugin;
import org.dresdenocl.model.IModel;
import org.dresdenocl.modelinstance.IModelInstance;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.standalone.facade.StandaloneFacade;
import org.dresdenocl.tools.template.exception.TemplateException;

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
    private String UML_JAR = "org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar";
    private static Logger logger = Logger.getLogger(SolveProblem.class);

    private IModel model;

    private Constraint constraint = null;

    private ValueElement4Search[] valueOfConstraints;

    private List<ValueElement4Search> initialVesForSearchList;

    private ArrayList<GeneValueScope> geneValueScopeList;

    private EncodingDecoding encodingAndDecoding = new EncodingDecoding();

    private VESGenerator vesGenerator;

    private UMLModelInsGenerator umlModelInsGenerator;



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

            File UMLRecourse = UMLRecourse();
            if (UMLRecourse == null || !UMLRecourse.exists()) {
                logger.error("Please add " + UML_JAR + " into resource");
                return;
            }


            model = StandaloneFacade.INSTANCE.loadUMLModel(
                    inputModelPath, UMLRecourse);


            // obtain the ocl constraints based on the model
            constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
                    model, inputOclConstraintsPath).get(0);


            vesGenerator = new VESGenerator(constraint);
            umlModelInsGenerator = new UMLModelInsGenerator(vesGenerator);
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

            File UMLRecourse = UMLRecourse();
            if (UMLRecourse == null || !UMLRecourse.exists()) {
                logger.error("Please add " + UML_JAR + " into resource");
                return;
            }


            model = StandaloneFacade.INSTANCE.loadUMLModel(
                    inputModelPath, UMLRecourse);


            constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
                    model, inputOclConstraintsPath).get(0);


            vesGenerator = new VESGenerator(this.constraint);
            umlModelInsGenerator = new UMLModelInsGenerator(vesGenerator);
            OCLExpUtility.INSTANCE.setVesGenerator(vesGenerator);
            new OclInterpreterPlugin();

            OCLExpUtility.INSTANCE.printOclClause4Depth(constraint);

        } catch (Exception e) {

            logger.error(e);
        }
    }


    /**
     * Initial the attribute array and Generate the model instances
     */
    public void processProblem() {

        // obtain the initial attribute array
        initialVesForSearchList = vesGenerator.buildInitialVes();

        // after fixing the association number, we can get the final attribute array
        valueOfConstraints = getAllAttributeConstraints();

        geneValueScopeList = encodingAndDecoding.encoding(getConstraintElements4Search());
    }


    /**
     * Interpret ocl constraint and return distance
     *
     * @param solutionValues values of variables
     */

    public double getFitness(String[] solutionValues) {

        umlModelInsGenerator.reAssignedUMLObjects(solutionValues);


        List<UMLObjectIns> objects = umlModelInsGenerator.getUmlObjectInsList();
        String solution = umlModelInsGenerator.getSolution();

        IModelInstance modelInstance = new RModelIns(model, objects);
        double distance = FitnessCalculator.getFitness(model, constraint, modelInstance);

        if (distance != -1) {
            instancesObjects.addAll(modelInstance.getAllModelInstanceObjects());
            solutions.add(solution);
        }
        return distance;
    }



    /*
     * get all the attributes after the class is instanced based on "classInstanceValue"
     */
    private ValueElement4Search[] getAllAttributeConstraints() {
        logger.debug("*******************Build the concreate number of class instance*******************");

        for (ValueElement4Search initialVes : initialVesForSearchList) {
            if (!initialVes.getSourceClass().equals(
                    initialVes.getDestinationClass()))
                logger.debug("the value of Association:: "
                        + initialVes.getSourceClass() + "->"
                        + initialVes.getDestinationClass() + " number: "
                        + initialVes.getValue());
        }
        return umlModelInsGenerator.getVes4InsNumberArray();
    }


    @Override
    public ValueElement4Search[] getConstraintElements4Search() {

        return valueOfConstraints;
    }



    @Override
    public ArrayList<GeneValueScope> getGeneConstraints() {

        return this.geneValueScopeList;
    }

    @Override
    public String[] decoding(int v[]) {

        return encodingAndDecoding.decoding(v, this.getConstraintElements4Search());
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

                    if (CommandLine.umlpath != null && !CommandLine.umlpath.isEmpty()) {
                        return new File(CommandLine.umlpath + UML_JAR);
                    } else {

                        url = getClass().getClassLoader().getResource(UML_JAR);
                        if (url != null) {
                            file = new File(url.getFile());
                            if (file.exists()) {
                                return file;
                            }
                        }
                    }
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
        return null;
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


    public Constraint getConstraint() {
        return constraint;
    }

    public UMLModelInsGenerator getUmlModelInsGenerator() {
        return umlModelInsGenerator;
    }
}