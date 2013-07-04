package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyProgram_InvAspect_noAccounts {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram}.</p>
     */
    protected pointcut allLoyaltyProgramConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#membership}.</p>
     */
    protected pointcut membershipSetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.membership) && target(aClass); 

    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#partners}.</p>
     */
    protected pointcut partnersSetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.partners) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
    	membershipSetter(aClass)
    	|| partnersSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class LoyaltyProgram defined by the constraint
     * <code>context LoyaltyProgram
     *       inv noAccounts:    partners.deliveredServices     ->forAll(pointsEarned = 0 and pointsBurned = 0)     implies membership.accounts->isEmpty()</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) : allLoyaltyProgramConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of LoyaltyProgram. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram")) {
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Service> result2;
        result2 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Service>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner anElement1 : aClass.partners) {
            result2.addAll(anElement1.deliveredServices);
        }
        Boolean result1;
        result1 = true;
        
        /* Iterator ForAll: Iterate and check, if all elements fulfill the condition. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Service anElement2 : result2) {
            if (!(((Object) anElement2.pointsEarned).equals(new Integer(0)) && ((Object) anElement2.pointsBurned).equals(new Integer(0)))) {
                result1 = false;
                break;
            }
            // no else
        }
    
        if (!(!result1 || tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.isEmpty(aClass.membership.accounts))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'noAccounts' (inv noAccounts:    partners.deliveredServices     ->forAll(pointsEarned = 0 and pointsBurned = 0)     implies membership.accounts->isEmpty()) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}