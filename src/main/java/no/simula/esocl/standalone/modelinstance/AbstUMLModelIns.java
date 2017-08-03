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

package no.simula.esocl.standalone.modelinstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class AbstUMLModelIns {

    private String qualifiedName;
    private String value;

    public AbstUMLModelIns(String qualifiedName, String value) {
        this.qualifiedName = qualifiedName;
        this.value = value;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        return new AbstUMLModelIns(this.qualifiedName, this.value);
    }

    public List<String> getQualifiedNameList() {
        List<String> qualifiedNameList = new ArrayList<>();
        String[] qualifiedNames = this.qualifiedName.split("::");
        qualifiedNameList.addAll(Arrays.asList(qualifiedNames));
        return qualifiedNameList;

    }

    public String getName() {
        String[] qualifiedNames = this.qualifiedName.split("::");
        return qualifiedNames[qualifiedNames.length - 1];
    }

}
