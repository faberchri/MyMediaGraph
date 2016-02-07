// Copyright (C) 2010 Zeno Gantner, Steffen Rendle
// Copyright (C) 2011 Zeno Gantner, Chris Newell
// Copyright (C) 2014 Zeno Gantner, Chris Newell, Fabian Christoffel
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

package org.mymedialite.eval;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mymedialite.data.IEntityMapping;
import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.datatype.IBooleanMatrix;
import org.mymedialite.eval.measures.IMeasure;
import org.mymedialite.eval.measures.IMeasure.*;

import org.mymedialite.eval.measures.IMeasureDump;
import org.mymedialite.eval.measures.diversity.CosineAttributeTypeItemSimilarity;
import org.mymedialite.eval.measures.diversity.DiversityInTopN;
import org.mymedialite.eval.measures.diversity.EntropyDiversity;
import org.mymedialite.eval.measures.diversity.GiniDiversity;
import org.mymedialite.eval.measures.diversity.HerfindahlDiversity;
import org.mymedialite.eval.measures.diversity.JaccardAttributeTypeItemSimilarity;
import org.mymedialite.eval.measures.diversity.IItemSimilarity;
import org.mymedialite.eval.measures.diversity.IntraListSimilarity;
import org.mymedialite.eval.measures.diversity.NormalizedDiversityInTopN;
import org.mymedialite.eval.measures.diversity.Personalization;
import org.mymedialite.eval.measures.diversity.SetDiversity;
import org.mymedialite.eval.measures.diversity.SurprisalNovelty;
import org.mymedialite.eval.measures.diversity.vargas.CombinedDiversityMetric;
import org.mymedialite.IRecommender;
import org.mymedialite.itemrec.Extensions;
import org.mymedialite.itemrec.Random;
import org.mymedialite.util.Utils;

/**
 * Evaluation class for item recommendation.
 * 
 * @version 2.03
 */
public class ItemsParallel {

	// this is a static class, but Java does not allow us to declare that ;-)
	private ItemsParallel() {
	}

	static public List<IMeasure> getIMeasures(int numberOfRecommendableItems) {
		List<IMeasure> measures = new ArrayList<>();

		measures.add(new NormalizedDiversityInTopN(numberOfRecommendableItems,
				5));
		measures.add(new NormalizedDiversityInTopN(numberOfRecommendableItems,
				10));
		measures.add(new NormalizedDiversityInTopN(numberOfRecommendableItems,
				20));

		measures.add(new GiniDiversity(numberOfRecommendableItems, 5));
		measures.add(new GiniDiversity(numberOfRecommendableItems, 10));
		measures.add(new GiniDiversity(numberOfRecommendableItems, 20));

		return measures;
	}

	static public List<IMeasure> getIMeasures() {
		List<IMeasure> measures = new ArrayList<>();
		measures.add(new AucAdapter());

		measures.add(new PrecAdapter(5));
		measures.add(new PrecAdapter(10));
		measures.add(new PrecAdapter(20));

		measures.add(new MapAdapter());

		measures.add(new RecallAdapter(5));
		measures.add(new RecallAdapter(10));
		measures.add(new RecallAdapter(20));

		measures.add(new NdcgAdapter());

		measures.add(new MrrAdapter());

		measures.add(new DiversityInTopN(5));
		measures.add(new DiversityInTopN(10));
		measures.add(new DiversityInTopN(20));

		measures.add(new EntropyDiversity(5));
		measures.add(new EntropyDiversity(10));
		measures.add(new EntropyDiversity(20));

		measures.add(new HerfindahlDiversity(5));
		measures.add(new HerfindahlDiversity(10));
		measures.add(new HerfindahlDiversity(20));

		measures.add(new Personalization(5));
		measures.add(new Personalization(10));
		measures.add(new Personalization(20));

		return measures;
	}

	static public List<IMeasure> getIMeasures(IBooleanMatrix attributesMatrix) {
		List<IMeasure> measures = new ArrayList<>();

		IItemSimilarity jaccard = new JaccardAttributeTypeItemSimilarity(
				attributesMatrix);
		IItemSimilarity cosine = new CosineAttributeTypeItemSimilarity(
				attributesMatrix);
		
		measures.add(new IntraListSimilarity(jaccard, 5));
		measures.add(new IntraListSimilarity(jaccard, 10));
		measures.add(new IntraListSimilarity(jaccard, 20));

		measures.add(new IntraListSimilarity(cosine, 5));
		measures.add(new IntraListSimilarity(cosine, 10));
		measures.add(new IntraListSimilarity(cosine, 20));

		measures.add(new SetDiversity(jaccard, 5));
		measures.add(new SetDiversity(jaccard, 10));
		measures.add(new SetDiversity(jaccard, 20));

		measures.add(new SetDiversity(cosine, 5));
		measures.add(new SetDiversity(cosine, 10));
		measures.add(new SetDiversity(cosine, 20));

		return measures;
	}

	static public List<IMeasure> getIMeasures(IPosOnlyFeedback training) {
		// create item degrees map / popularity
		Map<Integer, Integer> itemPopularity = new HashMap<>();
		IBooleanMatrix itemM = training.itemMatrix();
		for (int i = 0; i < itemM.numberOfRows(); i++) {
			itemPopularity.put(i, itemM.numEntriesByRow(i));
		}

		int numberOfUsers = itemM.numberOfColumns();

		List<IMeasure> measures = new ArrayList<>();
		measures.add(new SurprisalNovelty(itemPopularity, numberOfUsers, 5));
		measures.add(new SurprisalNovelty(itemPopularity, numberOfUsers, 10));
		measures.add(new SurprisalNovelty(itemPopularity, numberOfUsers, 20));

		return measures;
	}

	static public List<IMeasure> getDistanceBasedCombinedDiversityIMeasures(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			IBooleanMatrix itemAttributes, Collection<Integer> testUsers,
			Collection<Integer> candidateItems) {
		List<IMeasure> measures = new ArrayList<>();

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountJaccardEPD(train, test,
						testUsers, candidateItems, itemAttributes, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountJaccardEPD(train, test,
						testUsers, candidateItems, itemAttributes, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountJaccardEPD(train, test,
						testUsers, candidateItems, itemAttributes, 20, 0.85));

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountCosineEPD(train, test,
						testUsers, candidateItems, itemAttributes, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountCosineEPD(train, test,
						testUsers, candidateItems, itemAttributes, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountCosineEPD(train, test,
						testUsers, candidateItems, itemAttributes, 20, 0.85));

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountJaccardEILD(train, test,
						testUsers, candidateItems, itemAttributes, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountJaccardEILD(train, test,
						testUsers, candidateItems, itemAttributes, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountJaccardEILD(train, test,
						testUsers, candidateItems, itemAttributes, 20, 0.85));

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountCosineEILD(train, test,
						testUsers, candidateItems, itemAttributes, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountCosineEILD(train, test,
						testUsers, candidateItems, itemAttributes, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountCosineEILD(train, test,
						testUsers, candidateItems, itemAttributes, 20, 0.85));

		return measures;
	}

	static public List<IMeasure> getCombinedDiversityIMeasures(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems) {
		List<IMeasure> measures = new ArrayList<>();

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEPC(train, test,
						testUsers, candidateItems, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEPC(train, test,
						testUsers, candidateItems, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEPC(train, test,
						testUsers, candidateItems, 20, 0.85));

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEIP(train, test,
						testUsers, candidateItems, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEIP(train, test,
						testUsers, candidateItems, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEIP(train, test,
						testUsers, candidateItems, 20, 0.85));

		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEFD(train, test,
						testUsers, candidateItems, 5, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEFD(train, test,
						testUsers, candidateItems, 10, 0.85));
		measures.add(CombinedDiversityMetric
				.getLinearRelevanceExponentialDiscountEFD(train, test,
						testUsers, candidateItems, 20, 0.85));

		return measures;
	}

	/**
	 * Evaluation for rankings of items in parallel.
	 * 
	 * User-item combinations that appear in both sets are ignored for the test
	 * set, and thus in the evaluation, except when the boolean argument
	 * repeated_events is set.
	 * 
	 * The evaluation measures are listed in the ItemPredictionMeasures
	 * property. Additionally, 'num_users' and 'num_items' report the number of
	 * users that were used to compute the results and the number of items that
	 * were taken into account.
	 * 
	 * Literature: C. Manning, P. Raghavan, H. Sch&uuml;tze: Introduction to
	 * Information Retrieval, Cambridge University Press, 2008
	 * 
	 * @param recommender
	 *            item recommender
	 * @param test
	 *            test cases
	 * @param training
	 *            training data
	 * @param test_users
	 *            a collection of integers with all relevant users
	 * @param candidate_items
	 *            a collection of integers with all relevant items
	 * @param candidate_item_mode
	 *            the mode used to determine the candidate items. The default is
	 *            CandidateItems.OVERLAP
	 * @param repeated_events
	 *            allow repeated events in the evaluation (i.e. items accessed
	 *            by a user before may be in the recommended list). The default
	 *            is false.
	 * @return a dictionary containing the evaluation results
	 */
	public static ItemRecommendationEvaluationResults evaluate(
			IRecommender recommender, IPosOnlyFeedback test,
			IPosOnlyFeedback training, Collection<Integer> test_users,
			Collection<Integer> candidate_items, IBooleanMatrix itemAttributes,
			CandidateItems candidate_item_mode, Boolean repeated_events,
            IEntityMapping userMapping, File dumpLocation) {
		return evaluate(recommender, test, training, test_users, candidate_items,
                itemAttributes, candidate_item_mode, repeated_events, null,
                userMapping, dumpLocation);
	}

	/**
	 * Evaluation for rankings of items in parallel.
	 * 
	 * User-item combinations that appear in both sets are ignored for the test
	 * set, and thus in the evaluation, except when the boolean argument
	 * repeated_events is set.
	 * 
	 * The evaluation measures are listed in the ItemPredictionMeasures
	 * property. Additionally, 'num_users' and 'num_items' report the number of
	 * users that were used to compute the results and the number of items that
	 * were taken into account.
	 * 
	 * Literature: C. Manning, P. Raghavan, H. Sch&uuml;tze: Introduction to
	 * Information Retrieval, Cambridge University Press, 2008
	 * 
	 * @param recommender
	 *            item recommender
	 * @param test
	 *            test cases
	 * @param training
	 *            training data
	 * @param test_users
	 *            a collection of integers with all relevant users
	 * @param candidate_items
	 *            a collection of integers with all relevant items
	 * @param candidate_item_mode
	 *            the mode used to determine the candidate items. The default is
	 *            CandidateItems.OVERLAP
	 * @param repeated_events
	 *            allow repeated events in the evaluation (i.e. items accessed
	 *            by a user before may be in the recommended list). The default
	 *            is false.
	 * @return a dictionary containing the evaluation results
	 */
	public static ItemRecommendationEvaluationResults evaluate(
			IRecommender recommender, IPosOnlyFeedback test,
			IPosOnlyFeedback training, Collection<Integer> test_users,
			Collection<Integer> candidate_items, IBooleanMatrix itemAttributes,
			CandidateItems candidate_item_mode, Boolean repeated_events,
            List<IMeasure> measures, IEntityMapping userMapping, File dumpLocation) {

		if (candidate_item_mode == null)
			candidate_item_mode = CandidateItems.OVERLAP;
		if (repeated_events == null)
			repeated_events = false;

		if (candidate_item_mode.equals(CandidateItems.TRAINING)) {
			candidate_items = training.allItems();
		} else if (candidate_item_mode.equals(CandidateItems.TEST)) {
			candidate_items = test.allItems();
		} else if (candidate_item_mode.equals(CandidateItems.OVERLAP)) {
			candidate_items = Utils.intersect(test.allItems(),
					training.allItems());
		} else if (candidate_item_mode.equals(CandidateItems.UNION)) {
			candidate_items = Utils.union(test.allItems(), training.allItems());
	    }  else if(candidate_item_mode.equals(CandidateItems.EXPLICIT)) {
	    	if (candidate_items == null)
	    		throw new IllegalArgumentException("candidate_items == null!");
	    }

		if (test_users == null)
			test_users = test.allUsers();

		int num_users = 0;
		ItemRecommendationEvaluationResults result = new ItemRecommendationEvaluationResults();

		IBooleanMatrix training_user_matrix = training.userMatrix();
		IBooleanMatrix test_user_matrix = test.userMatrix();

		// compile the complete measures list if no measures injected
		if (measures == null) {
			measures = getIMeasures();
			measures.addAll(getIMeasures(candidate_items.size()));
			measures.addAll(getCombinedDiversityIMeasures(training, test,
					test_users, candidate_items));
			if (itemAttributes != null) {
				measures.addAll(getIMeasures(itemAttributes));
				// the rival measures are very memory demanding and do not
				// provide any new metrics for now
				// measures.addAll(getRivalIMeasures(training, itemAttributes,
				// candidate_items));
				measures.addAll(getDistanceBasedCombinedDiversityIMeasures(
						training, test, itemAttributes, test_users,
						candidate_items));
			}
			measures.addAll(getIMeasures(training));
		}

        if (userMapping != null && dumpLocation != null){
            measures = wrapMeasuresIntoDumpMeasures(userMapping, measures, dumpLocation);
        }

		ExecutorService executor = Executors.newFixedThreadPool(Runtime
				.getRuntime().availableProcessors());
		// ExecutorService executor = Executors.newFixedThreadPool(1);
		ArrayList<Future<ItemRecommendationEvaluationResults>> futures = new ArrayList<>();

		for (Integer user_id : test_users) {
			// Items viewed by the user in the test set that were also present
			// in the training set.
			HashSet<Integer> correct_items = new HashSet<Integer>(
					Utils.intersect(test_user_matrix.get(user_id),
							candidate_items));

			// The number of items that will be used for this user.
			HashSet<Integer> candidate_items_in_train = new HashSet<Integer>(
					Utils.intersect(training_user_matrix.get(user_id),
							candidate_items));

			int num_eval_items = candidate_items.size()
					- (repeated_events ? 0 : candidate_items_in_train.size());

			// Skip all users that have 0 or #relevant_items test items.
			if (correct_items.size() == 0)
				continue;
			if (num_eval_items - correct_items.size() == 0)
				continue;

			EvaluationTask task = new EvaluationTask(recommender,
					candidate_items, repeated_events, training_user_matrix,
					user_id, correct_items, measures);

			futures.add(executor.submit(task));

			num_users++;
		}

		executor.shutdown();
		int completedFutures = 0;
		for (Future<ItemRecommendationEvaluationResults> future : futures) {
			try {
				ItemRecommendationEvaluationResults futureResult = future.get();

				for (IMeasure m : measures) {
					result.put(m.getName(), result.get(m.getName())
							+ futureResult.get(m.getName()));
				}

				completedFutures++;

				// or: printStatus(completedFutures);
				printIntermediateResult(completedFutures, futures.size(),
						result, measures);

			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(
						"Exception in executor service for parallel evaluation.",
						e);
			}
		}

		if (num_users > 1000)
			System.out.println();

		for (IMeasure measure : measures) {
			String mId = measure.getName();
			result.put(mId, measure.normalize(result.get(mId), num_users));
		}

		result.put("num_users", (double) num_users);
		// result.put("num_lists", (double) num_users);
		result.put("num_items", (double) candidate_items.size());

        if (userMapping != null && dumpLocation != null){
            dumpMeasures(measures);
        }

		return result;
	}

    private static List<IMeasure> wrapMeasuresIntoDumpMeasures(
            IEntityMapping userMapping,List<IMeasure> measures, File dumpLocation){
        List<IMeasure> list = new ArrayList<>();
        for(IMeasure m : measures){
            list.add(new IMeasureDump.PerUserMeasureDump(userMapping, m, dumpLocation));
        }
        return list;
    }

    private static void dumpMeasures(List<IMeasure> measures){
        for(IMeasure m : measures){
            ((IMeasureDump) m).dump();
        }
    }

	private static void printIntermediateResult(int completedFutures,
			int numOfFutures, ItemRecommendationEvaluationResults result,
			List<IMeasure> measures) {
		if (completedFutures % 10 == 0) {
			StringBuilder sb = new StringBuilder();
			if (completedFutures % Integer.MAX_VALUE == 0) {
				DecimalFormat f = new DecimalFormat("##0.0000");
				sb.append("| ");
				for (IMeasure measure : measures) {
					if (measure.intermediateCalculationAllowed()) {
						String mId = measure.getName();
						double v = measure.normalize(result.get(mId),
								(double) completedFutures);
						sb.append(mId);
						sb.append(": ");
						sb.append(f.format(v));
						sb.append(" | ");
					}
				}
				sb.append("\n");
			}
			sb.append(completedFutures);
			sb.append(" of ");
			sb.append(numOfFutures);
			sb.append(" calculation tasks completed.");
			System.out.println(sb.toString());
		}
	}

	private static class EvaluationTask implements
			Callable<ItemRecommendationEvaluationResults> {

		private final IRecommender recommender;
		private final Collection<Integer> candidate_items;
		private final Boolean repeated_events;

		private final IBooleanMatrix training_user_matrix;
		private final Integer user_id;

		private final HashSet<Integer> correct_items;

		private final List<IMeasure> measures;

		private EvaluationTask(IRecommender recommender,
				Collection<Integer> candidate_items, Boolean repeated_events,
				IBooleanMatrix training_user_matrix, Integer user_id,
				HashSet<Integer> correct_items, List<IMeasure> measures) {
			this.recommender = recommender;
			this.candidate_items = candidate_items;
			this.repeated_events = repeated_events;
			this.training_user_matrix = training_user_matrix;
			this.user_id = user_id;
			this.correct_items = correct_items;
			this.measures = measures;
		}

		private final ItemRecommendationEvaluationResults result = new ItemRecommendationEvaluationResults();

		@Override
		public ItemRecommendationEvaluationResults call() throws Exception {

			List<Integer> randomItems = Extensions.predictItems(new Random(),
					user_id, candidate_items);
			List<Integer> prediction_list = Extensions.predictItems(
					recommender, user_id, randomItems);

			if (prediction_list.size() != candidate_items.size())
				throw new RuntimeException("Not all items have been ranked.");

			Collection<Integer> ignore_items = repeated_events ? new ArrayList<Integer>()
					: training_user_matrix.get(user_id);

			for (IMeasure m : measures) {
				result.put(m.getName(), m.compute(user_id, prediction_list,
						correct_items, ignore_items));
			}

			return result;
		}

	}

	/**
	 * Format item prediction results.
	 * 
	 * @param result
	 *            the result dictionary
	 * @return a string containing the results
	 */
	public static String formatResults(Map<String, Double> result,
			List<String> measureIdentifiers) {
		StringBuilder sb = new StringBuilder();
		for (String measureIdentifier : measureIdentifiers) {
			sb.append(measureIdentifier);
			sb.append(" ");
			sb.append(result.get(measureIdentifier));
		}
		return sb.toString();
	}

	/**
	 * Display item prediction results.
	 * 
	 * @param result
	 *            the result dictionary
	 */
	static public void displayResults(HashMap<String, Double> result,
			List<String> measureIdentifiers) {
		for (String measureIdentifier : measureIdentifiers) {
			System.out.println(measureIdentifier + "\t"
					+ result.get(measureIdentifier));
		}
		System.out.println("num_users  " + result.get("num_users"));
		System.out.println("num_items  " + result.get("num_items"));
		System.out.println("num_lists  " + result.get("num_lists"));
	}

}