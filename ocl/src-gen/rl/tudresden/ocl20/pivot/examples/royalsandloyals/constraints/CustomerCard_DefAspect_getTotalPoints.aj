package tudresden.ocl20.pivot.examples.royalsandloyals.constraints;

/**
 * <p>Generated Aspect to enforce OCL constraint.</p>
 *
 * @author OCL22Java of Dresden OCL2 for Eclipse
 * @Generated
 */
public privileged aspect CustomerCard_DefAspect_getTotalPoints {

    /**
     * <p>Defines the method getTotalPoints(tudresden.ocl20.pivot.examples.royalsandloyals.Date d) defined by the constraint
     * <code>context CustomerCard
     *       def: getTotalPoints(d: Date) : Integer = transactions->select(date.isAfter(d)).points->sum()</code></p>
     */
    public Integer tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard.getTotalPoints(tudresden.ocl20.pivot.examples.royalsandloyals.Date d) {
    	/* Self variable probably used within the definition. */
    	tudresden.ocl20.pivot.examples.royalsandloyals.CustomerCard aClass = this;
    	
        java.util.HashSet<tudresden.ocl20.pivot.examples.royalsandloyals.Transaction> result2;
        result2 = new java.util.HashSet<tudresden.ocl20.pivot.examples.royalsandloyals.Transaction>();
        
        /* Iterator Select: Select all elements which fulfill the condition. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Transaction anElement1 : aClass.transactions) {
            if (anElement1.date.isAfter(d)) {
                result2.add(anElement1);
            }
            // no else
        }
        java.util.ArrayList<Integer> result1;
        result1 = new java.util.ArrayList<Integer>();
        
        /* Iterator Collect: Iterate through all elements and collect them. Elements which are collections are flattened. */
        for (tudresden.ocl20.pivot.examples.royalsandloyals.Transaction anElement2 : result2) {
            result1.add(anElement2.points);
        }
    
        return new Integer(tudresden.ocl20.pivot.tools.codegen.ocl2java.types.util.OclCollections.sum(result1).intValue());
    }
}