package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect Customer_InvAspect_sizesAgree {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.Customer}.</p>
     */
    protected pointcut allCustomerConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.Customer.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.Customer#cards}.</p>
     */
    protected pointcut cardsSetter(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.Customer.cards) && target(aClass); 

    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.Customer#programs}.</p>
     */
    protected pointcut programsSetter(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.Customer.programs) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) :
    	cardsSetter(aClass)
    	|| programsSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class Customer defined by the constraint
     * <code>context Customer
     *       inv sizesAgree:     programs->size() = cards->select( valid = true )->size()</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.Customer aClass) : allCustomerConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of Customer. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.Customer")) {
        java.util.HashSet<tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard> result1;
        result1 = new java.util.HashSet<tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard>();
        
        /* Iterator Select: Select all elements which fulfill the condition. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard anElement1 : aClass.cards) {
            if (((Object) anElement1.valid).equals(new Boolean(true))) {
                result1.add(anElement1);
            }
            // no else
        }
    
        if (!((Object) tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.size(aClass.programs)).equals(tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.size(result1))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'sizesAgree' (inv sizesAgree:     programs->size() = cards->select( valid = true )->size()) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}