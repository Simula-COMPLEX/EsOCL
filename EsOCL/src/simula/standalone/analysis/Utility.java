package simula.standalone.analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import tudresden.ocl20.pivot.pivotmodel.Expression;

public class Utility {

	/** singleton instance */
	private static Utility instance;

	public static double K = 1;

	/**
	 * Returns the single instance of the {@link StandaloneFacade}.
	 */
	public static Utility INSTANCE = instance();

	private static Utility instance() {

		if (instance == null) {
			instance = new Utility();
		}
		return instance;
	}

	public double formatValue(double value) {
		DecimalFormat df = new DecimalFormat(".00");
		return Double.valueOf(df.format(value));
	}

	public double normalize(double value) {
		return value / (value + 1);
	}

	private int[] getNextIndexArray(int[] index, int max) {
		for (int i = 0; i < index.length; i++) {
			if (index[i] < max) {
				index[i] += 1;
				return index;
			} else {
				for (int j = i + 1; j < index.length; j++) {
					if (index[j] < max) {
						index[j] += 1;
						for (int k = j - 1; k >= 0; k--) {
							index[k] = 0;
						}
						return index;
					}
				}
			}
		}
		return null;
	}

	public int[][] combInArrayDup(int[] input, int n) {

		int count = 0;
		int[][] result = new int[(int) Math.pow(input.length, n)][n];
		int[] index = new int[n];
		for (int i = 0; i < index.length; i++) {
			index[i] = 0;
		}
		int max = input.length - 1;
		while (true) {
			if (index == null) {
				break;
			}
			for (int i = 0; i < index.length; i++) {
				result[count][i] = index[i];
			}
			count++;
			index = getNextIndexArray(index, max);
		}
		return result;
	}

	private ArrayList<String> getArrangeOrCombine(String[] args, int n,
			boolean isArrange) {
		Combination comb = new Combination();
		comb.mn(args, n);
		if (!isArrange) {
			return comb.getCombList();
		}
		ArrayList<String> arrangeList = new ArrayList<String>();
		for (int i = 0; i < comb.getCombList().size(); i++) {
			String[] list = comb.getCombList().get(i).split(",");
			Arrange ts = new Arrange();
			ts.perm(list, 0, list.length - 1);
			for (int j = 0; j < ts.getArrangeList().size(); j++) {
				arrangeList.add(ts.getArrangeList().get(j));
			}
		}
		return arrangeList;
	}

	public int[][] getArrangeOrCombine(int[] input) {
		int[][] result = new int[input.length * (input.length - 1) / 2][2];
		String[] str_args = new String[input.length];
		for (int i = 0; i < input.length; i++) {
			str_args[i] = input[i] + "";
		}
		int index = 0;
		ArrayList<String> str_result = getArrangeOrCombine(str_args, 2, false);
		for (String string : str_result) {
			String[] list = string.split(",");
			result[index][0] = Integer.valueOf(list[0]);
			result[index][1] = Integer.valueOf(list[1]);
			index++;
		}
		return result;
	}

}
