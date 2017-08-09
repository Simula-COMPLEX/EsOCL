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

import org.apache.commons.math3.random.MersenneTwister;

/**
 * @author Shaukat Ali
 * @version 1.0
 * @since 2017-07-03
 */
public class RandomGenerator {
    private static MersenneTwister random = new MersenneTwister();

    public static MersenneTwister getGenerator() {
        return random;
    }


}
