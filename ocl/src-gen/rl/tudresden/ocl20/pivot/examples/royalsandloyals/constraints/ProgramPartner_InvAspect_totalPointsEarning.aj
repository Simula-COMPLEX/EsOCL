package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect ProgramPartner_InvAspect_totalPointsEarning {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner}.</p>
     */
    protected pointcut allProgramPartnerConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner#deliveredServices}.</p>
     */
    protected pointcut deliveredServicesSetter(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner.deliveredServices) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) :
    	deliveredServicesSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class ProgramPartner defined by the constraint
     * <code>context ProgramPartner
     *       inv totalPointsEarning:    deliveredServices.transaction->select( 	oclIsTypeOf(Earning)).points->sum() < 10000</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) : allProgramPartnerConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of ProgramPartner. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner")) {
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Transaction> result3;
        result3 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Transaction>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Service anElement1 : aClass.deliveredServices) {
            result3.add(anElement1.transaction);
        }
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Transaction> result2;
        result2 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Transaction>();
        
        /* Iterator Select: Select all elements which fulfill the condition. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Transaction anElement2 : result3) {
            if (anElement2.getClass().getCanonicalName().equals(tudresden.ocl20.pivot.examples.royalsandloyals.Earning.class.getCanonicalName())) {
                result2.add(anElement2);
            }
            // no else
        }
        java.util.ArrayList<Integer> result1;
        result1 = new java.util.ArrayList<Integer>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Transaction anElement3 : result2) {
            result1.add(anElement3.points);
        }
    
        if (!(new Integer(tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.sum(result1).intValue()) < new Integer(10000))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'totalPointsEarning' (inv totalPointsEarning:    deliveredServices.transaction->select( 	oclIsTypeOf(Earning)).points->sum() < 10000) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}