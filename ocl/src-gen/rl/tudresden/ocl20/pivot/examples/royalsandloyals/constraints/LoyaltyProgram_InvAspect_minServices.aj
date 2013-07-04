package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyProgram_InvAspect_minServices {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram}.</p>
     */
    protected pointcut allLoyaltyProgramConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#partners}.</p>
     */
    protected pointcut partnersSetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.partners) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
    	partnersSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class LoyaltyProgram defined by the constraint
     * <code>context LoyaltyProgram
     *       inv minServices: partners->forAll(deliveredServices->size() >= 1)</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) : allLoyaltyProgramConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of LoyaltyProgram. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram")) {
        Boolean result1;
        result1 = true;
        
        /* Iterator ForAll: Iterate and check, if all elements fulfill the condition. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner anElement1 : aClass.partners) {
            if (!(tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.size(anElement1.deliveredServices) >= new Integer(1))) {
                result1 = false;
                break;
            }
            // no else
        }
    
        if (!result1) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'minServices' (inv minServices: partners->forAll(deliveredServices->size() >= 1)) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}