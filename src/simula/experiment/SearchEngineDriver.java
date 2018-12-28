package simula.experiment;

import java.io.*;

import simula.ocl.distance.DisplayResult;
import simula.ocl.distance.ValueElement4Search;
import simula.ocl.distance.SolveProblem;
import simula.oclga.*;
import simula.standalone.analysis.OCLExpUtility;
import simula.standalone.analysis.UMLModelInsGenerator;
import simula.standalone.modelinstance.UMLObjectIns;

import java.util.*;

public class SearchEngineDriver {

	String inputModelPath;

	String inputOclConstraintsPath;

	int searchAlgorithm = 0;

	public static int boundValueStratergy = 0;

	Search[] s = new Search[] { new simula.oclga.AVM(),
			new simula.oclga.SSGA(100, 0.75), new simula.oclga.OpOEA(),
			new simula.oclga.RandomSearch() };

	String[] b = new String[] { "simple", "boundValue" };

	public SearchEngineDriver(String inputModelPath,
			String inputOclConstraintsPath, int searchAlgorithm,
			int boundValueStratergy) {
		this.inputModelPath = inputModelPath;
		this.inputOclConstraintsPath = inputOclConstraintsPath;
		this.searchAlgorithm = searchAlgorithm;
		this.boundValueStratergy = boundValueStratergy;
	}

	public static void main(String s[]) throws Exception {

		SearchEngineDriver exp = new SearchEngineDriver(
				"resources/model/Test.uml",
				"resources/constraints/ocltest-1.ocl", 0, 1);
		exp.runSearch();

	}

	/**
	 * for kunming
	 * 
	 * @param assgnedValue4Attribute
	 * @param OptimizedValueofAttributes
	 * @return
	 */
	public boolean getOptimizedValueofAttributes(
			ValueElement4Search[] assgnedValue4Attribute,
			ValueElement4Search[] OptimizedValueofAttributes) {
		boolean isSolved = false;
		String[] inputProfilePaths = {};
		SolveProblem xp = new SolveProblem(this.inputModelPath,
				inputProfilePaths, this.inputOclConstraintsPath);
		xp.processProblem(assgnedValue4Attribute, OptimizedValueofAttributes);
		if (this.boundValueStratergy == 0) {
			isSolved = searchProcess(xp);
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
				isSolved = searchProcess(xp);
				OCLExpUtility.INSTANCE.restoreOriginalValue();
				DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
						.getUmlObjectInsList());
			}
		}
		xp.getAssignVlue();
		return isSolved;
	}

	public void runSearch() throws Exception {

		String[] inputProfilePaths = {};
		SolveProblem xp = new SolveProblem(this.inputModelPath,
				inputProfilePaths, this.inputOclConstraintsPath);
		xp.processProblem();
		if (this.boundValueStratergy == 0) {
			searchProcess(xp);
			// This class will store the final model instance
			DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
			DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
					.getUmlObjectInsList());
		} else {
			/**
			 * if we confirm the number of comparison expression, we can
			 * calculate the times for running the search process
			 */
			int iterateTime = OCLExpUtility.INSTANCE.buildIndexArray4Bound(xp
					.getConstraint());
			// it stores the type information of bound value for each comparison
			// expression
			DisplayResult.boundValueTypes = OCLExpUtility.INSTANCE
					.getTypeArray();
			DisplayResult.resultList = new ArrayList<List<UMLObjectIns>>();
			for (int i = 0; i < iterateTime; i++) {
				// modify the right part value of comparison expression
				OCLExpUtility.INSTANCE.generateBoundValue(i);
				searchProcess(xp);
				// restore the right part value of comparison expression
				OCLExpUtility.INSTANCE.restoreOriginalValue();
				DisplayResult.resultList.add(xp.getUmlModelInsGenerator()
						.getUmlObjectInsList());
			}

		}

	}

	public boolean searchProcess(SolveProblem xp) {
		Search sv = s[this.searchAlgorithm];
		sv.setMaxIterations(10000);
		Search.validateConstraints(xp);
		String[] value = sv.search(xp);
		// for (String string : value) {
		// System.out.print(string+" ");
		// }
		// System.out.println();
		boolean found = xp.getFitness(value) == 0d;
		int steps = sv.getIteration();
		System.out.println(sv.getShortName());
		System.out.println(found);
		System.out.println(steps);
		return found;
	}
}
