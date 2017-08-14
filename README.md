[EsOCL](http://zen-tools.com/tools/esocl.html) 
=====

Evolutionary Solver for OCL (EsOCL) is a practical and scalable search-based constraint solver for the Object 
Constraint Language (OCL) that provides a stringSolution for various model-driven engineering problems such as test data 
generation for Model-based Testing (MBT), configuration for product lines, and model-transformation testing. 
EsOCL is developed in Java that interacts with an existing library, an OCL evaluator, called the Dresden OCL Software 
(DOS), which provides APIs to parse an d evaluate an OCL expression based on an object model. Our tool only requires 
interacting with DOS for the evaluation of constraints.

EsOCL implements the calculation of branch distance for various expressions in OCL (see the related publication 
for details), which aims at calculating how far are the test data values from satisfying constraints. For a constraint, 
the search space is defined by those attributes that are used in the constraint. This is determined by statically 
parsing a constraint before solving it and improves the search efficiency in a similar fashion to the concept 
of input size reduction. 

The search algorithms employed are implemented in Java as well and include Genetic Algorithm, (1+1) Evolutionary 
Algorithm, and Alternating Variable Method (AVM). Our implementation of branch distance calculation corresponds 
to OCL semantics.


## Example



```java 
import no.simula.esocl.ocl.distance.Result;
import no.simula.esocl.solver.OCLSolver;
public class EsOCL {
    public static void main(String args[]) throws Exception {
        String UMLModelPath = "src/main/resources/model/Test2.uml";
        String constraint = "" +
                "package Test2 " +
                "context A inv initial: self.b->select(b|b.x > 90)->size() > 4 and self.b->select(b|b.x > 90)->exists(b|b.x=92) " +
                "endpackage";
        OCLSolver oclSolver = new OCLSolver();
        Result result = oclSolver.solveConstraint(UMLModelPath, constraint, new int[]{OCLSolver.AVM, OCLSolver.OpOEA}, 5000);
        oclSolver.printResults(result);
    }
}
```


## License
EsOCL  is released under the [Eclipse Public License - v 1.0](https://github.com/Simula-TaoGroup/EsOCL/blob/master/LICENSE)
