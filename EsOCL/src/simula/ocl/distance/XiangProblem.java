package simula.ocl.distance;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.eclipse.emf.ecore.EObject;

import simula.standalone.analysis.BDC4BooleanOp;
import simula.standalone.analysis.BDC4CheckOp;
import simula.standalone.analysis.BDC4IterateOp;
import simula.standalone.analysis.BDCManager;
import simula.standalone.analysis.OCLExpUtility;
import simula.standalone.analysis.UMLModelInsGenerator;

import simula.standalone.modelinstance.RModelIns;

import simula.standalone.modelinstance.UMLNonAssPropIns;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.interpreter.IInterpretationResult;
import tudresden.ocl20.pivot.interpreter.IOclInterpreter;
import tudresden.ocl20.pivot.interpreter.OclInterpreterPlugin;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.model.IModel;

import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;

import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.pivotmodel.Expression;
import tudresden.ocl20.pivot.standalone.facade.StandaloneFacade;

public class XiangProblem implements simula.oclga.Problem {

	// the input model
	File inputModel;

	// the input ocl constraints
	File inputOclConstraints;

	// after loading the model file, it is the loaded model
	IModel model;

	// the parsed ocl constraints
	List<Constraint> constraintList;

	int values[][];

	int i = 0;

	public XiangProblem(String inputModelPath, String inputOclConstraintsPath) {
		this.inputModel = new File(inputModelPath);
		this.inputOclConstraints = new File(inputOclConstraintsPath);
		try {
			StandaloneFacade.INSTANCE.initialize(new URL("file:"
					+ new File("log4j.properties").getAbsolutePath()));
			// obtain the uml model from the .uml file
			model = StandaloneFacade.INSTANCE.loadUMLModel(inputModel,
					getUMLResources());
			// obtain the ocl constraints based on the model
			System.out.println("---Parse the input ocl constraint---");
			constraintList = StandaloneFacade.INSTANCE.parseOclConstraints(
					model, inputOclConstraints);
			new OclInterpreterPlugin();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setValues(int[][] values) {
		this.values = values;

	}

	public void processProblem() {
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
		}

		this.values = valuesOfConstraints;

	}

	public double getFitness(int[] value) {
		System.err.print((++i) + ":::");
		for (int i = 0; i < value.length; i++) {
			System.err.print(" " + value[i]);

		}
		System.err.println();
		try {

			System.out.println("---Generate the concreate model instance---");
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

			System.out.println("---Interpreter the input ocl constraint---");
			for (IModelInstanceObject imiObject : modelInstance
					.getAllModelInstanceObjects()) {

				for (Constraint constraint : constraintList) {
					IInterpretationResult result = null;
					try {
						// Interpret the OCL constraint
						result = interpreter.interpretConstraint(constraint,
								imiObject);

						if (result != null) {
							System.out.println(result.getConstraint()
									.getSpecification().getBody()
									+ ": " + result.getResult());
							resultList.add(result);
							// Get the OCL expression
							Expression exp = constraint.getSpecification();
							// Classify the OCL expression
							System.out.println("---Caculate the fitness---");
							return classifyExp(exp, imiObject, bdc);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}

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
		if (e instanceof OperationCallExpImpl) {
			// Get the operator name
			String opName = ((OperationCallExpImpl) e).getReferredOperation()
					.getName();
			// "includes", "excludes", "includesAll","excludesAll", "isEmpty"
			if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_CHECK)) {
				BDC4CheckOp bdc4CheckOp = new BDC4CheckOp(bdc.getInterpreter());
				return bdc4CheckOp.handleCheckOp(imiObject,
						(OperationCallExpImpl) e);
			} // end if
				// "=", "<>", "<", "<=", ">", ">=", "implies", "not", "and",
				// "or", "xor"
			else if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_COMPARE)
					|| OCLExpUtility.INSTANCE.isBelongToOp(opName,
							OCLExpUtility.OP_BOOLEAN)) {
				BDC4BooleanOp bdc4BoolOp = new BDC4BooleanOp(
						bdc.getInterpreter());
				return bdc4BoolOp.handleBooleanOp(imiObject,
						(OperationCallExpImpl) e);
			}// end else if

		} else if (e instanceof IteratorExpImpl) {
			// "forAll", "exists", "isUnique", "one","select", "reject",
			// "collect"
			BDC4IterateOp bdc4IterOp = new BDC4IterateOp(bdc.getInterpreter());
			return bdc4IterOp.handleIteratorOp(imiObject, (IteratorExpImpl) e);
		}
		return -1;
	}

	public int[][] getConstraints() {
		return values;
	}

	private static File getUMLResources() {
		return new File(
				"lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar");
	}

}
