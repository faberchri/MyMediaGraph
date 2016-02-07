package ch.uzh.ifi.ddis.mymedialite.main;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.io.IOUtils;
import org.apache.tools.ant.util.FileUtils;
import org.mymedialite.IItemAttributeAwareRecommender;
import org.mymedialite.IItemAttributeValueAwareRecommender;
import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.data.PosOnlyFeedback;
import org.mymedialite.datatype.IBooleanMatrix;
import org.mymedialite.datatype.SparseBooleanMatrix;
import org.mymedialite.datatype.SparseMatrix;
import org.mymedialite.eval.CandidateItems;
import org.mymedialite.eval.ItemRecommendationEvaluationResults;
import org.mymedialite.eval.ItemsParallel;
import org.mymedialite.eval.measures.IMeasure.AucAdapter;
import org.mymedialite.io.ItemData;
import org.mymedialite.itemrec.Extensions;
import org.mymedialite.itemrec.ItemKNN;
import org.mymedialite.itemrec.ItemRecommender;

import cern.colt.Arrays;


/**
 * Provides example recommendations from ItemRecommenders for subjective evaluation
 * using the MyMediaLite library.
 */
public class ItemRecommenderExamples extends ItemRecommenderEvaluate {

	public static final int RECOMMENDATIONS_PER_USER = 10;

	public static final String[] exampleUsers_MovieLens = new String[] {
			"1748", "3916"
	};

	public static final String[] recoms_MovieLens = new String[] {
			"BPRMF2{numFactors=100}",
			"Perfect",
			"MostPopular",
			"Random",
			"WeightedItemKNN{k=200}",
			"WeightedItemKNN{k=30}",
			"P3PopAbsNorm{leverage=0.0,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopAbsNorm{leverage=0.5,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopAbsNorm{leverage=0.6,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopAbsNorm{leverage=0.7,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopAbsNorm{leverage=0.8,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopAbsNorm{leverage=0.9,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopRankNorm{leverage=0.5,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopRankNorm{leverage=0.6,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopRankNorm{leverage=0.7,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopRankNorm{leverage=0.8,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopRankNorm{leverage=0.9,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3PopAbsNorm{leverage=1.5,numberOfTopItemsToConsiderForConvergenceTest=20,CONVERGENCE_INTERVAL_TEST=50000}",
			"P3AlphaMatrix{alpha=0.0}",
			"P3AlphaMatrix{alpha=1.0}",
			"P3AlphaMatrix{alpha=1.5}",
			"P3AlphaMatrix{alpha=1.7}",
			"P3AlphaMatrix{alpha=1.9}",
			"P3AlphaMatrix{alpha=2.1}",
	};

	public static String[] exampleUsers;
//--------------------------------------------------------------------------------------------------
	/**
	 * Usage:  ItemRecommenderEvaluate <recommender name> <data directory> <training data> <test data> [ <attributes> | "-" ] [ <attribute values> | "-" ] [ <model> ]
	 *
	 *   <recommender name>  name of the recommender to use as specified in recommenders map
	 *   <data directory>    path to the data directory
	 *   <training data>     training data filename
	 *   <test data>         testing data filename
	 *   <titles filename>   file mapping PIDs to title
	 *   <attributes>        attribute data filename      (required for IItemAttributeAwareRecommenders)
	 *   <attribute-values>  attribute-value data filename (required for IItemAttributeValueAwareRecommenders)
	 *   <model>             model filename                (optional: include to save or load an existing model)
	 */

	public static void main(String[] args) {

		exampleUsers = exampleUsers_MovieLens;

		String[] pass = new String[args.length + 1];
		for (int i = 0; i < args.length; i++) {
			pass[i + 1] = args[i];
		}

		for (String recom : recoms_MovieLens) {
			pass[0] = recom;
			run(pass);
		}
	}

	public static void run(String[] args) {
		System.out.println(Arrays.toString(args));
		directory = new File(args[1]);
		print("---- ItemRecommenderExamples ----");

//--------------------------------------------------------------------------------------------------
		// Load the training data.
		String trainingFile = (new File(directory, args[2])).getAbsolutePath();
		print("Training datafile: " + trainingFile);
		IPosOnlyFeedback trainingData = null;
		try {
			trainingData = ItemData.read(trainingFile, userMapping, itemMapping, false);
		} catch (Exception e) {
			String eMsg = "Unable to load training data " + trainingFile;
			throw new RuntimeException(eMsg, e);
		}

		double trainingSparsity = (double) trainingData.size() / ((long) trainingData.allUsers().size() * (long) trainingData.allItems().size());
		print("Training data: users: " + trainingData.allUsers().size() + " items: " + trainingData.allItems().size() + " feedback: " + trainingData.size() + " sparsity: " + decimalFormat.format(trainingSparsity));

//--------------------------------------------------------------------------------------------------
		// Load the test data.
		String testingFile = (new File(directory, args[3])).getAbsolutePath();
		print("Test datafile: " + testingFile);
		IPosOnlyFeedback testData = null;
		try {
			testData = ItemData.read(testingFile, userMapping, itemMapping, false);
		} catch (Exception e) {
			String eMsg = "Unable to load test data " + testingFile;
			throw new RuntimeException(eMsg, e);
		}
		double testingSparsity = (double) testData.size() / ((long) testData.allUsers().size() * (long) testData.allItems().size());
		print("Test data: users: " + testData.allUsers().size() + " items: " + testData.allItems().size() + " feedback: " + testData.size() + " sparsity: " + decimalFormat.format(testingSparsity));
//--------------------------------------------------------------------------------------------------
		// Load the programme titles
		String titlesFile = (new File(directory, args[5])).getAbsolutePath();;
		print("Titles filename: " + titlesFile);
		Map<String, String> titles = null;
		try {
			titles = getTitles(titlesFile);
		} catch (Exception e) {
			String eMsg = "Unable to load titles file " + titlesFile;
			throw new RuntimeException(eMsg, e);
		}
//--------------------------------------------------------------------------------------------------
		// instantiate the recommender and set properties
		String recommenderName = getRecommenderName(args[0]);
		ItemRecommender recommender = instantiateRecommender(recommenderName, testData);

		Properties recProperties = getRecommenderProperties(args[0]);
		setRecommenderProperties(recommender, recProperties);

		print("Recommender: " + recommender);
		recommender.setFeedback(trainingData);
//--------------------------------------------------------------------------------------------------
		SparseBooleanMatrix itemAttributes = loadItemAttributes(directory, args);
		if(recommender instanceof IItemAttributeAwareRecommender) {
			((IItemAttributeAwareRecommender)recommender).setItemAttributes(itemAttributes);
		}
		//--------------------------------------------------------------------------------------------------
		print("Training model ...");
		long trainingStart = System.currentTimeMillis();
		recommender.train();
		print("Training time: " + DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - trainingStart));
//--------------------------------------------------------------------------------------------------
		Int2IntMap[] degreeMaps = getDegreeMaps(trainingData, trainingData.allUsers(), trainingData.allItems());
		Int2IntMap userDegrees = degreeMaps[0];
		Int2IntMap itemDegrees = degreeMaps[1];

		Map<Integer, String> attributeStrings = getAttributeStrings(itemAttributes);
		// Produce recommendations for some example users.
		try {
			for(String externalUserId : exampleUsers) {
				//predictForUser(userId, recommender, trainingData, testData, titles);
				predictForUserToFile(externalUserId, recommender, trainingData, testData, titles,
						itemAttributes, directory, args[0], userDegrees, itemDegrees, attributeStrings);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	//--------------------------------------------------------------------------------------------------
	public static void predictForUser(int userId, ItemRecommender recommender, IPosOnlyFeedback trainingData, IPosOnlyFeedback testData, Map<String, String> titles) {
		String uid = userMapping.toOriginalID(userId);
		print("----------------------------------------");
		print("uid: " + uid);

		print("Training items:");
		IntCollection userTrainingItems = trainingData.userMatrix().get(userId);
		for(int userTrainingItem : userTrainingItems) {
			String pid = itemMapping.toOriginalID(userTrainingItem);
			String title = titles.get(pid);
			print(pid +  " " + title);
		}
		print("");

		print("Test items:");
		IntCollection userTestItems = testData.userMatrix().get(userId);
		for(int userTestItem : userTestItems) {
			String pid = itemMapping.toOriginalID(userTestItem);
			String title = titles.get(pid);
			print(pid +  " " + title);
		}
		print("");

		Collection<Integer> unwatchedItems = getUnwatchedItems(userId, trainingData, testData);
		List<Integer> recommendations = Extensions.predictItems(recommender, userId, unwatchedItems);
		int hitRate = 0;

		print("Recommendations:");
		for (int i = 0; i < RECOMMENDATIONS_PER_USER; i++) {
			int itemId = recommendations.get(i);
			if(userTestItems.contains(itemId)) hitRate++;
			String pid = itemMapping.toOriginalID(recommendations.get(i));
			String title = titles.get(pid);
			print(pid + " " + title);
		}

		print("Hit rate: " + hitRate);
	}

	public static void predictForUserToFile(String externalUserId,
											ItemRecommender recommender, IPosOnlyFeedback trainingData,
											IPosOnlyFeedback testData, Map<String, String> titles,
											IBooleanMatrix itemAttributes, File rootDir, String parsedRecommenderName,
											Int2IntMap userDegrees, Int2IntMap itemDegrees, Map<Integer, String> attributeStrings) throws FileNotFoundException {

		int userId = userMapping.toInternalID(externalUserId);

		StringBuilder trainStr = new StringBuilder();
		StringBuilder testStr = new StringBuilder();
		StringBuilder recommendationStr = new StringBuilder();

		String header = "id, title, attributes, popularityInTrain";
		trainStr.append(header);
		trainStr.append(System.lineSeparator());
		IntList userTrainingItems = new IntArrayList(trainingData.userMatrix().get(userId));
		Collections.sort(userTrainingItems);
		for(int userTrainingItem : userTrainingItems) {
			String pid = itemMapping.toOriginalID(userTrainingItem);
			String title = titles.get(pid);
			trainStr.append(pid);
			trainStr.append( ", ");
			trainStr.append(title);
			trainStr.append( ", ");
			trainStr.append(attributeStrings.get(userTrainingItem));
			trainStr.append( ", ");
			trainStr.append(itemDegrees.get(userTrainingItem));
			trainStr.append(System.lineSeparator());
		}

		header = "id, title, attributes, popularityInTrain";
		testStr.append(header);
		testStr.append(System.lineSeparator());
		IntList userTestItems = new IntArrayList(testData.userMatrix().get(userId));
		Collections.sort(userTestItems);
		for(int userTestItem : userTestItems) {
			String pid = itemMapping.toOriginalID(userTestItem);
			String title = titles.get(pid);
			testStr.append(pid);
			testStr.append( ", ");
			testStr.append(title);
			testStr.append( ", ");
			testStr.append(attributeStrings.get(userTestItem));
			testStr.append( ", ");
			testStr.append(itemDegrees.get(userTestItem));
			testStr.append(System.lineSeparator());
		}

		Collection<Integer> unwatchedItems = getUnwatchedItems(userId, trainingData, testData);
		List<Integer> recommendations = Extensions.predictItems(recommender, userId, unwatchedItems);

		header = "hit, rank, id, title, attributes, popularityInTrain";
		recommendationStr.append(header);
		recommendationStr.append(System.lineSeparator());
		for (int i = 0; i < recommendations.size(); i++) {
			int itemId = recommendations.get(i);
			if(userTestItems.contains(itemId) && userTrainingItems.contains(itemId)) {
				recommendationStr.append( "+/-, ");
			} else if(userTestItems.contains(itemId)) {
				recommendationStr.append( "+, ");
			} else if(userTrainingItems.contains(itemId)) {
				recommendationStr.append("-, ");
			} else {
				recommendationStr.append("0, ");
			}
			String pid = itemMapping.toOriginalID(recommendations.get(i));
			String title = titles.get(pid);
			recommendationStr.append(i + 1);
			recommendationStr.append( ", ");
			recommendationStr.append(pid);
			recommendationStr.append( ", ");
			recommendationStr.append(title);
			recommendationStr.append( ", ");
			recommendationStr.append(attributeStrings.get(itemId));
			recommendationStr.append( ", ");
			recommendationStr.append(itemDegrees.get(itemId));
			recommendationStr.append(System.lineSeparator());
		}

		String metrics = getMetrics(recommender, testData, trainingData, userId, unwatchedItems, itemAttributes);
		writeToFile(rootDir, externalUserId, trainStr.toString(), testStr.toString(), recommendationStr.toString(), metrics, parsedRecommenderName);
	}

	private static String getAttributeString(int itemId, IBooleanMatrix attributes){
		IntList attributeIds = attributes.getEntriesByRow(itemId);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attributeIds.size(); i++) {
			sb.append(attributeMapping.toOriginalID(attributeIds.get(i)));
			if (i < attributeIds.size() - 1){
				sb.append(" | ");
			}
		}
		return sb.toString();
	}

	private static Map<Integer, String> getAttributeStrings(IBooleanMatrix attributes){
		Map<Integer, String> map = new HashMap<>();
		for (int r = 0; r < attributes.numberOfRows(); r++){
			String attributeString = getAttributeString(r, attributes);
			map.put(r, attributeString);
		}
		return map;
	}

	public static String getMetrics(
			ItemRecommender recommender,
			IPosOnlyFeedback test,
			IPosOnlyFeedback training,
			int userId,
			Collection<Integer> candidate_items,
			IBooleanMatrix itemAttributes){
//	  IPosOnlyFeedback testFb = new PosOnlyFeedback<SparseBooleanMatrix>(SparseBooleanMatrix.class);
//	  for (int itemId : test) {
//		  testFb.add(userId, itemId);		
//	  }
		Collection<Integer> test_users = new ArrayList<>();
		test_users.add(userId);
		ItemRecommendationEvaluationResults result = ItemsParallel.evaluate(recommender, test, training, test_users, candidate_items, itemAttributes, CandidateItems.OVERLAP, false, null, null);
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Double> metric : result.entrySet()) {
			sb.append(metric.getKey());
			sb.append(",");
			sb.append(metric.getValue());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	public static void writeToFile(File rootDir, String uId, String train, String test, String recoms, String metrics, String parsedRecommenderName) throws FileNotFoundException{
		File root = new File(rootDir, "predictions");
		root = new File(root, uId);
		root = new File(root, parsedRecommenderName);
		root.mkdirs();
		printToFile(new File(root, "train.csv"), train);
		printToFile(new File(root, "test.csv"), test);
		printToFile(new File(root, "recommendations.csv"), recoms);
		printToFile(new File(root, "metrics.csv"), metrics);
	}

	static final void printToFile(File file, String string) throws FileNotFoundException{
		try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
			out.print(string);
		}
	}

	//--------------------------------------------------------------------------------------------------
	public static Map<String, String> getTitles(String filename) throws IOException {
		HashMap<String, String> titles = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.trim().length() == 0) continue;
			int index = line.indexOf(",");
			String pid = line.substring(0, index).trim();
			String title = line.substring(index + 1).trim();
			titles.put(pid, title);
		}
		reader.close();
		return titles;
	}
//--------------------------------------------------------------------------------------------------
	/**
	 * Get all items found in the training and test data, omitting any the user is known to have watched in the training data.
	 */
	public static IntCollection getUnwatchedItems(int userId, IPosOnlyFeedback trainingData, IPosOnlyFeedback testData) {
		IntCollection unwatchedItems = new IntArraySet();
		IntCollection userTrainingItems = trainingData.userMatrix().get(userId);

		for (Integer itemId : trainingData.allItems()) {
			if (!userTrainingItems.contains(itemId)) unwatchedItems.add(itemId);
		}

		for (Integer itemId : testData.allItems()) {
			if (!userTrainingItems.contains(itemId) && !unwatchedItems.contains(itemId)) unwatchedItems.add(itemId);
		}

		return unwatchedItems;
	}
//--------------------------------------------------------------------------------------------------
}
