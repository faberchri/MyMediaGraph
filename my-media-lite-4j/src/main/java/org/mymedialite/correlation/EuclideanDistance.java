package org.mymedialite.correlation;

import java.util.Map;
import java.util.Set;
import org.mymedialite.correlation.CorrelationMatrix;
import org.mymedialite.datatype.SparseMatrix;

/**
 * Correlation class for Cosine correlation.
 * http://en.wikipedia.org/wiki/Cosine_similarity
 * @version 2.03
 */

public class EuclideanDistance extends AttributeValueCorrelationMatrix {

  /**
   * Constructor. Create a EuclideanDistance based correlation matrix.
   * @param numEntities the number of entities
   */
  public EuclideanDistance(int numEntities) {
    super(numEntities);
  }

  /**
   * Create a EuclideanDistance based correlation matrix from given data.
   * @param attributeValues the attribute values
   * @return the complete EuclideanDistance based correlation matrix
   */
  public static CorrelationMatrix create(SparseMatrix<Float> attributeValues) {
    EuclideanDistance cm;
    int numEntities = attributeValues.numberOfRows();

    try {
      cm = new EuclideanDistance(numEntities);
    } catch (OutOfMemoryError e) {
      System.err.println("Too many entities: " + numEntities);
      throw e;
    }
    cm.computeCorrelations(attributeValues);
    return cm;
  }

  /**
   * Compute correlations between two entities for given ratings.
   * @param ratings the rating data
   * @param entityType the entity type, either USER or ITEM
   * @param i the ID of first entity
   * @param j the ID of second entity
   * @param shrinkage the shrinkage parameter
   */
  public static float computeCorrelation(SparseMatrix<Float> attributeValues, int i, int j) {
    if (i == j) return 1.0F;

    Map<Integer, Float> attributeValues_i = attributeValues.get(i);
    Map<Integer, Float> attributeValues_j = attributeValues.get(j);
    
    Set<Integer> attributeIds_i = attributeValues_i.keySet();
    Set<Integer> attributeIds_j = attributeValues_j.keySet();

    // Get common attribute Ids for the two items
    attributeIds_i.retainAll(attributeIds_j);    
    double sumSq = 0.0;

    for (int attribute_id : attributeIds_i) {
      // Get the attribute value for each item
      double value_i = attributeValues_i.get(attribute_id);
      double value_j = attributeValues_j.get(attribute_id);
      
      double diff = value_i - value_j;
      sumSq += diff * diff;
      //System.out.println(ri + " " + rj + " " + sumSq);
    }

    double distance = Math.sqrt(sumSq); 
    double similarity = 1 / (1 + distance);
    return (float)similarity;
  }

  /**
   * Compute correlations for given ratings.
   * @param ratings the rating data
   * @param entityType the entity type, either USER or ITEM
   */
  public void computeCorrelations(SparseMatrix<Float> attributeValues) {

    // The diagonal of the correlation matrix
    for (int i = 0; i < numEntities; i++) {
      set(i, i, 1.0F);
    }
    
    for (int i = 0; i < numEntities; i++) {
      for (int j = i + 1; j < numEntities; j++) {
        float correlation = computeCorrelation(attributeValues, i, j);
        this.set(i, j, correlation);
      }
    }
  }

}

