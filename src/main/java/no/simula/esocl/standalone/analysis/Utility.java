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

package no.simula.esocl.standalone.analysis;

import no.simula.esocl.ocl.distance.ValueElement4Search;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class Utility {

    public static double K = 1;
    /**
     * singleton instance
     */
    private static Utility instance;
    /**
     * Returns the single instance of the {@link Utility}.
     */
    public static Utility INSTANCE = instance();
    private Document modelDoc;
    private Document[] profileDocs;

    private static Utility instance() {

        if (instance == null) {
            instance = new Utility();
        }
        return instance;
    }

    public static void main(String[] args) {
//		String[] profile = {};
//		Utility.instance.initialUMLDoc("resources/model/OCLTest.uml",profile);
//		System.out.println(Utility.instance.getElementID("Customer", "cards"));
        String str = "34000000";
        String str1 = "342351";
        System.out.println(Utility.instance.formatRealValueWithoutZero(str));
        System.out.println(Utility.instance.formatRealValueWithoutZero(str1));
    }

    public void initialUMLDoc(File inputModelFilePath,
                              String[] inputProfileFilePaths) {
        SAXReader sax = new SAXReader();
        try {
            this.modelDoc = sax.read(inputModelFilePath);
            if (inputProfileFilePaths.length != 0) {
                this.profileDocs = new Document[inputProfileFilePaths.length];
                for (int i = 0; i < inputProfileFilePaths.length; i++) {
                    this.profileDocs[i] = sax.read(new File(
                            inputProfileFilePaths[i]));
                }
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public double formatValue(double value) {
        try {
            DecimalFormat df = new DecimalFormat(".000000000000000000000");
            return Double.valueOf(df.format(value));
        } catch (NumberFormatException e) {
            try {
                DecimalFormat df = new DecimalFormat(",000000000000000000000");
                return Double.valueOf(df.format(value));
            } catch (NumberFormatException e2) {
                return 0.0;
            }
        }
    }

    public double formatRealValueWithoutZero(String afterDecimal) {
        StringBuffer sBuffer = new StringBuffer(afterDecimal);
        int length = afterDecimal.length();
        for (int i = length - 1; i >= 0; i--) {
            if (sBuffer.charAt(i) == '0') sBuffer.deleteCharAt(i);
        }

        try {
            return Double.valueOf(sBuffer.toString());
        } catch (NumberFormatException e2) {
            return 0.0;
        }

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
            System.arraycopy(index, 0, result[count], 0, index.length);
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

    public String[] getLowAndUpperValueForProperty(String className,
                                                   String attrName, String fileType) {
        String[] LowAndUpperValue = new String[2];
        String xpath_attribute = "";
        if (fileType.equals("model")) {
            xpath_attribute = "//packagedElement[@xmi:type='uml:Class' and @name='"
                    + className + "']/ownedAttribute[@name='" + attrName + "']";
            String xpath_lowValue = xpath_attribute + "/lowerValue";
            String xpath_upperValue = xpath_attribute + "/upperValue";
            List<Element> valueNodes = this.modelDoc.selectNodes(xpath_lowValue
                    + "|" + xpath_upperValue);
            for (Element element : valueNodes) {
                if (element.getName().equals("lowerValue"))
                    LowAndUpperValue[0] = element.attributeValue("value");
                else
                    LowAndUpperValue[1] = element.attributeValue("value");
            }
            return LowAndUpperValue;
        } else {
            for (int i = 0; i < this.profileDocs.length; i++) {
                xpath_attribute = "//packagedElement[@xmi:type='uml:Stereotype' and @name='"
                        + className
                        + "']/ownedAttribute[@name='"
                        + attrName
                        + "']";
                String xpath_lowValue = xpath_attribute + "/lowerValue";
                String xpath_upperValue = xpath_attribute + "/upperValue";
                List<Element> valueNodes = this.profileDocs[i]
                        .selectNodes(xpath_lowValue + "|" + xpath_upperValue);
                if (valueNodes.size() != 0) {
                    for (Element element : valueNodes) {
                        if (element.getName().equals("lowerValue"))
                            LowAndUpperValue[0] = element
                                    .attributeValue("value");
                        else
                            LowAndUpperValue[1] = element
                                    .attributeValue("value");
                    }
                    return LowAndUpperValue;
                }
            }
        }
        return null;

    }

    public String getElementID(String fatherElementName, String elementName) {
        String xpath = "";
        if (elementName != null) {
            xpath = "//packagedElement[@xmi:type='uml:Class' and @name='"
                    + fatherElementName + "']/ownedAttribute[@name='"
                    + elementName + "']";
        } else {
            xpath = "//packagedElement[@xmi:type='uml:Class' and @name='"
                    + fatherElementName + "']";
        }
        List<Element> valueNodes = this.modelDoc.selectNodes(xpath);
        return valueNodes.get(0).attributeValue("id");
    }

    public int getFixedNumberOfCardinality(ValueElement4Search assVes) {
        int min = assVes.getMinValue();
        int max = assVes.getMaxValue();
        if (min == max)
            return max;
        else if (max - min == 1)
            return min + 1;
        else {
            Random random = new Random();
            return random.nextInt(max) % (max - min + 1) + min;
        }
    }

    public int[] genIndexArray(int size) {
        int[] indexArray = new int[size];
        for (int i = 0; i < size; i++) {
            indexArray[i] = i;
        }
        return indexArray;
    }

}
