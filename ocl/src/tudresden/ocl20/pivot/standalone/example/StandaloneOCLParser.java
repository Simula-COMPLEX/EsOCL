package tudresden.ocl20.pivot.standalone.example;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import tudresden.ocl20.pivot.essentialocl.expressions.impl.IteratorExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.OperationCallExpImpl;
import tudresden.ocl20.pivot.essentialocl.expressions.impl.PropertyCallExpImpl;
import tudresden.ocl20.pivot.examples.pml.PmlPackage;
import tudresden.ocl20.pivot.examples.simple.Person;
import tudresden.ocl20.pivot.examples.simple.Professor;
import tudresden.ocl20.pivot.examples.simple.Student;
import tudresden.ocl20.pivot.interpreter.IInterpretationResult;
import tudresden.ocl20.pivot.interpreter.IOclInterpreter;
import tudresden.ocl20.pivot.interpreter.OclInterpreterPlugin;
import tudresden.ocl20.pivot.interpreter.internal.OclInterpreter;
import tudresden.ocl20.pivot.model.IModel;
import tudresden.ocl20.pivot.model.ModelAccessException;
import tudresden.ocl20.pivot.modelinstance.IModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.java.internal.modelinstance.JavaModelInstance;
import tudresden.ocl20.pivot.modelinstancetype.types.IModelInstanceObject;
import tudresden.ocl20.pivot.pivotmodel.ConstrainableElement;
import tudresden.ocl20.pivot.pivotmodel.Constraint;
import tudresden.ocl20.pivot.pivotmodel.ConstraintKind;
import tudresden.ocl20.pivot.pivotmodel.Expression;
import tudresden.ocl20.pivot.pivotmodel.Feature;
import tudresden.ocl20.pivot.pivotmodel.NamedElement;
import tudresden.ocl20.pivot.pivotmodel.Namespace;
import tudresden.ocl20.pivot.pivotmodel.Property;
import tudresden.ocl20.pivot.pivotmodel.Type;
import tudresden.ocl20.pivot.standalone.analysis.BranchDistanceCaculation;
import tudresden.ocl20.pivot.standalone.analysis.Initialize;
import tudresden.ocl20.pivot.standalone.analysis.OCLExpUtility;
import tudresden.ocl20.pivot.standalone.analysis.UMLModelAnalysis;
import tudresden.ocl20.pivot.standalone.analysis.Utility;
import tudresden.ocl20.pivot.standalone.facade.StandaloneFacade;
import tudresden.ocl20.pivot.tools.codegen.declarativ.IOcl2DeclSettings;
import tudresden.ocl20.pivot.tools.codegen.declarativ.Ocl2DeclCodeFactory;
import tudresden.ocl20.pivot.tools.codegen.declarativ.impl.Ocl2DeclSettings;
import tudresden.ocl20.pivot.tools.codegen.ocl2java.IOcl2JavaSettings;
import tudresden.ocl20.pivot.tools.codegen.ocl2java.Ocl2JavaFactory;
import tudresden.ocl20.pivot.tools.template.TemplatePlugin;

public class StandaloneOCLParser {

	final static File inputModel = new File(
			"resources/model/royalsandloyals.uml");
	final static File inputOclConstraints = new File(
			"resources/constraints/test.ocl");

	public static void main(String[] args) throws Exception {

		StandaloneFacade.INSTANCE.initialize(new URL("file:"
				+ new File("log4j.properties").getAbsolutePath()));

		run();

	}

	private static void run() {

		System.out.println("---Load the input model---");
		try {
			IModel model = StandaloneFacade.INSTANCE.loadUMLModel(inputModel,
					getUMLResources());

			System.out.println("---Generate the model instance---");
			IModelInstance modelInstance = new UMLModelAnalysis(inputModel)
					.getModelInstance(model);

			System.out.println("---Parse the input ocl constraint---");
			List<Constraint> constraintList = StandaloneFacade.INSTANCE
					.parseOclConstraints(model, inputOclConstraints);

			new OclInterpreterPlugin();

			List<IInterpretationResult> resultList = new LinkedList<IInterpretationResult>();
			
			//Create OCL Constraints Interpreter			
			IOclInterpreter interpreter = new OclInterpreter(modelInstance);
			
			//Initial the calculator
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
						//Interpret the OCL constraint
						result = interpreter.interpretConstraint(constraint,
								imiObject);
						if (result != null) {
							System.out.println(result.getConstraint()
									.getSpecification().getBody()
									+ ": " + result.getResult());
							//set the "self" with current instance
							interpreter
									.setEnviromentVariable("self", imiObject);
							//Get the OCL expression
							Expression exp = constraint.getSpecification();
							//Classify the OCL expression
							classifyExp(exp, imiObject, bdc);
							resultList.add(result);
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
	}

	/**
	 * Identify the different kind of expressions and calculate the distance
	 * @param exp OCL expression
	 * @param imiObject model instance
	 * @param bdc
	 */
	private static void classifyExp(Expression exp,
			IModelInstanceObject imiObject, BranchDistanceCaculation bdc) {
		EObject e = exp.eContents().get(0);
		if (e instanceof OperationCallExpImpl) {
			//Get the operator name
			String opName = ((OperationCallExpImpl) e).getReferredOperation()
					.getName();
			//"includes", "excludes", "includesAll","excludesAll", "isEmpty"
			if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_CHECK)) {
				bdc.handleCheckOp(imiObject, (OperationCallExpImpl) e);
			} // end if
			//"=", "<>", "<", "<=", ">", ">="
			else if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_COMPARE)) {
				bdc.handleBooleanOp(imiObject, (OperationCallExpImpl) e);
			}// end else if
			//"forAll", "exists", "isUnique", "one","select", "reject", "collect"
			else if (OCLExpUtility.INSTANCE.isBelongToOp(opName,
					OCLExpUtility.OP_ITERATE)
					|| OCLExpUtility.INSTANCE.isBelongToOp(opName,
							OCLExpUtility.OP_SELECT)) {
				bdc.handleIteratorOp(imiObject, (IteratorExpImpl) e);
			}
		}
	}

	private static File getUMLResources() {
		return new File(
				"lib/org.eclipse.uml2.uml.resources_3.1.0.v201005031530.jar");
	}
}
