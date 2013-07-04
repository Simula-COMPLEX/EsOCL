package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect Membership_DefAspect_getCurrentLevelName {

    /**
     * <p>Defines the method getCurrentLevelName() defined by the constraint
     * <code>context Membership
     *       def: getCurrentLevelName() : String = currentLevel.name</code></p>
     */
    public String tudresden.ocl20.pivot.examples.royalsandloyals.Membership.getCurrentLevelName( ) {
    	/* Self variable probably used within the definition. */
    	tudresden.ocl20.pivot.examples.royalsandloyals.Membership aClass = this;
    	
        return aClass.currentLevel.name;
    }
}