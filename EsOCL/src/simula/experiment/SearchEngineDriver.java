package simula.experiment;

import java.io.*;

import simula.oclga.*;
import java.util.*;

public class SearchEngineDriver {
	public static void main(String s[]) throws Exception {

		SearchEngineDriver exp = new SearchEngineDriver();
		exp.runSearch();

	}

	public void runSearch() throws Exception {

		String inputModelPath = "resources/model/royalsandloyals.uml";
		String inputOclConstraintsPath = "resources/constraints/test.ocl";
		// Search[] s = new Search[]{new simula.oclga.AVM(), new
		// simula.oclga.SSGA(100,0.75), new simula.oclga.OpOEA(), new
		// simula.oclga.RandomSearch()};
		Search[] s = new Search[] { new simula.oclga.AVM() };
		simula.ocl.distance.XiangProblem xp = new simula.ocl.distance.XiangProblem(
				inputModelPath, inputOclConstraintsPath);
		xp.processProblem();
		Search sv = s[0];
		sv.setMaxIterations(20000);
		Search.validateConstraints(xp);
		int[] v = sv.search(xp);
		boolean found = xp.getFitness(v) == 0d;
		int steps = sv.getIteration();
		System.out.println(sv.getShortName());
		System.out.println(found);
		System.out.println(steps);

	}

}
