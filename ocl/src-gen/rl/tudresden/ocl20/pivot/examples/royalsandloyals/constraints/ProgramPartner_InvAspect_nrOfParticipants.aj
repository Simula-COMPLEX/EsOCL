package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect ProgramPartner_InvAspect_nrOfParticipants {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner}.</p>
     */
    protected pointcut allProgramPartnerConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner#numberOfCustomers}.</p>
     */
    protected pointcut numberOfCustomersSetter(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner.numberOfCustomers) && target(aClass); 

    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner#programs}.</p>
     */
    protected pointcut programsSetter(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner.programs) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) :
    	numberOfCustomersSetter(aClass)
    	|| programsSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class ProgramPartner defined by the constraint
     * <code>context ProgramPartner
     *       inv nrOfParticipants: numberOfCustomers = programs.participants->size()</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner aClass) : allProgramPartnerConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of ProgramPartner. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.ProgramPartner")) {
        java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Customer> result1;
        result1 = new java.util.ArrayList<tudresden.ocl20.pivot.examples.royalsandloyals.Customer>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.LoyaltyProgram anElement1 : aClass.programs) {
            result1.addAll(anElement1.participants);
        }
    
        if (!((Object) aClass.numberOfCustomers).equals(tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.size(result1))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'nrOfParticipants' (inv nrOfParticipants: numberOfCustomers = programs.participants->size()) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}