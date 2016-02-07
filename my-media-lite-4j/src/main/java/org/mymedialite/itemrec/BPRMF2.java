// Copyright (C) 2010 Zeno Gantner, Christoph Freudenthaler
// Copyright (C) 2011 Zeno Gantner
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

package org.mymedialite.itemrec;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;

import org.mymedialite.datatype.MatrixExtensions;
import org.mymedialite.util.Random;

/** 
 * Matrix factorization model for item prediction optimized using BPR-Opt.
 * In proceedings:
 *   @author Steffen Rendle
 *   @author Christoph Freudenthaler
 *   @author Zeno Gantner
 *   @author Lars Schmidt-Thieme
 *   BPR: Bayesian Personalized Ranking from Implicit Feedback
 *   Proceedings of the 25th Conference on Uncertainty in Artificial Intelligence (UAI 2009)
 *   Montreal, Canada, 2009
 *   
 * {@see http://portal.acm.org/citation.cfm?id=1795114.1795167 }
 */
public class BPRMF2 extends MF {
  
  /** Fast, but memory-intensive sampling */
  protected boolean fast_sampling = false;

  /** Fast sampling memory limit, in MiB 
   * TODO find out why fast sampling does not improve performance
   */
   //public int fast_sampling_memory_limit = 128;
  public int fast_sampling_memory_limit = 0;
  
  /** Use the first item latent factor as a bias term if set to true */
  public boolean item_bias = false;

  /** One iteration is iteration_length * number of entries in the training matrix */
  public int iteration_length = 5;

  /** Learning rate alpha */
  public double learn_rate = 0.05;

  /** Regularization parameter for positive item factors */
  public double reg_i = 0.0025;  // 0.005;

  /** Regularization parameter for negative item factors */
  public double reg_j = 0.00025;  // 0.001;

  /** Regularization parameter for user factors */
  public double reg_u = 0.0025; // 0.005;;

  /** support data structure for fast sampling */
  protected ArrayList<int[]> user_pos_items;
  
  /** support data structure for fast sampling */
  protected ArrayList<int[]> user_neg_items;

  /** Random number generator */
  protected org.mymedialite.util.Random random = Random.getInstance();
  
  /** {@inheritDoc} */
  public void train() {
    random = Random.getInstance();
    //checkSampling();

    // if necessary, set the bias counterparts to 1
    if (item_bias) {
      userFactors.setColumnToOneValue(0, 1.0);
    }
    super.train();
  }
  
  /** 
   * Perform one iteration of stochastic gradient ascent over the training data.
   * One iteration is iteration_length * number of entries in the training matrix
   */
  public void iterate() {
    int num_pos_events = feedback.size();
  
    for (int i=0; i<num_pos_events * iteration_length; i++) {
      SampleTriple triple = sampleTriple();      
      updateFactors(triple.u, triple.i, triple.j, true, true, true);
    }
  }
 
  /** 
   * Sample another item, given the first one and the user
   * @param triple a SampleTriple consisting of a user ID and two item IDs
   * @return true if the given item was already seen by the user
   */  
  protected boolean sampleOtherItem(SampleTriple triple) {
    IntCollection userItems = feedback.userMatrix().get(triple.u);
    boolean itemIsPositive = userItems.contains(triple.i);
    if (fast_sampling) {
      if (itemIsPositive) {
        int rindex = random.nextInt(0, user_neg_items.get(triple.u).length);
        triple.j = user_neg_items.get(triple.u)[rindex];
      } else {
        int rindex = random.nextInt(0, user_pos_items.get(triple.u).length);
        triple.j = user_pos_items.get(triple.u)[rindex];
      }
    } else {
      do {
        triple.j = random.nextInt(0, maxItemID + 1);
      } while (userItems.contains(triple.j) != itemIsPositive);
    }
    return itemIsPositive;
  }
 
  /**  
   * Sample a pair of items, given a user
   * @param triple a SampleTriple consisting of a user ID and two item IDs
   */
  protected void sampleItemPair(SampleTriple triple) {
    if (fast_sampling) {
      int rindex = random.nextInt(0, user_pos_items.get(triple.u).length);
      triple.i = user_pos_items.get(triple.u)[rindex];

      rindex = random.nextInt (0, user_neg_items.get(triple.u).length);
      triple.j = user_neg_items.get(triple.u)[rindex];
    } else {
      IntCollection user_items = feedback.userMatrix().get(triple.u);
      triple.i = user_items.toIntArray()[random.nextInt (0, user_items.size())];
      do {
        triple.j = random.nextInt (0, maxItemID + 1);
      } while (user_items.contains(triple.j));
    }
  }
  
  /**  
   * Sample a user that has viewed at least one and not all items.
   * @return the user ID
   */
  protected int sampleUser() {
    while (true) {
      int u = random.nextInt(0, maxUserID + 1);
      IntCollection user_items = feedback.userMatrix().get(u);
      if (user_items.size() == 0 || user_items.size() == maxItemID + 1) continue;
      return u;
    }
  }
  
  /**
   * Sample a triple for BPR learning.
   * @return a SampleTriple consisting of a user ID and two item IDs
   */
  protected SampleTriple sampleTriple() {
    SampleTriple triple = new SampleTriple();
    triple.u = sampleUser();
    sampleItemPair(triple);
    return triple;
  }
  
  /** 
   * Update features according to the stochastic gradient descent update rule.
   * @param u the user ID
   * @param i the ID of the first item
   * @param j the ID of the second item
   * @param update_u if true, update the user features
   * @param update_i if true, update the features of the first item
   * @param update_j if true, update the features of the second item
   */
  protected void updateFactors(int u, int i, int j, boolean update_u, boolean update_i, boolean update_j) {
    double x_uij = predict(u, i) - predict(u, j);

    int start_factor = 0;
    if (item_bias) {
      start_factor = 1;

      double w_uf = userFactors.get(u, 0);
      double h_if = itemFactors.get(i, 0);
      double h_jf = itemFactors.get(j, 0);

      if (update_i) {
        double if_update = w_uf / (1 + Math.exp(x_uij)) - reg_i * h_if;
        itemFactors.set(i, 0, h_if + learn_rate * if_update);
      }

      if (update_j) {
        double jf_update = -w_uf / (1 + Math.exp(x_uij)) - reg_j * h_jf;
        itemFactors.set(j, 0, h_jf + learn_rate * jf_update);
      }
    }
   
    for (int f=start_factor; f<numFactors; f++) {
      double w_uf = userFactors.get(u, f);
      double h_if = itemFactors.get(i, f);
      double h_jf = itemFactors.get(j, f);

      if (update_u) {
        double uf_update = (h_if - h_jf) / (1 + Math.exp(x_uij)) - reg_u * w_uf;
        userFactors.set(u, f, w_uf + learn_rate * uf_update);
      }
      
      if (update_i) {
        double if_update = w_uf / (1 + Math.exp(x_uij)) - reg_i * h_if;
        itemFactors.set(i, f, h_if + learn_rate * if_update);
      }

      if (update_j) {
        double jf_update = -w_uf / (1 + Math.exp(x_uij)) - reg_j * h_jf;
        itemFactors.set(j, f, h_jf + learn_rate * jf_update);
      }
    }
  }

  /** {@inheritDoc} */
  public void addFeedback(int user_id, int item_id) {
    super.addFeedback(user_id, item_id);
    if (fast_sampling)  createFastSamplingData(user_id);
    // retrain
    retrainUser(user_id);
    //retrainItem(item_id);
  }

  /** {@inheritDoc} */
  public void addFeedback(int user_id, List<Integer> item_ids) {
    for(int item_id : item_ids) { 
      super.addFeedback(user_id, item_id);
    }
    if (fast_sampling)  createFastSamplingData(user_id);
    // retrain
    retrainUser(user_id);
    //retrainItem(item_id);
  }
  
  /** {@inheritDoc} */
  public void removeFeedback(int user_id, int item_id) {
    super.removeFeedback(user_id, item_id);
    if (fast_sampling)  createFastSamplingData(user_id);
    // retrain
    retrainUser(user_id);
    //retrainItem(item_id);
  }

  /** {@inheritDoc} */
  public void addUser(int user_id) {
    if (user_id > maxUserID) {
      userFactors.addRows(user_id + 1);
      MatrixExtensions.rowInitNormal(userFactors, user_id, initMean, initStDev);
    }
    super.addUser(user_id);
  }

  /** {@inheritDoc} */
  public void addItem(int item_id) {
    if (item_id > maxItemID) {
      itemFactors.addRows(item_id + 1);
      MatrixExtensions.rowInitNormal(itemFactors, item_id, initMean, initStDev);
    }
    super.addItem(item_id);
  }

  /** {@inheritDoc} */
  public void removeUser(int user_id) {
    super.removeUser(user_id);
    if (fast_sampling) {
      user_pos_items.set(user_id, null);
      user_neg_items.set(user_id, null);
    }
    // set user latent factors to zero
    userFactors.setRowToOneValue(user_id, 0.0);
  }

  /** {@inheritDoc} */
  public void removeItem(int item_id) {
    super.removeItem(item_id);
    // TODO remove from fast sampling data structures
    //   (however: not needed if all feedback events have been removed properly before)

    // set item latent factors to zero
    itemFactors.setRowToOneValue(item_id, 0.0);
  }

  /**
   * Retrain the latent factors of a given user.
   * @param user_id the user ID
   */
  protected void retrainUser(int user_id) {
    MatrixExtensions.rowInitNormal(userFactors, user_id, initMean, initStDev);
    IntCollection user_items = feedback.userMatrix().get(user_id);
    //for (int i = 0; i < user_items.size() * iteration_length * num_iter; i++) {    // AUC: 0.929
    for (int i = 0; i < user_items.size() * numIter; i++) {                         // AUC: 0.929
    //for (int i = 0; i < user_items.size() * iteration_length; i++) {               // AUC: 0.921
    //for (int i = 0; i < user_items.size(); i++) {                                  // AUC: 0.857
      SampleTriple triple = new SampleTriple();
      triple.u = user_id;
      sampleItemPair(triple);
      updateFactors(triple.u, triple.i, triple.j, true, false, false);
    }
  }

  /**
   * Retrain the latent factors of a given item.
   * @param item_id the item ID
   */
  protected void retrainItem(int item_id) {
    MatrixExtensions.rowInitNormal(itemFactors, item_id, initMean, initStDev);
    int num_pos_events = feedback.userMatrix().numberOfEntries();
    int num_item_iterations = num_pos_events * iteration_length * numIter / (maxItemID + 1);
    for (int i = 0; i < num_item_iterations; i++) {
      // remark: the item may be updated more or less frequently than in the normal from-scratch training
      int user_id = sampleUser();
      SampleTriple triple = new SampleTriple();
      triple.u = sampleUser();
      triple.i = item_id;
      boolean item_is_positive = sampleOtherItem(triple);

      if (item_is_positive) {
        updateFactors(user_id, item_id, triple.j, false, true, false);
      } else {
        updateFactors(user_id, triple.j, item_id, false, false, true);
      }
    }
  }
  
  /**  
   * Compute approximate fit (AUC on training data)
   *  @return the fit
   */
  public double computeLoss() {
    double sum_auc = 0;
    int num_user = 0;

    for (int user_id = 0; user_id < maxUserID + 1; user_id++) {
      IntCollection test_items = feedback.userMatrix().get(user_id);
      if (test_items.size() == 0) continue;
      
      List<Integer> prediction = Extensions.predictItems(this, user_id, maxItemID);

      int num_eval_items = maxItemID + 1;
      int num_eval_pairs = (num_eval_items - test_items.size()) * test_items.size();

      int num_correct_pairs = 0;
      int num_pos_above = 0;
      // start with the highest weighting item...
      for (int i = 0; i < prediction.size(); i++) {
        int item_id = prediction.get(i);

        if (test_items.contains(item_id)) {
          num_pos_above++;
        } else {
          num_correct_pairs += num_pos_above;
        }
      }
      double user_auc = ((double)num_correct_pairs) / num_eval_pairs;
      sum_auc += user_auc;
      num_user++;
    }

    double auc = sum_auc / num_user;
    return auc;
  }

  protected void createFastSamplingData(int u) {
    while (u >= user_pos_items.size()) user_pos_items.add(null);
    while (u >= user_neg_items.size()) user_neg_items.add(null);
      
    user_pos_items.set(u, feedback.userMatrix().get(u).toIntArray());
    
    IntSet neg_list = new IntArraySet();    
    for (int i=0; i < maxItemID; i++) {
      if (!feedback.userMatrix().get(u).contains(i)) neg_list.add(i);
    }
    user_neg_items.set(u, neg_list.toIntArray());
  }

  protected void checkSampling() {
    try {
      int fast_sampling_memory_size = ((maxUserID + 1) * (maxItemID + 1) * 4) / (1024 * 1024);
      System.out.println("fast_sampling_memory_size=" + fast_sampling_memory_size);
      
      if (fast_sampling_memory_size <= fast_sampling_memory_limit) {
        fast_sampling = true;
        this.user_pos_items = new ArrayList<int[]>(maxUserID + 1);
        this.user_neg_items = new ArrayList<int[]>(maxUserID + 1);
        for (int u = 0; u < maxUserID + 1; u++)
          createFastSamplingData(u);
      }
    } catch (Exception e) {
      System.out.println("fast_sampling_memory_size=TOO_MUCH");
      // Do nothing - don't use fast sampling
    }
  }
  
  private class SampleTriple {
    int u;  // user_id
    int i;  // item_id_1
    int j;  // item_id_2
  }

@Override
public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(this.getClass().getName());
	builder.append(" fast_sampling=");
	builder.append(fast_sampling);
	builder.append(" fast_sampling_memory_limit=");
	builder.append(fast_sampling_memory_limit);
	builder.append(" item_bias=");
	builder.append(item_bias);
	builder.append(" iteration_length=");
	builder.append(iteration_length);
	builder.append(" learn_rate=");
	builder.append(learn_rate);
	builder.append(" reg_i=");
	builder.append(reg_i);
	builder.append(" reg_j=");
	builder.append(reg_j);
	builder.append(" reg_u=");
	builder.append(reg_u);
	builder.append(" initMean=");
	builder.append(initMean);
	builder.append(" initStDev=");
	builder.append(initStDev);
	builder.append(" numFactors=");
	builder.append(numFactors);
	builder.append(" numIter=");
	builder.append(numIter);
	return builder.toString();
}
  


}