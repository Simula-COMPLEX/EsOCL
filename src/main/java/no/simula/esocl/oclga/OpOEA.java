package no.simula.esocl.oclga;

//This algorithm is based on Genetic Algorithm

public class OpOEA extends SSGA {
    public OpOEA() {
    }

    public String[] getSolution(Problem problem) {
        Individual current = Individual.getRandomIndividual(problem);
        current.evaluate();
        this.increaseIteration();

        if (current.fitness_value == 0d)
            return current.problem.decoding(current.v);

        Individual tmp = Individual.getRandomIndividual(problem);

        //init first generation
        while (!this.isStoppingCriterionFulfilled()) {
            tmp.copyDataFrom(current);
            mutateAndEvaluateOffspring(tmp);

            if (tmp.fitness_value == 0d)
                return tmp.problem.decoding(tmp.v);

            if (tmp.fitness_value <= current.fitness_value) {
                current.copyDataFrom(tmp);

                if (tmp.fitness_value < current.fitness_value)
                    reportImprovement();
            }
        }

        return current.problem.decoding(current.v);
    }

    @Override
    public String getShortName() {
        return "OpOEA";
    }
}
