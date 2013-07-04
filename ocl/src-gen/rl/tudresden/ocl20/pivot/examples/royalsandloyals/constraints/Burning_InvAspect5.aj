package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect Burning_InvAspect5 {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.Burning}.</p>
     */
    protected pointcut allBurningConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.Burning aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.Burning.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.Burning#points}.</p>
     */
    protected pointcut pointsSetter(tudresden.ocl20.pivot.examples.royalsandloyals.Burning aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.Burning.points) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.Burning aClass) :
    	pointsSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class Burning defined by the constraint
     * <code>context Burning
     *       inv: self.points = self.oclAsType(Transaction).points</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.Burning aClass) : allBurningConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of Burning. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.Burning")) {
        if (!((Object) aClass.points).equals(((tudresden.ocl20.pivot.examples.royalsandloyals.Transaction) aClass).points)) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'undefined' (inv: self.points = self.oclAsType(Transaction).points) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}