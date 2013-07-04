package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect LoyaltyProgram_PreAspect_addService {

    /**
     * <p>Pointcut for all calls on {@link tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram#addService(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aPartner, tudresden.ocl20.pivot.examples.royalsandloyals.ServiceLevel aLevel, tudresden.ocl20.pivot.examples.royalsandloyals.Service aService)}.</p>
     */
    protected pointcut addServiceCaller(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass, tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aPartner, tudresden.ocl20.pivot.examples.royalsandloyals.ServiceLevel aLevel, tudresden.ocl20.pivot.examples.royalsandloyals.Service aService):
    	call(* tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram.addService(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner, tudresden.ocl20.pivot.examples.royalsandloyals.ServiceLevel, tudresden.ocl20.pivot.examples.royalsandloyals.Service))
    	&& target(aClass) && args(aPartner, aLevel, aService);
    
    /**
     * <p>Checks a precondition for the {@link LoyaltyProgram#addService(, tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aPartner, tudresden.ocl20.pivot.examples.royalsandloyals.ServiceLevel aLevel, tudresden.ocl20.pivot.examples.royalsandloyals.Service aService)} defined by the constraint
     * <code>context LoyaltyProgram::addService(aPartner: tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner, aLevel: tudresden.ocl20.pivot.examples.royalsandloyals.ServiceLevel, aService: tudresden.ocl20.pivot.examples.royalsandloyals.Service) : 
     *       pre: partners->includes(aPartner)</code></p>
     */
    before(tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram aClass, tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aPartner, tudresden.ocl20.pivot.examples.royalsandloyals.ServiceLevel aLevel, tudresden.ocl20.pivot.examples.royalsandloyals.Service aService): addServiceCaller(aClass, aPartner, aLevel, aService) {
        /* Disable this constraint for subclasses of LoyaltyProgram. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram")) {
        if (!tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.includes(aClass.partners, aPartner)) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'undefined' (pre: partners->includes(aPartner)) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}