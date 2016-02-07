//Copyright (C) 2013 Chris Newell
//
//This file is part of MyMediaLite.
//
//MyMediaLite is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//MyMediaLite is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with MyMediaLite.  If not, see <http://www.gnu.org/licenses/>.

package org.mymedialite.itemrec;

import org.mymedialite.IItemAttributeValueAwareRecommender;
import org.mymedialite.correlation.EuclideanDistance;
import org.mymedialite.datatype.SparseMatrix;

/**
 * k-nearest neighbor item-based collaborative filtering using Euclidean Distance similarity
 * over the item attribute values.
 * 
 * This recommender does NOT support incremental updates.
 * @version 2.03
 */
public class ItemAttributeValueKNN extends ItemKNN implements IItemAttributeValueAwareRecommender {

  private SparseMatrix<Float> itemAttributeValues;

  @Override
  public SparseMatrix<Float> getItemAttributeValues() {
    return itemAttributeValues;
  }

  @Override
  public void setItemAttributeValues(SparseMatrix<Float> itemAttributeValues) {
    this.itemAttributeValues = itemAttributeValues;
    this.maxItemID = itemAttributeValues.numberOfRows() - 1;
  }

  @Override
  public int numItemAttributeValues() {
    return itemAttributeValues.numberOfColumns();
  }

  @Override
  public void train() {
    this.correlation = EuclideanDistance.create(itemAttributeValues);
    int num_items = maxItemID + 1;
    this.nearest_neighbors = new int[num_items][];
    for (int i = 0; i < num_items; i++)
      nearest_neighbors[i] = correlation.getNearestNeighbors(i, k);
  }

  @Override
  public String toString() {
    return "ItemAttributeValueKNN k=" + (k == Integer.MAX_VALUE ? "inf" : Integer.toString(k));
  }
  
}

