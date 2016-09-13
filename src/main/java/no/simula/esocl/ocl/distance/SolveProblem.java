package no.simula.esocl.ocl.distance;

import no.simula.esocl.oclga.EnAndDecoding;
import no.simula.esocl.oclga.GeneValueScope;
import no.simula.esocl.oclga.Problem;
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
import org.dresdenocl.modelinstancetype.exception.PropertyAccessException;
import org.dresdenocl.modelinstancetype.exception.PropertyNotFoundException;
import org.dresdenocl.modelinstancetype.types.IModelInstanceElement;
import org.dresdenocl.modelinstancetype.types.IModelInstanceObject;
import org.dresdenocl.modelinstancetype.types.base.JavaModelInstanceInteger;
import org.dresdenocl.modelinstancetype.types.base.JavaModelInstanceString;
import org.dresdenocl.pivotmodel.Constraint;
import org.dresdenocl.pivotmodel.Expression;
import org.dresdenocl.pivotmodel.Property;
import org.dresdenocl.standalone.facade.StandaloneFacade;
import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SolveProblem implements Problem {

    // after loading the model file, it is the parsed model
    IModel model;

    // after loading the ocl file, it is the parsed constraint
    Constraint constraint = null;

    // the array stores the attributes which should be delivered for searching process
    ValueElement4Search[] valueOfConstraints;

    // the list stores the attributes involved in the constraint
    List<ValueElement4Search> initialVesForSearchList;

    // the list is used for search algorithm
    ArrayList<GeneValueScope> geneValueScopeList;

    // it is usef for encoding and decoding values for the ValueElement4Search[] valueOfConstraints
    EnAndDecoding  encodingAndDecoding = new EnAndDecoding();

    // this variable indicates the time that invoked the getFitness function
    int i = 0;

    Logger logger = Logger.getLogger("bar");

    VESGenerator vesGenerator;

    UMLModelInsGenerator umlModelInsGenerator;

    ValueElement4Search[] OptimizedValueofAttributes;

    String[] optiValue;

    List<String> solutions = new ArrayList<>();

    public SolveProblem(String inputModelPath, String[] inputProfilePaths,
                        File inputOclConstraintsPath) {


        // PropertyConfigurator.configure("Eslog4j.properties");
        Utility.INSTANCE.initialUMLDoc(inputModelPath, inputProfilePaths);
        for (String inputProfileFilePath : inputProfilePaths) {
            logger.info("The profle file path:: " + inputProfileFilePath);
        }
        try {
            System.setProperty("DRESDENOCL_LOCATION_LOG4J",
                    new File("log4j.properties").getAbsolutePath());

            System.setProperty("DRESDENOCL_LOCATION_ECLIPSE",
                    new File("templates").getAbsolutePath() + File.separator);

            StandaloneFacade.INSTANCE.initialize();

            model = StandaloneFacade.INSTANCE.loadUMLModel(new File(
                            inputModelPath),
                    new File(getClass().getClassLoader().getResource("lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar").getFile()));


            // obtain the ocl constraints based on the model
            this.constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
                    model, inputOclConstraintsPath).get(0);


            Expression ex = constraint.getSpecification();

            this.vesGenerator = new VESGenerator(this.constraint);
            this.umlModelInsGenerator = new UMLModelInsGenerator(
                    this.vesGenerator);
            OCLExpUtility.INSTANCE.setVesGenerator(vesGenerator);
            new OclInterpreterPlugin();

            //add by luhong
            OCLExpUtility.INSTANCE.printOclClause4Depth(constraint);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public SolveProblem(String inputModelPath, String[] inputProfilePaths,
                        String inputOclConstraintsPath) {

        // PropertyConfigurator.configure("Eslog4j.properties");
        Utility.INSTANCE.initialUMLDoc(inputModelPath, inputProfilePaths);
        for (String inputProfileFilePath : inputProfilePaths) {
            logger.info("The profle file path:: " + inputProfileFilePath);
        }
        try {
            System.setProperty("DRESDENOCL_LOCATION_LOG4J",
                    new File("log4j.properties").getAbsolutePath());

            System.setProperty("DRESDENOCL_LOCATION_ECLIPSE",
                    new File("templates").getAbsolutePath() + File.separator);

            StandaloneFacade.INSTANCE.initialize();

            model = StandaloneFacade.INSTANCE.loadUMLModel(new File(
                            inputModelPath),
                    new File(getClass().getClassLoader().getResource("lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar").getFile()));


            // obtain the ocl constraints based on the model
            this.constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
                    model, inputOclConstraintsPath).get(0);


            Expression ex = constraint.getSpecification();

            this.vesGenerator = new VESGenerator(this.constraint);
            this.umlModelInsGenerator = new UMLModelInsGenerator(
                    this.vesGenerator);
            OCLExpUtility.INSTANCE.setVesGenerator(vesGenerator);
            new OclInterpreterPlugin();

            //add by luhong
            OCLExpUtility.INSTANCE.printOclClause4Depth(constraint);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Identify the different kind of expressions and calculate the distance
     *
     * @param exp       OCL expression
     * @param imiObject model instance
     * @param bdc
     */
    private static double classifyExp(Expression exp,
                                      IModelInstanceObject imiObject, BDCManager bdc) {
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

    private static File getUMLResources() {
        try {
            URL fileURL = ClassLoaderUtil
                    .getExtendResource("../lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar");
            return new File(fileURL.getPath());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

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

    /**
     * for kunming
     *
     * @param assgnedValue4Attribute
     * @param OptimizedValueofAttributes
     */
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

    public double getFitness(String[] valueStr) {
        try {
            this.optiValue = valueStr;
            // assign the values into the attribute and build the class objects
            List<UMLObjectIns> objects = this.umlModelInsGenerator
                    .getReAssignedUMLObjects(valueStr);
            // create the model instance for ocl interpreter
            IModelInstance modelInstance = new RModelIns(model, objects);
            OclInterpreter interpreter = new OclInterpreter(modelInstance);
            List<IInterpretationResult> resultList = new LinkedList<IInterpretationResult>();

            // Initial the calculator
            BDCManager bdc = new BDCManager();
            bdc.setOclInterpreter((OclInterpreter) interpreter);
            bdc.setModelInstanceObjects(modelInstance
                    .getAllModelInstanceObjects());
            System.out.println("---Calculate the fitness---");
            for (IModelInstanceObject imiObject : modelInstance
                    .getAllModelInstanceObjects()) {
                IInterpretationResult result = null;
                try {


                    // Interpret the OCL constraint
                    result = interpreter.interpretConstraint(this.constraint,
                            imiObject);
                    if (result != null) {

                        if (result.getResult() instanceof OclBoolean) {
                            if (((OclBoolean) result.getResult()).isTrue()) {
                                String solution = printobject(imiObject);
                                solutions.add(solution);
                            }

                        }


                        System.out.println(result.getConstraint()
                                .getSpecification().getBody()
                                + ": " + result.getResult());
                        resultList.add(result);
                        // Get the OCL expression
                        Expression exp = this.constraint.getSpecification();

                        // Classify the OCL expression
                        double distance = classifyExp(exp, imiObject, bdc);
                        System.out.println("the fitness: " + distance);
                        return distance;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
     * get all the attributes after the class is instanced based on "classInstanceValue"
     */
    public ValueElement4Search[] getAllAttributeConstraints() {
        System.err
                .println("*******************Build the concreate number of class instance*******************");

        for (ValueElement4Search initialVes : this.initialVesForSearchList) {
            if (!initialVes.getSourceClass().equals(
                    initialVes.getDestinationClass()))
                logger.info("the value of Association:: "
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
        // TODO Auto-generated method stub
        return this.geneValueScopeList;
    }

    @Override
    public String[] decoding(int v[]) {
        // TODO Auto-generated method stub

        return encodingAndDecoding.decoding(v, this.getConstraints());
    }

    @Override
    public List<String> getSolutions() {
        return solutions;
    }


    private String printobject(IModelInstanceElement modelInstanceObject) throws PropertyAccessException, PropertyNotFoundException {

        StringBuilder resultBuilder = new StringBuilder();
        System.out.println(modelInstanceObject.getType().getName());

        for (Property property : modelInstanceObject.getType().getOwnedProperty()) {

            IModelInstanceObject InstanceObject = (IModelInstanceObject) modelInstanceObject;
            if (property.getType().getName().equals("String")) {


                JavaModelInstanceString instanceString =
                        (JavaModelInstanceString) InstanceObject.getProperty(property);
                System.out.println("Property " + property.getName() + "=" + instanceString.getString());

                resultBuilder.append(property.getName());
                resultBuilder.append("=");
                resultBuilder.append(instanceString.getString());
                resultBuilder.append(" ,");

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
                resultBuilder.append(property.getName());
                resultBuilder.append("=");
                resultBuilder.append(result);
                resultBuilder.append(" ,");
            }


        }

        return resultBuilder.toString();
    }

}
