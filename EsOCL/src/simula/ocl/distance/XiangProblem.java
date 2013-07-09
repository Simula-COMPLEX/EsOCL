package simula.ocl.distance;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import simula.standalone.analysis.BranchDistanceCaculation;
import simula.standalone.analysis.OCLExpUtility;
import simula.standalone.analysis.UMLModelAnalysis;
import simula.standalone.modelinstance.RuntimeModelInstance;
import simula.standalone.modelinstance.UMLObjectInstance;
import simula.standalone.modelinstance.UMLPrimitivePropertyInstance;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.interpreter.IInterpretationResult;
import tudresden.ocl20.pivot.interpreter.IOclInterpreter;
import tudresden.ocl20.pivot.interpreter.OclInterpreterPlugin;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.model.ModelAccessException;
import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.parser.ParseException;
import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.pivotmodel.Expression;
import tudresden.ocl20.pivot.standalone.facade.StandaloneFacade;
import tudresden.ocl20.pivot.tools.template.exception.TemplateException;

public class XiangProblem implements simula.oclga.Problem {

	File inputModel;
	File inputOclConstraints;

	IModel model;
	List<Constraint> constraintList;

	int values[][];

	int i = 0;

	public XiangProblem(String inputModelPath, String inputOclConstraintsPath) {
		this.inputModel = new File(inputModelPath);
		this.inputOclConstraints = new File(inputOclConstraintsPath);
		try {
			StandaloneFacade.INSTANCE.initialize(new URL("file:"
					+ new File("log4j.properties").getAbsolutePath()));
			model = StandaloneFacade.INSTANCE.loadUMLModel(inputModel,
					getUMLResources());
			System.out.println("---Parse the input ocl constraint---");
			constraintList = StandaloneFacade.INSTANCE.parseOclConstraints(
					model, inputOclConstraints);
			new OclInterpreterPlugin();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			IModelInstanceObject imiObject, BranchDistanceCaculation bdc) {
		EObject e = exp.eContents().get(0);
		if (e instanceof OperationCallExpImpl) {
			// Get the operator name
			String opName = ((OperationCallExpImpl) e).getReferredOperation()
					.getName();
			// "includes", "excludes", "includesAll","excludesAll", "isEmpty"
			if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_CHECK)) {
				return bdc.handleCheckOp(imiObject, (OperationCallExpImpl) e);
			} // end if
				// "=", "<>", "<", "<=", ">", ">="
			else if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_COMPARE)) {
				return bdc.handleBooleanOp(imiObject, (OperationCallExpImpl) e);
			}// end else if
				// "forAll", "exists", "isUnique", "one","select", "reject",
				// "collect"
			else if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_ITERATE)
					|| OCLExpUtility.INSTANCE.isBelongToOp(opName,
							OCLExpUtility.OP_SELECT)) {
				return bdc.handleIteratorOp(imiObject, (IteratorExpImpl) e);
			}
		}
		return -1;
	}

	public void setValues(int[][] values) {
		this.values = values;

	}

	public void processProblem() {
		UMLModelAnalysis uma = UMLModelAnalysis.INSTANCE;
		UMLPrimitivePropertyInstance[] array_properties = uma
				.getProperties(inputModel.getAbsolutePath());
		int valuesOfConstraints[][] = new int[array_properties.length][3];

		for (int i = 0; i < array_properties.length; i++) {
			if (array_properties[i].getType() == 0) {
				valuesOfConstraints[i][0] = -100;
				valuesOfConstraints[i][1] = 100;
				valuesOfConstraints[i][2] = 0;
			} else if (array_properties[i].getType() == 1) {
				valuesOfConstraints[i][0] = 0;
				valuesOfConstraints[i][1] = 1;
				valuesOfConstraints[i][2] = 1;
			}
		}

		this.values = valuesOfConstraints;

	}

	public double getFitness(int[] value) {
		System.err.println((++i) + ":::" + value[0]);
		try {

			System.out.println("---Generate the model instance---");
			IModelInstance modelInstance = new RuntimeModelInstance(model,
					UMLModelAnalysis.INSTANCE.getModelInstance(value));
			List<IInterpretationResult> resultList = new LinkedList<IInterpretationResult>();

			// Create OCL Constraints Interpreter
			IOclInterpreter interpreter = new OclInterpreter(modelInstance);

			// Initial the calculator
			BranchDistanceCaculation bdc = BranchDistanceCaculation.INSTANCE;
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
							// set the "self" with current instance
							interpreter
									.setEnviromentVariable("self", imiObject);
							// Get the OCL expression
							Expression exp = constraint.getSpecification();
							// Classify the OCL expression
							return classifyExp(exp, imiObject, bdc);
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.err.println(constraint.getSpecification()
								.getBody() + "--------------" + e.getMessage());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
