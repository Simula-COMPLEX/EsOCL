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


import org.apache.log4j.Logger;
import org.dresdenocl.essentialocl.expressions.OclExpression;
import org.dresdenocl.essentialocl.expressions.impl.IntegerLiteralExpImpl;
import org.dresdenocl.essentialocl.expressions.impl.OperationCallExpImpl;
import org.dresdenocl.pivotmodel.Constraint;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class BoundValueOCLExpUtility {
    private final static Logger logger = Logger.getLogger(BoundValueOCLExpUtility.class);

    private final static String[] OP_BOUND = {"<", "<=", ">", ">="};
    /**
     * singleton instance
     */
    private static BoundValueOCLExpUtility instance;
    /**
     * Returns the single instance of the {@link OCLExpUtility}.
     */
    public static BoundValueOCLExpUtility INSTANCE = instance();
    private String[][] typeArray;
    private int[][] comb;
    private Map<OperationCallExpImpl, Integer> oceMap;

    private static BoundValueOCLExpUtility instance() {

        if (instance == null) {
            instance = new BoundValueOCLExpUtility();
        }
        return instance;
    }


    public String[][] getTypeArray() {
        return typeArray;
    }


    public int buildIndexArray4Bound(Constraint constraint) {
        oceMap = new HashMap<OperationCallExpImpl, Integer>();
        TreeIterator<EObject> it = constraint.getSpecification().eAllContents();
        while (it.hasNext()) {
            EObject e = (EObject) it.next();
            if (e instanceof OperationCallExpImpl) {
                OperationCallExpImpl oce = (OperationCallExpImpl) e;
                if (isBelongToOp(oce.getReferredOperation().getName(), OP_BOUND)) {
                    OclExpression rightExp = (OclExpression) oce.eContents()
                            .get(1);
                    if (rightExp instanceof IntegerLiteralExpImpl) {
                        oceMap.put(oce, ((IntegerLiteralExpImpl) rightExp)
                                .getIntegerSymbol());
                    }
                }
            }
        }
        typeArray = buildBoundTypeArray(oceMap.keySet());
        int[] input = Utility.INSTANCE.genIndexArray(3);
        comb = Utility.INSTANCE.combInArrayDup(input, oceMap.size());
        return comb.length;
    }


    public boolean isBelongToOp(String opName, String[] ops) {
        for (String op : ops) {
            if (op.equals(opName))
                return true;
        }
        return false;
    }

    public String[][] buildBoundTypeArray(Set<OperationCallExpImpl> oceSet) {
        String[][] typeArray = new String[oceSet.size()][3];
        OperationCallExpImpl[] oceArray = new OperationCallExpImpl[oceSet
                .size()];
        oceArray = oceSet.toArray(oceArray);
        for (int i = 0; i < oceArray.length; i++) {
            OperationCallExpImpl oce = oceArray[i];
            String opName = oce.getReferredOperation().getName();
            switch (opName) {
                case "<":
                    typeArray[i][0] = "valid";
                    typeArray[i][1] = "invalid";
                    typeArray[i][2] = "invalid";
                    break;
                case "<=":
                    typeArray[i][0] = "valid";
                    typeArray[i][1] = "valid";
                    typeArray[i][2] = "invalid";
                    break;
                case ">":
                    typeArray[i][0] = "invalid";
                    typeArray[i][1] = "invalid";
                    typeArray[i][2] = "valid";
                    break;
                case ">=":
                    typeArray[i][0] = "invalid";
                    typeArray[i][1] = "valid";
                    typeArray[i][2] = "valid";
                    break;
            }
        }
        return typeArray;
    }

    public void generateBoundValue(int iterate_index) {
        OperationCallExpImpl[] oceArray = new OperationCallExpImpl[oceMap
                .keySet().size()];
        oceArray = oceMap.keySet().toArray(oceArray);
        for (int i = 0; i < oceArray.length; i++) {
            OperationCallExpImpl oce = oceArray[i];
            IntegerLiteralExpImpl rightExp = (IntegerLiteralExpImpl) oce
                    .eContents().get(1);
            int index = comb[iterate_index][i];
            switch (index) {
                case 0:
                    rightExp.setIntegerSymbol(rightExp.getIntegerSymbol() - 1);
                    break;
                case 2:
                    rightExp.setIntegerSymbol(rightExp.getIntegerSymbol() + 1);
                    break;
            }
        }
    }

    public void restoreOriginalValue() {
        Set<OperationCallExpImpl> oceSet = oceMap.keySet();
        for (OperationCallExpImpl oce : oceSet) {
            int original = oceMap.get(oce);
            IntegerLiteralExpImpl rightExp = (IntegerLiteralExpImpl) oce
                    .eContents().get(1);
            rightExp.setIntegerSymbol(original);
        }
    }
}

