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

package no.simula.esocl.solver;

import no.simula.esocl.ocl.distance.DisplayResult;
import no.simula.esocl.ocl.distance.Result;
import no.simula.esocl.ocl.distance.SolveProblem;
import no.simula.esocl.ocl.distance.ValueElement4Search;
import no.simula.esocl.oclga.*;
import no.simula.esocl.standalone.analysis.OCLExpUtility;
import no.simula.esocl.standalone.analysis.Utility;
import no.simula.esocl.standalone.modelinstance.RModelInsObject;
import no.simula.esocl.standalone.modelinstance.UMLObjectIns;
import org.apache.log4j.Logger;
import org.dresdenocl.modelinstancetype.exception.PropertyAccessException;
import org.dresdenocl.modelinstancetype.exception.PropertyNotFoundException;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.modelinstancetype.types.base.*;
import org.dresdenocl.pivotmodel.EnumerationLiteral;
import org.dresdenocl.pivotmodel.Property;
import org.eclipse.osgi.framework.internal.reliablefile.ReliableFileInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class OCLSolver {
    private static int boundValueStratergy = 0;
    private static Logger logger = Logger.getLogger(OCLSolver.class);
    private String inputModelPath;
    private File inputOclConstraintsPath;
    private String constraint;
    private int searchAlgorithm = 0;
    private Integer iterations;

    public static int AVM = 0;
    public static int SSGA = 1;
    public static int OpOEA = 2;
    public static int RandomSearch = 3;

    private Map<String, Integer> alogkeys = new HashMap<>();


    private Search[] s = new Search[]{new AVM(),
            new SSGA(100, 0.75), new OpOEA(),
            new RandomSearch()};

    public OCLSolver() {

        OCLExpUtility.INSTANCE = new OCLExpUtility();
        Utility.INSTANCE = new Utility();
        DisplayResult.boundValueTypes = null;
        DisplayResult.resultList = null;

        alogkeys.put("AVM", OCLSolver.AVM);
        alogkeys.put("SSGA", OCLSolver.SSGA);
        alogkeys.put("OpOEA", OCLSolver.OpOEA);
        alogkeys.put("RandomSearch", OCLSolver.RandomSearch);

    }


    public Result solveConstraint(String inputModel, File constraint, String searchAlgorithm[], Integer iterations) throws Exception {
        return solveConstraint(inputModel, constraint, searchAlgorithmKeys(searchAlgorithm), iterations);
    }


    public Result solveConstraint(String inputModel, String constraint, String searchAlgorithm[], Integer iterations) throws Exception {
        return solveConstraint(inputModel, constraint, searchAlgorithmKeys(searchAlgorithm), iterations);
    }

    public Result solveConstraint(String inputModel, File constraint, int searchAlgorithm[], int iterations) throws Exception {


        for (int alogokey : searchAlgorithm) {
            OCLSolver searchEngineDriver = new OCLSolver();
            Result result = searchEngineDriver.solveConstraint(inputModel, constraint, alogokey, iterations);
            if (result.getResult()) {
                return result;
            }
        }

        Result result = new Result();
        result.setResult(false);
        return result;


    }

    public Result solveConstraint(String inputModel, String constraint, int searchAlgorithm[], int iterations) throws Exception {


        for (int alogokey : searchAlgorithm) {
            OCLSolver searchEngineDriver = new OCLSolver();
            Result result = searchEngineDriver.solveConstraint(inputModel, constraint, alogokey, iterations);
            if (result.getResult()) {
                return result;
            }
        }

        Result result = new Result();
        result.setResult(false);
        return result;


    }


    public Result solveConstraint(String inputModel, File constraint, int searchAlgorithm, Integer iterations) throws Exception {
        this.inputModelPath = inputModel;
        this.searchAlgorithm = searchAlgorithm;
        this.inputOclConstraintsPath = constraint;
        this.iterations = iterations;


        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(new ReliableFileInputStream(constraint), "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {

        }

        this.constraint = sb.toString();


        String[] inputProfilePaths = {};
        SolveProblem xp = new SolveProblem(new File(inputModelPath),
                inputProfilePaths, constraint);
        return runSearch(xp);
    }

    public Result solveConstraint(String inputModel, String constraint, int searchAlgorithm, Integer iterations) throws Exception {
        this.inputModelPath = inputModel;
        this.searchAlgorithm = searchAlgorithm;
        this.constraint = constraint;
        this.iterations = iterations;

        String[] inputProfilePaths = {};
        SolveProblem xp = new SolveProblem(new File(inputModelPath),
                inputProfilePaths, constraint);
        return runSearch(xp);
    }


    public Result runSearch(SolveProblem xp) throws Exception {
        Result result = null;

        xp.processProblem();
        if (this.boundValueStratergy == 0) {
            result = searchProcess(xp);
            // This class will store the final model instance
            DisplayResult.resultList = new ArrayList<>();
            DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                    .getUmlObjectInsList());
        } else {
            /**
             * if we confirm the number of comparison expression, we can calculate the times for
             * running the search process
             */
            int iterateTime = OCLExpUtility.INSTANCE.buildIndexArray4Bound(xp
                    .getConstraint());
            // it stores the type information of bound value for each comparison expression
            DisplayResult.boundValueTypes = OCLExpUtility.INSTANCE
                    .getTypeArray();
            DisplayResult.resultList = new ArrayList<>();


            List<Result> results = new ArrayList<>();
            for (int i = 0; i < iterateTime; i++) {
                // modify the right part value of comparison expression
                OCLExpUtility.INSTANCE.generateBoundValue(i);

                results.add(searchProcess(xp));

                // restore the right part value of comparison expression
                OCLExpUtility.INSTANCE.restoreOriginalValue();
                DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                        .getUmlObjectInsList());
            }


            for (Result tempResult : results) {
                if (tempResult.getResult()) {
                    result = tempResult;
                    break;
                }
            }


        }


        return result;
    }

    public Result searchProcess(SolveProblem xp) {
        Search sv = s[this.searchAlgorithm];
        sv.setMaxIterations(iterations);
        Search.validateConstraints(xp);
        String[] value = sv.search(xp);

        for (String str : value) {
            logger.debug(str);
        }

        boolean found = sv.getFitness() == 0d;


        if (!found) {
            found = xp.getFitness(value) == 0d;
        }

        int steps = sv.getIteration();


        Result result = new Result();
        result.setConstraint(constraint);
        result.setResult(found);
        result.setAlgo(sv.getShortName());
        result.setIternations(steps);
        result.setSolutions(xp.getSolutions());
        if (!result.getSolutions().isEmpty()) {
            result.setSolution(result.getSolutions().get(result.getSolutions().size() - 1));
        }


        result.setObjects(xp.getObjects());
        if (!result.getObjects().isEmpty()) {
            result.setObject(result.getObjects().get(result.getObjects().size() - 1));
        }


        return result;
    }


    public void printResults(Result result) {

        System.out.println();
        System.out.println("Constraint: " + result.getConstraint());
        System.out.println("Algo: " + result.getAlgo());
        System.out.println("Result: " + result.getResult());
        System.out.println("Iterations:" + result.getIternations());


        System.out.println("\nModelInstance:");
        try {
            printObject(result.getObject(), 0);
/*
            for (IModelInstanceElement element:result.getObjects()
                 ) {

                printObject(element);
            }*/

        } catch (PropertyAccessException | PropertyNotFoundException e) {
        }


     /*   if (result.getSolution() != null) {
            System.out.println("\nDetail Solution: \n");
            System.out.println(result.getSolution());
        }
*/

    }


    private void printObject(IModelInstanceElement modelInstanceObject, int depth) throws PropertyAccessException, PropertyNotFoundException {
        if (modelInstanceObject == null) return;


        IModelInstanceObject InstanceObject = (IModelInstanceObject) modelInstanceObject;


        System.out.println("\n" + depthPrint(depth) + "Class Name: " + modelInstanceObject.getType().getQualifiedName());


        for (Property property : modelInstanceObject.getType().getOwnedProperty()) {


            IModelInstanceElement instanceElement = InstanceObject.getProperty(property);


            if (instanceElement == null) {

                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName() + " ------  Attribute Value: " + InstanceObject.getProperty(property));

            } else if (instanceElement instanceof JavaModelInstanceString) {


                JavaModelInstanceString instanceString =
                        (JavaModelInstanceString) instanceElement;
                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName() + " ------  Attribute Value: " + instanceString.getString());


            } else if (instanceElement instanceof JavaModelInstanceInteger) {

                JavaModelInstanceInteger instanceInteger =
                        (JavaModelInstanceInteger) instanceElement;

                Double attributeValue = null;
                try {
                    attributeValue = instanceInteger.getDouble();
                } catch (NullPointerException e) {
                }

                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName() + " ------  Attribute Value: " + attributeValue);


            } else if (instanceElement instanceof JavaModelInstanceReal) {

                JavaModelInstanceReal modelInstanceReal =
                        (JavaModelInstanceReal) instanceElement;


                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName() + " ------  Attribute Value: " + modelInstanceReal.getDouble());


            } else if (instanceElement instanceof JavaModelInstanceBoolean) {

                JavaModelInstanceBoolean javaModelInstanceBoolean =
                        (JavaModelInstanceBoolean) instanceElement;


                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName() + " ------  Attribute Value: " + javaModelInstanceBoolean.getBoolean());


            } else if (instanceElement instanceof ModelInstanceEnumerationLiteral) {

                ModelInstanceEnumerationLiteral modelInstanceEnumerationLiteral = (ModelInstanceEnumerationLiteral) instanceElement;


                EnumerationLiteral enumerationLiteral = modelInstanceEnumerationLiteral.getLiteral();

                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Enumeration: " + property.getType().getQualifiedName() + " ------  Enumeration Literal: " + enumerationLiteral.getQualifiedName());


            } else if (instanceElement instanceof JavaModelInstanceCollection) {

                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName());

                JavaModelInstanceCollection javaModelInstanceCollection = (JavaModelInstanceCollection) InstanceObject.getProperty(property);

                for (Object object : javaModelInstanceCollection.getCollection()) {
                    if (object instanceof IModelInstanceElement) {


                        printObject((IModelInstanceElement) object, depth + 1);
                    }
                }


            } else if (instanceElement instanceof RModelInsObject) {



                RModelInsObject rModelInsObject = (RModelInsObject) instanceElement;

                System.out.println(depthPrint(depth) + "Attribute Name: " + property.getQualifiedName() + "  ------  Attribute Type:" + property.getType().getQualifiedName());
                printObject(rModelInsObject, depth + 1);



            } else {


                System.out.println(property);
                System.out.println(property.getType());
                System.out.println(property.getType().getQualifiedName());
                System.out.println(InstanceObject.getProperty(property));


                System.out.println("Unknown UML2 Property : " + property.getType().getQualifiedName());
            }


        }

        System.out.println();
    }


    private String depthPrint(int depth) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            stringBuilder.append("    ");
        }
        return stringBuilder.toString();

    }


    private int[] searchAlgorithmKeys(String searchAlgorithm[]) {

        int algos[] = new int[searchAlgorithm.length];
        for (int i = 0, searchAlgorithmLength = searchAlgorithm.length; i < searchAlgorithmLength; i++) {
            String algo = searchAlgorithm[i];
            algos[i] = alogkeys.get(algo);
        }
        return algos;
    }

    public boolean getOptimizedValueofAttributes(SolveProblem xp,
                                                 ValueElement4Search[] assgnedValue4Attribute,
                                                 ValueElement4Search[] OptimizedValueofAttributes) {
        boolean isSolved = false;
        String[] inputProfilePaths = {};
        xp.processProblem(assgnedValue4Attribute, OptimizedValueofAttributes);
        if (this.boundValueStratergy == 0) {
            isSolved = searchProcess(xp).getResult();
            DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
            DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                    .getUmlObjectInsList());
        } else {
            int iterateTime = OCLExpUtility.INSTANCE.buildIndexArray4Bound(xp
                    .getConstraint());
            DisplayResult.boundValueTypes = OCLExpUtility.INSTANCE
                    .getTypeArray();
            DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
            for (int i = 0; i < iterateTime; i++) {
                OCLExpUtility.INSTANCE.generateBoundValue(i);
                isSolved = searchProcess(xp).getResult();
                OCLExpUtility.INSTANCE.restoreOriginalValue();
                DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
                        .getUmlObjectInsList());
            }
        }
        xp.getAssignVlue();
        return isSolved;
    }
}
