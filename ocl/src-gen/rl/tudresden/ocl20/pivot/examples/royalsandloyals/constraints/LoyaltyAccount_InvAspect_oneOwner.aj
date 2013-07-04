package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyAccount_InvAspect_oneOwner {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount}.</p>
     */
    protected pointcut allLoyaltyAccountConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount#transactions}.</p>
     */
    protected pointcut transactionsSetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount.transactions) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount aClass) :
    	transactionsSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class LoyaltyAccount defined by the constraint
     * <code>context LoyaltyAccount
     *       inv oneOwner: transactions.card.owner->asSet()->size() = 1</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount aClass) : allLoyaltyAccountConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of LoyaltyAccount. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount")) {
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard> result2;
        result2 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Transaction anElement1 : aClass.transactions) {
            result2.add(anElement1.card);
        }
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Customer> result1;
        result1 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Customer>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard anElement2 : result2) {
            result1.add(anElement2.owner);
        }
    
        if (!((Object) tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.size(tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclBags.asSet(result1))).equals(new Integer(1))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'oneOwner' (inv oneOwner: transactions.card.owner->asSet()->size() = 1) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}