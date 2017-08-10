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

package no.simula.esocl.oclga;

import no.simula.esocl.ocl.distance.ValueElement4Search;

import java.util.ArrayList;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class EncodingDecoding {

    private static ArrayList<GeneValueScope> geneValueList;

    public EncodingDecoding() {
        geneValueList = new ArrayList<>();
    }

    /**
     * during the encoding, we encoding the value type of String to a array that with the max length
     * for example, when the String length scope is [1, 20], then the arry is 20
     */
    private static ArrayList<GeneValueScope> generateGeneValueList(ValueElement4Search[] constraints) {
        int individualLen = 0;

        for (ValueElement4Search element : constraints) {
            if (element.getType() == 2) {
                addCommonGene(element);               //add the gene of the length of string
                addStringGene(element.getMaxValue()); //add the gene of all the alphabet of the string
                individualLen = individualLen + element.getMaxValue() + 1;
            } else if (element.getType() == 3) {
                addDoubleGene(element);
                individualLen = individualLen + 2;
            } else {
                addCommonGene(element);
                individualLen++;
            }
        }

        return getGeneValueList();
    }

    /**
     * For the ValueElement4Search
     * 0 represent types of int
     * 1 represent types of boolean and enumeration
     * 2 represent types of string
     * 3 represent types of double
     * <p>
     * but for GeneValueScope
     * NUMERICAL_TYPE = 0;
     * CATEGORICAL_TYPE = 1;
     * <p>
     * When type of ValueElement4Search is 1 the type of GeneValueScope need to set to be 1 as a CATEGORICAL type
     * or else, set type to 0, as a double type
     */
    private static void addCommonGene(ValueElement4Search element) {
        GeneValueScope gene = new GeneValueScope();
        gene.setMinValue(element.getMinValue());
        gene.setMaxValue(element.getMaxValue());

        switch (element.getType()) {
            case 0:
                gene.type = Problem.NUMERICAL_TYPE;
                break;
            case 1:
                gene.type = Problem.CATEGORICAL_TYPE;
                break;
            case 2:
                gene.setEncodedConstraintType(GeneValueScope.EncodedConstraintType.String);
                gene.type = Problem.NUMERICAL_TYPE;
                break;
            case 3:
                gene.setEncodedConstraintType(GeneValueScope.EncodedConstraintType.Double);
                gene.type = Problem.NUMERICAL_TYPE;
                break;
        }

        geneValueList.add(gene);
    }

    private static void addStringGene(int len) {
        for (int i = 0; i < len; i++) {
            GeneValueScope gene = new GeneValueScope();
            gene.setMinValue(1);
            gene.setMaxValue(127);
            gene.type = Problem.NUMERICAL_TYPE;

            geneValueList.add(gene);
        }
    }

    private static void addDoubleGene(ValueElement4Search element) {
        GeneValueScope BeforeDotGene = new GeneValueScope();
        BeforeDotGene.setMinValue(element.getMinValue());
        BeforeDotGene.setMaxValue(element.getMaxValue() - 1);
        BeforeDotGene.setEncodedConstraintType(GeneValueScope.EncodedConstraintType.Double);
        BeforeDotGene.type = Problem.NUMERICAL_TYPE;

        geneValueList.add(BeforeDotGene);

        GeneValueScope afterDotGene = new GeneValueScope();
        afterDotGene.setMinValue(0);
        afterDotGene.setMaxValue(Integer.MAX_VALUE - 100);
        afterDotGene.type = Problem.NUMERICAL_TYPE;

        geneValueList.add(afterDotGene);
    }

    private static ArrayList<GeneValueScope> getGeneValueList() {
        return geneValueList;
    }

    /**
     * encoding the ValueElement4Search[] constraints
     */
    public ArrayList<GeneValueScope> encoding(ValueElement4Search[] constraints) {
        return generateGeneValueList(constraints);
    }

    /**
     * decoding the ValueElement4Search[] constraints
     */
    public String[] decoding(int[] v, ValueElement4Search[] constraints) {
        String retString[] = new String[constraints.length];
        int index = 0; //indicate the index of the origin constraint

        for (int i = 0; i < geneValueList.size(); i++) {
            GeneValueScope geneValue = geneValueList.get(i);

            if (geneValue.getEncodedConstraintType() == GeneValueScope.EncodedConstraintType.String) {
                retString[index] = decodingString(i, v[i], v);
                i = i + geneValue.getMaxValue();
            } else if (geneValue.getEncodedConstraintType() == GeneValueScope.EncodedConstraintType.Double) {
                retString[index] = decodingDouble(i, v);
                i = i + 1;
            } else {
                retString[index] = String.valueOf(v[i]);
            }

            index++;
        }

        return retString;
    }

    private String decodingDouble(int index, int[] v) {
        String doubleStr = "";

        String realPart = String.valueOf(v[index]);
        String dotPart = String.valueOf(v[index + 1]);

        doubleStr = realPart + "." + dotPart;
        return doubleStr;
    }

    private String decodingString(int index, int len, int[] v) {
        StringBuilder str = new StringBuilder();

        for (int i = 1; i < len + 1; i++) {
            char character = (char) v[index + i];
            str.append(character);
        }
        System.out.println(len + " after decoding: " + str);
        return str.toString();
    }


}
