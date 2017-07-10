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

import org.dresdenocl.pivotmodel.Type;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class UMLAttributeIns extends AbstUMLModelIns {

    private Type type;

    public UMLAttributeIns(String name, String value) {
        super(name, value);

    }


    public Type getType() {
        return type;
    }


    public void setType(Type type) {
        this.type = type;
    }


    public String getClassName() {
        String[] qualifiedNames = this.qualifiedName.split("::");
        return qualifiedNames[qualifiedNames.length - 2];
    }

}
