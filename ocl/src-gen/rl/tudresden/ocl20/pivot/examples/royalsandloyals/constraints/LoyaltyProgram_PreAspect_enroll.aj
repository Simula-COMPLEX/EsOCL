package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyProgram_PreAspect_enroll {

    /**
     * <p>Pointcut for all calls on {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#enroll(tudresden.ocl20.pivot.examples.royalsandloyals.Customer c)}.</p>
     */
    protected pointcut enrollCaller(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass, tudresden.ocl20.pivot.examples.royalsandloyals.Customer c):
    	call(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.enroll(tudresden.ocl20.pivot.examples.royalsandloyals.Customer))
    	&& target(aClass) && args(c);
    
    /**
     * <p>Checks a precondition for the {@link LoyaltyProgram#enroll(, tudresden.ocl20.pivot.examples.royalsandloyals.Customer c)} defined by the constraint
     * <code>context LoyaltyProgram::enroll(c: tudresden.ocl20.pivot.examples.royalsandloyals.Customer) : Boolean
     *       pre: c.name <> ''</code></p>
     */
    before(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass, tudresden.ocl20.pivot.examples.royalsandloyals.Customer c): enrollCaller(aClass, c) {
        /* Disable this constraint for subclasses of LoyaltyProgram. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram")) {
        if (!!c.name.equals("")) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'undefined' (pre: c.name <> '') was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}