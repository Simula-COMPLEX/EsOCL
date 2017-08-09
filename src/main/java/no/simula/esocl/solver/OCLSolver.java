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
import no.simula.esocl.ocl.distance.SearchRunner;
import no.simula.esocl.ocl.distance.SolveProblem;
import no.simula.esocl.oclga.*;
import no.simula.esocl.standalone.analysis.OCLExpUtility;
import no.simula.esocl.standalone.analysis.Utility;
import no.simula.esocl.standalone.modelinstance.RModelInsObject;
import org.dresdenocl.modelinstancetype.exception.PropertyAccessException;
import org.dresdenocl.modelinstancetype.exception.PropertyNotFoundException;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.modelinstancetype.types.base.*;
import org.dresdenocl.pivotmodel.EnumerationLiteral;
import org.dresdenocl.pivotmodel.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class OCLSolver {
    public static final int AVM = 0;
    public static final int SSGA = 1;
    public static final int OpOEA = 2;
    public static final int RandomSearch = 3;
    private static final Map<String, Integer> alogkeys = new HashMap<>();


    static {
        alogkeys.put("AVM", OCLSolver.AVM);
        alogkeys.put("SSGA", OCLSolver.SSGA);
        alogkeys.put("OpOEA", OCLSolver.OpOEA);
        alogkeys.put("RandomSearch", OCLSolver.RandomSearch);
    }


    public OCLSolver() {

        OCLExpUtility.INSTANCE = new OCLExpUtility();
        Utility.INSTANCE = new Utility();
        DisplayResult.boundValueTypes = null;
        DisplayResult.resultList = null;


    }

    /**
     * solveConstraint method takes path of UML model , OCL constraint, participant Algorithms and maximum number of
     * iterations and return {@link Result}
     *
     * @param inputModel       path of UML Model
     * @param constraint       ocl constraint
     * @param searchAlgorithms search Algorithms used to find solution {AVM , SSGA, OpOEA, RandomSearch;}
     * @param iterations       maximum number of iteration for each algorithm
     * @return {@link Result}
     */
    public Result solveConstraint(String inputModel, String constraint, String searchAlgorithms[], Integer iterations) throws Exception {
        return solveConstraint(inputModel, constraint, searchAlgorithmKeys(searchAlgorithms), iterations);
    }

    /**
     * solveConstraint method takes path of UML model , OCL constraint, participant Algorithms keys and maximum number of
     * iterations and return {@link Result}
     *
     * @param inputModel       path of UML Model
     * @param constraint       ocl constraint
     * @param searchAlgorithms keys search Algorithms used to find solution {AVM = 0, SSGA = 1, OpOEA = 2, RandomSearch = 3;}
     * @param iterations       maximum number of iteration for each algorithm
     * @return {@link Result}
     */

    public Result solveConstraint(String inputModel, String constraint, int searchAlgorithms[], int iterations) throws Exception {

        for (int alogokey : searchAlgorithms) {
            Result result = new OCLSolver().solveConstraint(inputModel, constraint, alogokey, iterations);
            if (result.getResult()) {
                return result;
            }
        }

        return new Result();

    }


    /**
     * solveConstraint method takes path of UML model , OCL constraint, participant Algorithms keys and maximum number of
     * iterations and return {@link Result}
     *
     * @param inputModel      path of UML Model
     * @param constraint      ocl constraint
     * @param searchAlgorithm key search Algorithm used to find solution {AVM = 0, SSGA = 1, OpOEA = 2, RandomSearch = 3;}
     * @param iterations      maximum number of iteration
     * @return {@link Result}
     */
    public Result solveConstraint(String inputModel, String constraint, int searchAlgorithm, Integer iterations) throws Exception {


        SolveProblem xp = new SolveProblem(new File(inputModel),
                constraint);
        return runSearch(xp, searchAlgorithm, iterations);
    }


    public Result solveConstraint(String inputModel, File constraint, String searchAlgorithm[], Integer iterations) throws Exception {
        return solveConstraint(inputModel, constraint, searchAlgorithmKeys(searchAlgorithm), iterations);
    }

    public Result solveConstraint(String inputModel, File constraint, int searchAlgorithm[], int iterations) throws Exception {


        for (int alogoKey : searchAlgorithm) {

            Result result = new OCLSolver().solveConstraint(inputModel, constraint, alogoKey, iterations);
            if (result.getResult()) {
                return result;
            }
        }


        return new Result();


    }

    public Result solveConstraint(String inputModel, File constraint, int searchAlgorithm, Integer iterations) throws Exception {


        SolveProblem problem = new SolveProblem(new File(inputModel), constraint);

        return runSearch(problem, searchAlgorithm, iterations);
    }


    private Result runSearch(SolveProblem problem, int searchAlgorithm, Integer iterations) throws Exception {
        Search search = null;
        switch (searchAlgorithm) {
            case 0: {
                search = new AVM();
                break;
            }

            case 1: {
                search = new SSGA(100, 0.75);
                break;
            }

            case 2: {
                search = new OpOEA();
                break;
            }
            case 3: {
                search = new RandomSearch();
                break;
            }

        }


        if (search != null) {
            search.setMaxIterations(iterations);
            return new SearchRunner().runSearch(problem, search);
        }

        return null;
    }


    public void printResults(Result result) {

        System.out.println();
        System.out.println("Constraint: " + result.getConstraint());
        System.out.println("Algo: " + result.getSearchAlgorithms());
        System.out.println("Result: " + result.getResult());
        System.out.println("Iterations:" + result.getIterations());


        System.out.println("\nModelInstance:");
        try {
            printObject(result.getFinalSolutionObject(), 0);
        } catch (PropertyAccessException | PropertyNotFoundException e) {
        }

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

    /*public boolean getOptimizedValueofAttributes(SolveProblem xp,
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
    }*/
}
