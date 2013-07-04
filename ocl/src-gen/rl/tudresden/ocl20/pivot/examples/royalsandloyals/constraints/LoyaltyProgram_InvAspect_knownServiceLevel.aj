package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyProgram_InvAspect_knownServiceLevel {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram}.</p>
     */
    protected pointcut allLoyaltyProgramConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#levels}.</p>
     */
    protected pointcut levelsSetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.levels) && target(aClass); 

    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#membership}.</p>
     */
    protected pointcut membershipSetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.membership) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) :
    	levelsSetter(aClass)
    	|| membershipSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class LoyaltyProgram defined by the constraint
     * <code>context LoyaltyProgram
     *       inv knownServiceLevel: levels->includes(membership.currentLevel)</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass) : allLoyaltyProgramConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of LoyaltyProgram. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram")) {
        if (!tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.includes(aClass.levels, aClass.membership.currentLevel)) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'knownServiceLevel' (inv knownServiceLevel: levels->includes(membership.currentLevel)) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}