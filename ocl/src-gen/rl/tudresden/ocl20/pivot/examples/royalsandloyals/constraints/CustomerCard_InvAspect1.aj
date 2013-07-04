package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect CustomerCard_InvAspect1 {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard}.</p>
     */
    protected pointcut allCustomerCardConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard#valid}.</p>
     */
    protected pointcut validSetter(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard.valid) && target(aClass); 

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
    	validSetter(aClass)
    	|| validFromSetter(aClass)
    	|| validThruSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class CustomerCard defined by the constraint
     * <code>context CustomerCard
     *       inv:  let correctDate : Boolean =   self.validFrom.isBefore(Date::now()) and   self.validThru.isAfter(Date::now()) in   if valid then     correctDate = false   else     correctDate = true   endif</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass) : allCustomerCardConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of CustomerCard. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard")) {
        Boolean correctDate;
        correctDate = (aClass.validFrom.isBefore(tudresden.ocl20.pivot.examples.royalsandloyals.Date.now()) && aClass.validThru.isAfter(tudresden.ocl20.pivot.examples.royalsandloyals.Date.now()));
        
        Boolean ifExpResult1;
        
        if (aClass.valid) {
            ifExpResult1 = ((Object) correctDate).equals(new Boolean(false));
        } else {
            ifExpResult1 = ((Object) correctDate).equals(new Boolean(true));
        }
    
        if (!ifExpResult1) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'undefined' (inv:  let correctDate : Boolean =   self.validFrom.isBefore(Date::now()) and   self.validThru.isAfter(Date::now()) in   if valid then     correctDate = false   else     correctDate = true   endif) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}