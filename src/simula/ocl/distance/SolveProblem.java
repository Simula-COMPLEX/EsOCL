package simula.ocl.distance;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.UMLPackage;

import simula.oclga.EnAndDecoding;
import simula.oclga.GeneValueScope;
import simula.standalone.analysis.BDC4BooleanOp;
import simula.standalone.analysis.BDC4CheckOp;
import simula.standalone.analysis.BDC4IterateOp;
import simula.standalone.analysis.BDCManager;
import simula.standalone.analysis.ClassLoaderUtil;
import simula.standalone.analysis.ModelInsFileWriter;
import simula.standalone.analysis.OCLExpUtility;
import simula.standalone.analysis.UMLModelInsGenerator;
import simula.standalone.analysis.Utility;
import simula.standalone.analysis.VESGenerator;
import simula.standalone.modelinstance.RModelIns;
import simula.standalone.modelinstance.UMLAttributeIns;
import simula.standalone.modelinstance.UMLObjectIns;
import tudresden.ocl20.pivot.essentialocl.expressions.OclExpression;
import tudresden.ocl20.pivot.essentialocl.expressions.Variable;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.LetExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.standardlibrary.OclAny;
import tudresden.ocl20.pivot.interpreter.IInterpretationResult;
import tudresden.ocl20.pivot.interpreter.IOclInterpreter;
import tudresden.ocl20.pivot.interpreter.OclInterpreterPlugin;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.language.ocl.resource.ocl.Ocl22Parser;
import tudresden.ocl20.pivot.language.ocl.resource.ocl.mopp.OclResource;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2Class;
import tudresden.ocl20.pivot.metamodels.uml2.internal.model.UML2Package;
import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.pivotmodel.Expression;
import tudresden.ocl20.pivot.pivotmodel.Namespace;
import tudresden.ocl20.pivot.pivotmodel.Type;
import tudresden.ocl20.pivot.standalone.facade.StandaloneFacade;

public class SolveProblem implements simula.oclga.Problem {

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
	EnAndDecoding encodingAndDecoding;

	// this variable indicates the time that invoked the getFitness function
	int i = 0;

	Logger logger = Logger.getLogger("bar");

	VESGenerator vesGenerator;

	UMLModelInsGenerator umlModelInsGenerator;

	ValueElement4Search[] OptimizedValueofAttributes;

	String[] optiValue;

	public SolveProblem(String inputModelPath, String[] inputProfilePaths,
			String inputOclConstraintsPath) {
		encodingAndDecoding = new EnAndDecoding();
		// PropertyConfigurator.configure("Eslog4j.properties");
		Utility.INSTANCE.initialUMLDoc(inputModelPath, inputProfilePaths);
		for (String inputProfileFilePath : inputProfilePaths) {
			logger.info("The profle file path:: " + inputProfileFilePath);
		}
		try {
			URL fileURL = ClassLoaderUtil
					.getExtendResource("../log4j.properties");
			StandaloneFacade.INSTANCE.initialize(fileURL);
			// obtain the uml model from the .uml file
			model = StandaloneFacade.INSTANCE.loadUMLModel(new File(
					inputModelPath), getUMLResources());
			// obtain the ocl constraints based on the model
			this.constraint = StandaloneFacade.INSTANCE.parseOclConstraints(
					model, new File(inputOclConstraintsPath)).get(0);
			this.vesGenerator = new VESGenerator(this.constraint);
			this.umlModelInsGenerator = new UMLModelInsGenerator(
					this.vesGenerator);
			OCLExpUtility.INSTANCE.setVesGenerator(vesGenerator);
			new OclInterpreterPlugin();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Constraint getConstraint() {
		return constraint;
	}

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

	/**
	 * Identify the different kind of expressions and calculate the distance
	 * 
	 * @param exp
	 *            OCL expression
	 * @param imiObject
	 *            model instance
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

}
