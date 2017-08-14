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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class Arrange {


    private void swap(String list[], int k, int i) {
        String c3 = list[k];
        list[k] = list[i];
        list[i] = c3;
    }

    public List<String> perm(String list[], int k, int m) {
        List<String> arrangeList = new ArrayList<>();
        if (k > m) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= m; i++) {
                sb.append(list[i]).append(",");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            arrangeList.add(sb.toString());

        } else {
            for (int i = k; i <= m; i++) {
                swap(list, k, i);
                arrangeList.addAll(perm(list, k + 1, m));
                swap(list, k, i);
            }
        }
        return arrangeList;
    }


}