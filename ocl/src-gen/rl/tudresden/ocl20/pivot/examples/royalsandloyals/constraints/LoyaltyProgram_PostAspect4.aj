package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyProgram_PostAspect4 {

    /**
     * A method to make copies of {@link tudresden.ocl20.pivot.examples.royalsandloyals.Membership}.
     *
     * @param anObject The {@link Object} which shall be copied.
     * @return The copy of the given {@link Object}.
     */
    protected tudresden.ocl20.pivot.examples.royalsandloyals.Membership createCopy(tudresden.ocl20.pivot.examples.royalsandloyals.Membership anObject) {
    
        tudresden.ocl20.pivot.examples.royalsandloyals.Membership result;
    
        /*
         * TODO: Auto-generated code to copy values of the class tudresden.ocl20.pivot.examples.royalsandloyals.Membership.
         * Change this statement to create a new instance of tudresden.ocl20.pivot.examples.royalsandloyals.Membership
         * and eventually set some attributes as well.
         */
        result = anObject;
    
        return result;
    } 
    
    /**
     * <p>Pointcut for all calls on {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#enroll(tudresden.ocl20.pivot.examples.royalsandloyals.Customer c)}.</p>
     */
    protected pointcut enrollCaller(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass, tudresden.ocl20.pivot.examples.royalsandloyals.Customer c):
    	call(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.enroll(tudresden.ocl20.pivot.examples.royalsandloyals.Customer))
    	&& target(aClass) && args(c);
    
    /**
     * <p>Checks a postcondition for the operation {@link LoyaltyProgram#enroll(, tudresden.ocl20.pivot.examples.royalsandloyals.Customer c)} defined by the constraint
     * <code>context LoyaltyProgram::enroll(c: tudresden.ocl20.pivot.examples.royalsandloyals.Customer) : Boolean
     *       post: membership = membership@pre</code></p>
     */
    Boolean around(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass, tudresden.ocl20.pivot.examples.royalsandloyals.Customer c): enrollCaller(aClass, c) {
        /* Disable this constraint for subclasses of LoyaltyProgram. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram")) {
    
        tudresden.ocl20.pivot.examples.royalsandloyals.Membership atPreValue1;
        
        if (aClass.membership == null) {
            atPreValue1 == null;
        } else {
        atPreValue1 = this.createCopy(aClass.membership);
        }
    
        Boolean result;
        result = proceed(aClass, c);
    
        if (!aClass.membership.equals(atPreValue1)) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'undefined' (post: membership = membership@pre) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
    
        return result;
        }
    
        else {
            return proceed(aClass, c);
        }
    }
}