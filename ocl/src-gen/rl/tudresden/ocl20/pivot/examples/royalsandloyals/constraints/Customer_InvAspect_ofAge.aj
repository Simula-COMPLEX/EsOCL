package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect Customer_InvAspect_ofAge {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.Customer}.</p>
     */
    protected pointcut allCustomerConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.Customer.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.Customer#age}.</p>
     */
    protected pointcut ageSetter(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.Customer.age) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) :
    	ageSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class Customer defined by the constraint
     * <code>context Customer
     *       inv ofAge: age >= 18</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) : allCustomerConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of Customer. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.Customer")) {
        if (!(aClass.age >= new Integer(18))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'ofAge' (inv ofAge: age >= 18) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}