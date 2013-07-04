package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect CustomerCard_InvAspect_checkDates {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard}.</p>
     */
    protected pointcut allCustomerCardConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard#validFrom}.</p>
     */
    protected pointcut validFromSetter(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard.validFrom) && target(aClass); 

    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard#validThru}.</p>
     */
    protected pointcut validThruSetter(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard.validThru) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass) :
    	validFromSetter(aClass)
    	|| validThruSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class CustomerCard defined by the constraint
     * <code>context CustomerCard
     *       inv checkDates: validFrom.isBefore(validThru)</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass) : allCustomerCardConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of CustomerCard. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard")) {
        if (!aClass.validFrom.isBefore(aClass.validThru)) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'checkDates' (inv checkDates: validFrom.isBefore(validThru)) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}