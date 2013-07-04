package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyAccount_DefAspect_usedServices {

    /**
     * <p>Defines the field usedServices defined by the constraint
     * <code>context LoyaltyAccount
     *       def: usedServices: Set(Service) = transactions.service->asSet()</code></p>
     */
    public java.util.Set<tudresden.ocl20.pivot.examples.royalsandloyals.Service> tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount.usedServices;
    
    /**
     * <p>Pointcut for all requests on {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount#usedServices}.</p>
     */
    protected pointcut usedServicesGetter(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount aClass) :
    	get(* usedServices) && target(aClass);
    
    /**
     * <p>Initializes the attribute usedServices defined by the constraint
     * <code>context LoyaltyAccount
     *       def: usedServices: Set(Service) = transactions.service->asSet()</code></p>
     */
    before(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyAccount aClass): usedServicesGetter(aClass) {
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Service> result1;
        result1 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Service>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Transaction anElement1 : aClass.transactions) {
            result1.add(anElement1.service);
        }
    
        aClass.usedServices = tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclBags.asSet(result1);
    }
}