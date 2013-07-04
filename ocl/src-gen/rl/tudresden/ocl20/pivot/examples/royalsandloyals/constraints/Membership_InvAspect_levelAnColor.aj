package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect Membership_InvAspect_levelAnColor {

    /**
     * <p>Describes all Constructors of the class {@link tudresden.ocl20.pivot.examples.royalsandloyals.Membership}.</p>
     */
    protected pointcut allMembershipConstructors(tudresden.ocl20.pivot.examples.royalsandloyals.Membership aClass):
        execution(tudresden.ocl20.pivot.examples.royalsandloyals.Membership.new(..)) && this(aClass);
    
    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.Membership#card}.</p>
     */
    protected pointcut cardSetter(tudresden.ocl20.pivot.examples.royalsandloyals.Membership aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.Membership.card) && target(aClass); 

    /**
     * <p>Pointcut for all changes of the attribute {@link tudresden.ocl20.pivot.examples.royalsandloyals.Membership#currentLevel}.</p>
     */
    protected pointcut currentLevelSetter(tudresden.ocl20.pivot.examples.royalsandloyals.Membership aClass) :
        set(* tudresden.ocl20.pivot.examples.royalsandloyals.Membership.currentLevel) && target(aClass); 
    
    /**
     * <p>Pointcut to collect all attributeSetters.</p>
     */
    protected pointcut allSetters(tudresden.ocl20.pivot.examples.royalsandloyals.Membership aClass) :
    	cardSetter(aClass)
    	|| currentLevelSetter(aClass);
    /**
     * <p><code>Checks an invariant on the class Membership defined by the constraint
     * <code>context Membership
     *       inv levelAnColor:   currentLevel.name = 'Silver' implies card.color = Color::silver   and   currentLevel.name = 'Gold' implies card.color = Color::gold</code></p>
     */
    after(tudresden.ocl20.pivot.examples.royalsandloyals.Membership aClass) : allMembershipConstructors(aClass) || allSetters(aClass) {
        /* Disable this constraint for subclasses of Membership. */
        if (aClass.getClass().getCanonicalName().equals("tudresden.ocl20.pivot.examples.royalsandloyals.Membership")) {
        if (!(!(!aClass.currentLevel.name.equals("Silver") || (aClass.card.color.equals(tudresden.ocl20.pivot.examples.royalsandloyals.Color.silver) && aClass.currentLevel.name.equals("Gold"))) || aClass.card.color.equals(tudresden.ocl20.pivot.examples.royalsandloyals.Color.gold))) {
        	// TODO Auto-generated code executed when constraint is violated.
        	String msg = "Error: Constraint 'levelAnColor' (inv levelAnColor:   currentLevel.name = 'Silver' implies card.color = Color::silver   and   currentLevel.name = 'Gold' implies card.color = Color::gold) was violated for Object " + aClass.toString() + ".";
        	throw new RuntimeException(msg);
        }
        // no else.
        }
        // no else.
    }
}