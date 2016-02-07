// Copyright (C) 2013 Chris Newell
//
// This file is part of MyMediaLite.
//
// MyMediaLite is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// MyMediaLite is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with MyMediaLite.  If not, see <http://www.gnu.org/licenses/>.

package org.mymedialite;

import org.mymedialite.datatype.SparseMatrix;

/**
 * Interface for recommenders that take item attribute values into account
 * 
 * @author Chris Newell
 * @version 2.03
 */
public interface IItemAttributeValueAwareRecommender extends IRecommender {

  /**
   * Getter for the number of item attribute values
   */
  public int numItemAttributeValues();

  /**
   * Getter for item attribute values
   */
  public SparseMatrix<Float> getItemAttributeValues();

  /**
   * Setter for item attribute values
   */
  public void setItemAttributeValues(SparseMatrix<Float> s);
    
}
