package ch.uzh.ifi.ddis.mymedialite.main;

import es.uam.eps.ir.Example;
import it.unimi.dsi.fastutil.ints.Int2DoubleArrayMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


import net.vidageek.mirror.dsl.Mirror;

import org.apache.commons.digester.SetRootRule;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.stat.StatUtils;
import org.mymedialite.IItemAttributeAwareRecommender;
import org.mymedialite.IItemAttributeValueAwareRecommender;
import org.mymedialite.IRecommender;
import org.mymedialite.data.EntityMapping;
import org.mymedialite.data.IEntityMapping;
import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.datatype.IBooleanMatrix;
import org.mymedialite.datatype.IMatrix;
import org.mymedialite.datatype.SparseBooleanMatrix;
import org.mymedialite.datatype.SparseMatrix;
import org.mymedialite.eval.CandidateItems;
import org.mymedialite.eval.ItemRecommendationEvaluationResults;
import org.mymedialite.eval.Items;
import org.mymedialite.eval.ItemsParallel;
import org.mymedialite.io.AttributeData;
import org.mymedialite.io.ItemData;
import org.mymedialite.itemrec.BPRMF;
import org.mymedialite.itemrec.BPRMF2;
import org.mymedialite.itemrec.ItemAttributeKNN;
import org.mymedialite.itemrec.ItemAttributeValueKNN;
import org.mymedialite.itemrec.ItemKNN;
import org.mymedialite.itemrec.ItemRecommender;
import org.mymedialite.itemrec.MostPopular;
import org.mymedialite.itemrec.Perfect;
import org.mymedialite.itemrec.SoftMarginRankingMF;
import org.mymedialite.itemrec.WRMF;
import org.mymedialite.itemrec.WeightedBPRMF;
import org.mymedialite.itemrec.WeightedItemAttributeKNN;
import org.mymedialite.itemrec.WeightedItemAttributeValueKNN;
import org.mymedialite.itemrec.WeightedItemKNN;
import org.mymedialite.itemrec.Random;
import org.mymedialite.itemrec.WeightedUserKNN;
import org.mymedialite.itemrec.Worst;
import org.mymedialite.util.Utils;

import com.google.common.primitives.Doubles;

import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.AbsoluteNormalizedItemBasedPureCF3Path;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.AbsoluteNormalizedPureCF3Path;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.ItemAttributeCF3Path;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.ItemBasedPureCF3Path;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.PureCF3Path;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.ItemAttribute3Path;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.AverageSimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.MedianSimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.PersonalizedPageRankGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.ShowPopularityNormalizedSampledRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.SimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.UserDependingMedianRankShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.UserDependingRankedShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.UserDependingShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender;
import ch.uzh.ifi.ddis.mymedialite.graph.pathcount.experiment.UserDependingTotalShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender;


import org.apache.commons.lang.time.DurationFormatUtils;

/**
 * General purpose training and evaluation program for ItemRecommenders
 * using the MyMediaLite library.
 */
public class ItemRecommenderEvaluate {

    private static final Map<String, Class<? extends ItemRecommender>> recommenders;
    static
    {

        // always use the same seed for the mymedialites random number generator
        org.mymedialite.util.Random.initInstance(1234567890L);

        recommenders = new HashMap<>();
        recommenders.put("Perfect".toLowerCase(), 						Perfect.class);
        recommenders.put("Worst".toLowerCase(), 						Worst.class);
        recommenders.put("Random".toLowerCase(), 						Random.class);
        recommenders.put("MostPopular".toLowerCase(), 					MostPopular.class);
        recommenders.put("ItemKNN".toLowerCase(), 						ItemKNN.class);
        recommenders.put("WeightedItemKNN".toLowerCase(), 				WeightedItemKNN.class);
        recommenders.put("WeightedUserKNN".toLowerCase(), 				WeightedUserKNN.class);
        recommenders.put("ItemAttributeKNN".toLowerCase(), 				ItemAttributeKNN.class);
        recommenders.put("WeightedItemAttributeKNN".toLowerCase(), 		WeightedItemAttributeKNN.class);
        recommenders.put("WeightedItemAttributeValueKNN".toLowerCase(), WeightedItemAttributeValueKNN.class);
        recommenders.put("BPRMF".toLowerCase(), 						BPRMF.class);
        recommenders.put("BPRMF2".toLowerCase(), 						BPRMF2.class);
        recommenders.put("SoftMarginRankingMF".toLowerCase(), 			SoftMarginRankingMF.class);
        recommenders.put("WeightedBPRMF".toLowerCase(), 				WeightedBPRMF.class);
        recommenders.put("WRMF".toLowerCase(), 							WRMF.class);
        recommenders.put("3Path".toLowerCase(), 						PureCF3Path.class);
        recommenders.put("IACF-3Path".toLowerCase(),	 				ItemAttributeCF3Path.class);
        recommenders.put("IA-3Path".toLowerCase(),						ItemAttribute3Path.class);
        recommenders.put("AN-3Path".toLowerCase(),						AbsoluteNormalizedPureCF3Path.class);
        recommenders.put("SPN-SRCFGR".toLowerCase(),					ShowPopularityNormalizedSampledRankedCollaborativeFilteringGraphRecommender.class);
        recommenders.put("IB-3Path".toLowerCase(),						ItemBasedPureCF3Path.class);
        recommenders.put("AN-IB-3Path".toLowerCase(),					AbsoluteNormalizedItemBasedPureCF3Path.class);
        recommenders.put("UDRSPNIB-RCFGR".toLowerCase(),				UserDependingRankedShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender.class);
        recommenders.put("UDTSPNIB-RCFGR".toLowerCase(),				UserDependingTotalShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender.class);
        recommenders.put("UDMRSPNIB-RCFGR".toLowerCase(),				UserDependingMedianRankShowPopularityNormalizedItemBasedRankedCollaborativeFilteringGraphRecommender.class);
        recommenders.put("ASRCIB-RCFGR".toLowerCase(),					AverageSimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender.class);
        recommenders.put("MSRCIB-RCFGR".toLowerCase(),					MedianSimilarityRankCombiningItemBasedRankedCollaborativeFilteringGraphRecommender.class);
        recommenders.put("PPRGR".toLowerCase(),							PersonalizedPageRankGraphRecommender.class);
        recommenders.put("P3RandomWalkTree".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree.P3.class);
        recommenders.put("P3RandomWalkCached".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3.class);
        recommenders.put("P3Probability".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.P3.class);
        recommenders.put("P5RandomWalkTree".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree.P5.class);
        recommenders.put("P5RandomWalkCached".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P5.class);
        // recommenders.put("P3AlphaRandomWalkTree".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.tree.P3Alpha.class);
        recommenders.put("P3AlphaRandomWalkCached".toLowerCase(),		ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3Alpha.class);
        recommenders.put("P3AlphaProbability".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.P3Alpha.class);

        // recommenders.put("P3AlphaRandomWalkCachedExp".toLowerCase(),		ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.P3AlphaExperimental.class);
        recommenders.put("P3LowDegree".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.LowDegreeP3.class);
        recommenders.put("P3LowDegreeDyn".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.DynamicLowDegreeP3.class);
        recommenders.put("P3LowDegreeDynBin".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.BinarySearchDynamicLowDegreeP3.class);
        recommenders.put("P3PopAbsNorm".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.P3PAN.class);
        recommenders.put("P3PopRankNorm".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.P3PRN.class);
        recommenders.put("P5PopAbsNorm".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.P5PAN.class);
        recommenders.put("P5PopRankNorm".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.probability.normalized.P5PRN.class);
        recommenders.put("P3PersPopAbsNorm".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3PersonalizedPopularityAbsoluteNormalized.class);
        recommenders.put("P3SimplePopAbsNorm".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3SimplePersonalizedPopularityAbsoluteNormalized.class);
        recommenders.put("P3PersPopRankNorm".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3PersonalizedPopularityRankNormalized.class);
        recommenders.put("P3AutoPopAbsNorm".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3AutoPopularityAbsoluteNormalized.class);
        recommenders.put("P3EstPopAbsNorm".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3EstPopularityAbsoluteNormalized.class);
        recommenders.put("P3EstPopRankNorm".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3EstPopularityRankNormalized.class);
        recommenders.put("P3PopAbsInvExpNorm".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3PopularityAbsoluteInvertedExponentialNormalized.class);
        recommenders.put("P3PopAbsBPowerNorm".toLowerCase(),			ch.uzh.ifi.ddis.mymedialite.graph.cooper.randomwalk.cached.experiment.P3PopularityAbsoluteBoundedPowerNormalized.class);


        recommenders.put("P3Matrix".toLowerCase(),						ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix.P3.class);
        recommenders.put("P5Matrix".toLowerCase(),						ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix.P5.class);
        recommenders.put("P3AlphaMatrix".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix.P3Alpha.class);
        recommenders.put("P5AlphaMatrix".toLowerCase(),					ch.uzh.ifi.ddis.mymedialite.graph.cooper.matrix.P5Alpha.class);
        // recommenders.put("OneWayBruteForce".toLowerCase(),				ch.uzh.ifi.ddis.mymedialite.graph.fouss.OneWayBruteForce.class);
        // recommenders.put("OneWayIntermediateCounts".toLowerCase(), 		ch.uzh.ifi.ddis.mymedialite.graph.fouss.OneWayIntermediateCounts.class);
        recommenders.put("OneWay".toLowerCase(), 						ch.uzh.ifi.ddis.mymedialite.graph.fouss.OneWay.class);
        recommenders.put("OneWayLim".toLowerCase(), 					ch.uzh.ifi.ddis.mymedialite.graph.fouss.limited.OneWay.class);
        recommenders.put("Commute".toLowerCase(), 						ch.uzh.ifi.ddis.mymedialite.graph.fouss.Commute.class);
        recommenders.put("BnB".toLowerCase(), 							ch.uzh.ifi.ddis.mymedialite.graph.huang.BranchAndBound.class);
        recommenders.put("RBnB".toLowerCase(), 							ch.uzh.ifi.ddis.mymedialite.graph.huang.ReactivatingBranchAndBound.class);
        recommenders.put("ItemRankMatrix".toLowerCase(), 				ch.uzh.ifi.ddis.mymedialite.graph.gori.ItemRankMatrix.class);
        recommenders.put("ItemRankMatrixPrec".toLowerCase(), 			ch.uzh.ifi.ddis.mymedialite.graph.gori.ItemRankMatrixPrecalculated.class);
        recommenders.put("ItemRankRandomWalk".toLowerCase(), 			ch.uzh.ifi.ddis.mymedialite.graph.gori.ItemRankRandomWalk.class);

        recommenders.put("ProbeS".toLowerCase(), 			            ch.uzh.ifi.ddis.mymedialite.graph.zhou.ProbS.class);
        recommenders.put("HeatS".toLowerCase(), 			            ch.uzh.ifi.ddis.mymedialite.graph.zhou.HeatS.class);
        recommenders.put("HybridS".toLowerCase(), 			            ch.uzh.ifi.ddis.mymedialite.graph.zhou.Hybrid.class);
        recommenders.put("HybridSRw".toLowerCase(), 			        ch.uzh.ifi.ddis.mymedialite.graph.zhou.randomwalk.HybridShort.class);

        recommenders.put("Laplacian".toLowerCase(), 					ch.uzh.ifi.ddis.mymedialite.graph.fouss.LaplacianPseudoinverse.class);
        recommenders.put("OneWayM".toLowerCase(), 						ch.uzh.ifi.ddis.mymedialite.graph.fouss.matrix.OneWay.class);

        recommenders.put("BmANP3".toLowerCase(), 					    ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark.ANP3.class);
        recommenders.put("BmHybridS".toLowerCase(), 					ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark.HybridS.class);
        recommenders.put("BmP3Alpha".toLowerCase(), 					ch.uzh.ifi.ddis.mymedialite.graph.rwbenchmark.P3Alpha.class);


    }

    private static final Set<Class<? extends ItemRecommender>> injectTestDataInConstructor;
    static
    {
        injectTestDataInConstructor = new HashSet<>();
        injectTestDataInConstructor.add(Perfect.class);
        injectTestDataInConstructor.add(Worst.class);
    }

    public static final String METRICS_LOG = "METRICS.csv";

    protected static File directory;
    protected static IEntityMapping userMapping = new EntityMapping();
    protected static IEntityMapping itemMapping = new EntityMapping();
    protected static IEntityMapping attributeMapping = new EntityMapping();
    protected static DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    protected static DecimalFormat integerFormat = new DecimalFormat("0");

//--------------------------------------------------------------------------------------------------
    /**
     * Usage:  ItemRecommenderEvaluate <recommender name> <data directory> <training data> <test data> [ <attributes> | "-" ] [ <attribute values> | "-" ] [ <model> ]
     *
     *   <recommender name>  name of the recommender to use as specified in recommenders map
     *   <data directory>    path to the data directory
     *   <training data>     training data filename
     *   <test data>         testing data filename
     *   <attributes>        attribute data filename      (required for IItemAttributeAwareRecommenders)
     *   <attribute-values>  attribute-value data filename (required for IItemAttributeValueAwareRecommenders)
     *   <model>             model filename                (optional: include to save or load an existing model)
     */
    public static void main(String[] args) {
        directory = new File(args[1]);
        print("---- ItemRecommenderEvaluate ----");

//--------------------------------------------------------------------------------------------------
        // Load the training data.
        String trainingFile = (new File(directory, args[2])).getAbsolutePath();
        print("Training datafile: " + trainingFile);
        IPosOnlyFeedback trainingData = null;
        try {
            trainingData = ItemData.read(trainingFile, userMapping, itemMapping, false);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load training data " + trainingFile, e);
        }
        print("Training data: users: " + trainingData.allUsers().size() + " items: " + trainingData.allItems().size() + " feedback: " + trainingData.size());
        print("Train Data Stats: " + getStatistics(trainingData));
//--------------------------------------------------------------------------------------------------
        // Load the test data.
        String testingFile = (new File(directory, args[3])).getAbsolutePath();
        print("Test datafile: " + testingFile);
        IPosOnlyFeedback testData = null;
        try {
            testData = ItemData.read(testingFile, userMapping, itemMapping, false);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load test data " + testingFile, e);
        }
        print("Test data: users: " + testData.allUsers().size() + " items: " + testData.allItems().size() + " feedback: " + testData.size());
        print("Test Data Stats: " + getStatistics(testData));
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
        if(recommender instanceof IItemAttributeValueAwareRecommender) {
            // Load attribute-value data
            SparseMatrix<Float> itemAttributeValues = null;
            if(args.length > 5 && args[5] != "-") {
                String attributeValuesFile = (new File(directory, args[5])).getAbsolutePath();
                print("Item attribute-values file: " + attributeValuesFile);
                try {
                    //itemAttributeValues = AttributeValueData.read(attributeValuesFile, itemMapping, attributeMapping);
                    throw new RuntimeException("Attribute value data are not supported in open source release.");
                } catch (Exception e) {
                    printError("Unable to load attribute data " + attributeValuesFile, e);
                }
                print("Item Attributes: items: " + itemAttributeValues.numberOfRows() + " item-attribute-values: " + itemAttributeValues.numberOfNonEmptyEntries());
            } else {
                printError("No item attribute file specified.");
            }
            ((IItemAttributeValueAwareRecommender)recommender).setItemAttributeValues(itemAttributeValues);
        }
//--------------------------------------------------------------------------------------------------
        // Load a saved model (if filename specified and it exists) or train the model (and save if filename specified)
        long startTraining = System.currentTimeMillis();
        String trainingDuration = null;
        if(args.length > 6) {
            String modelFile = (new File(directory, args[6])).getAbsolutePath();
            print("Model filename: " + modelFile);
            File file = new File(modelFile);
            if(file.exists()) {
                try {
                    print("Loading model ...");
                    recommender.loadModel(modelFile);
                    trainingDuration = DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startTraining) + " (loading, not training)";
                    print("Loading duration: " + trainingDuration);
                } catch (Exception e) {
                    printError("Unable to load model " + modelFile, e);
                }
            } else {
                print("Training model ...");
                recommender.train();
                trainingDuration = DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startTraining);
                print("Training duration: " + trainingDuration);
                try {
                    recommender.saveModel(modelFile);
                } catch (Exception e) {
                    printError("Unable to save model " + modelFile, e);
                }
            }
        } else {
            print("Training model ...");
            recommender.train();
            trainingDuration = DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - startTraining);
            print("Training duration: " + trainingDuration);
        }
//--------------------------------------------------------------------------------------------------
        // Measure the prediction accuracy against the test data.
        long evaluationStart = System.currentTimeMillis();
        Collection<Integer> relevant_users = trainingData.allUsers();  // Users that will be taken into account in the evaluation.
        Collection<Integer> relevant_items = trainingData.allItems();  // Items that will be taken into account in the evaluation.
        try {
            ItemRecommendationEvaluationResults results = ItemsParallel.evaluate(recommender, testData, trainingData, relevant_users,
                    relevant_items, itemAttributes, CandidateItems.OVERLAP, false, userMapping, new File(directory, args[0]+"-dump"));
            long evaluationEnd = System.currentTimeMillis();
            for (String k : results.keySet()){
                print(k + "\t" + results.getPrettyPrintedValue(k));
            }
            String evaluationDuration = DurationFormatUtils.formatDurationHMS(evaluationEnd - evaluationStart);
            print("Evaluation duration: " + evaluationDuration);
            recordMetrics(recommender, results, trainingDuration, evaluationDuration);
        } catch (Exception e) {
            throw new RuntimeException("Exception during evaluation.", e);
        }
    }
    //--------------------------------------------------------------------------------------------------
    public static void recordMetrics(Object recommender, ItemRecommendationEvaluationResults results, String trainingTime,
                                     String evaluationTime) throws IOException {
        File file = new File(directory, METRICS_LOG);

        // we need to lock file access to make sure all parallel processes can write to the file
        @SuppressWarnings("resource")
        FileChannel channel = new RandomAccessFile(file, "rws").getChannel();

        // Use the file channel to create a lock on the file.
        // This method blocks until it can retrieve the lock.
        FileLock lock = channel.lock();
        try {
            ByteBuffer buf = ByteBuffer.allocate(5000);
            // first check if the file contains already the header, we simply check if file is empty
            if (channel.size() < 1){
                // no header in file
                String header = getMetricsFileHeader(results);
                header = header + System.lineSeparator();
                // from http://tutorials.jenkov.com/java-nio/file-channel.html
                byte[] bytes = header.getBytes();
                buf.put(bytes);
                buf.flip();
                while(buf.hasRemaining()) {
                    channel.write(buf);
                }
                buf.clear();
            }
            // set the channels position to the end, so that we append and not insert at beginning
            channel.position(channel.size());
            // now append data
            StringBuilder sb = new StringBuilder();
            sb.append(recommender.getClass().getName());
            for (String k : results.keySet()){
                sb.append(",");
                sb.append(results.getPrettyPrintedValue(k));
            }
            sb.append(",");
            sb.append(trainingTime);
            sb.append(",");
            sb.append(evaluationTime);
            sb.append(",");
            sb.append(getCleansedRecommenderToSring(recommender));
            sb.append(",");
            sb.append(getEnvironmentDescription());
            sb.append(",");
            sb.append(getTimeStamp());
            sb.append(System.lineSeparator());
            String data = sb.toString();

            // append to file
            // from http://tutorials.jenkov.com/java-nio/file-channel.html
            byte[] bytes = data.getBytes();
            buf.put(bytes);
            buf.flip();
            while(buf.hasRemaining()) {
                channel.write(buf);
            }
        } finally {
            // Release the lock in any case
            lock.release();
            // ... and close the file
            channel.close();
        }
    }

    private static String getCleansedRecommenderToSring(Object recommender) {
        String s =recommender.toString();
        s = s.replace(System.lineSeparator(), " | ");
        return s.replace(",", " - ");
    }

    private static String getMetricsFileHeader(ItemRecommendationEvaluationResults results) {
        StringBuilder sb = new StringBuilder();
        sb.append("Recommender,");
        for (String m : results.keySet()){
            sb.append(m);
            sb.append(",");
        }
        sb.append("TrainingTime[s],EvaluationTime[s],RecommenderToString,Environment,Timestamp");
        return sb.toString();
    }

    public static String getEnvironmentDescription(){
        StringBuilder sb = new StringBuilder();
        sb.append("AvailableProcessors: ");
        sb.append(Runtime.getRuntime().availableProcessors());
        sb.append(" | TotalMemory (bytes): ");
        sb.append(Runtime.getRuntime().totalMemory());
        sb.append(" | OS: ");
        Properties props = System.getProperties();
        sb.append(props.get("os.name"));
        sb.append(" - ");
        sb.append(props.get("os.arch"));
        sb.append(" - ");
        sb.append(props.get("os.version"));
        return sb.toString();
    }

    public static String getTimeStamp() {
        Date d = new Date();
        SimpleDateFormat timeStampFormatter = new SimpleDateFormat("yyyy.MMM.dd-HH:mm:ss-z");
        return timeStampFormatter.format(d);
    }

    public static SparseBooleanMatrix loadItemAttributes(File directory, String[] args){
        // Load attribute data
        SparseBooleanMatrix itemAttributes = null;
        if(args.length > 4 && args[4] != "-") {
            String attributesFile = (new File(directory, args[4])).getAbsolutePath();
            try {
                itemAttributes = AttributeData.read(attributesFile, itemMapping, attributeMapping);
            } catch (Exception e) {
                printError("Unable to load attribute data " + attributesFile, e);
            }
            print("Item Attributes: items: " + itemAttributes.numberOfRows() + " attributes: "
                    + itemAttributes.numberOfColumns() + " item-attributes: " + itemAttributes.numberOfEntries());
        } else {
            printError("No item attribute file specified.");
        }
        return itemAttributes;
    }

    public static String getRecommenderName(String arg){
        arg = arg.replaceAll("^(.*)\\{(.*)\\}.*$", "$1");

        // remove all whitespaces in properties string
        arg = arg.replaceAll("\\s", "");
        return arg;
    }

    public static Properties getRecommenderProperties(String arg) {
        Properties p = new Properties();

        // get curly braces enclosed part of name -> properties
        String r1 = arg.replaceAll("^.*\\{(.*)\\}.*$", "$1");

        // check if we replaced something, i.e. '{..}' is present
        if (r1.equals(arg)){
            return p;
        }

        // remove all whitespaces in properties string
        arg = r1.replaceAll("\\s", "");

        // convert  ',' to line break
        arg = arg.replaceAll(",", System.lineSeparator());

        try {
            p.load(new StringReader(arg));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    protected static void setRecommenderProperties(Object recommender, Properties p) {

        for (Map.Entry e : p.entrySet()) {
            Object v = convertPropertyValueToType((String) e.getValue());
            new Mirror().on(recommender).set().field((String)e.getKey()).withValue(v);
        }

    }

    public static Object convertPropertyValueToType(String v) {
        if (v == null | v.isEmpty()){
            throw new IllegalArgumentException("Parameter value is null or empty.");

        }

        if (v.equalsIgnoreCase("true")){
            return true;
        }
        if (v.equalsIgnoreCase("false")){
            return false;
        }

        try {
            return Integer.parseInt(v);

        } catch (NumberFormatException e) {
            // nothing todo
        }

        try {
            return Double.parseDouble(v);

        } catch (NumberFormatException e) {
            // nothing todo
        }

        return v;
    }

    protected static ItemRecommender instantiateRecommender(String recommenderName, IPosOnlyFeedback testData) {
        Class<? extends ItemRecommender> recommenderClass = recommenders.get(recommenderName.toLowerCase());
        if (recommenderClass == null) {
            throw new IllegalArgumentException("The specified recommender name '" + recommenderName + "' is not known.");
        }
        ItemRecommender recommender = null;
        try {
            if (injectTestDataInConstructor.contains(recommenderClass)){
                Constructor<? extends ItemRecommender> constructor = recommenderClass.getConstructor(IPosOnlyFeedback.class);
                recommender = constructor.newInstance(testData);
            } else {
                recommender = recommenderClass.newInstance();
            }
        } catch(NoSuchMethodException | SecurityException | InvocationTargetException | InstantiationException | IllegalAccessException e){
            throw new RuntimeException("Exception while instantiating recommender ('" + recommenderName + "') with reflection.", e);
        }
        return recommender;
    }

    protected static Int2IntMap[] getDegreeMaps(IPosOnlyFeedback fb, IntList users, IntList items){

        Int2IntMap userDegreesM = new Int2IntOpenHashMap();
        userDegreesM.defaultReturnValue(0);
        Int2IntMap itemDegreesM = new Int2IntOpenHashMap();
        itemDegreesM.defaultReturnValue(0);

        IBooleanMatrix m = fb.userMatrix();
        for (int u : users) {
            for (int i : items) {
                if (m.get(u, i)){
                    int pD = userDegreesM.get(u);
                    userDegreesM.put(u, pD + 1);
                    pD = itemDegreesM.get(i);
                    itemDegreesM.put(i, pD + 1);
                }
            }
        }
        return new Int2IntMap[] {userDegreesM, itemDegreesM};
    }


    private static String getStatistics(IPosOnlyFeedback fb){
        IntList users = fb.allUsers();
        IntList items = fb.allItems();

        int usersC = users.size();
        int itemsC = items.size();

        Int2IntMap[] degreeMaps = getDegreeMaps(fb, users, items);
        Int2IntMap userDegreesM = degreeMaps[0];
        Int2IntMap itemDegreesM = degreeMaps[1];

        double[] userDegrees = Doubles.toArray(userDegreesM.values());
        double[] itemDegrees = Doubles.toArray(itemDegreesM.values());

        double sum = StatUtils.sum(itemDegrees);

        assert sum == StatUtils.sum(userDegrees);
        assert sum == fb.size();

        double minU = StatUtils.min(userDegrees);
        double maxU = StatUtils.max(userDegrees);

        double minI = StatUtils.min(itemDegrees);
        double maxI = StatUtils.max(itemDegrees);

        double medianU = StatUtils.percentile(userDegrees, 50.0);
        double medianI = StatUtils.percentile(itemDegrees, 50.0);

        double gMeanU = StatUtils.geometricMean(userDegrees);
        double gMeanI = StatUtils.geometricMean(itemDegrees);

        double aMeanU = StatUtils.mean(userDegrees);
        double aMeanI = StatUtils.mean(itemDegrees);

        double sparsity = (double)sum / (double)(usersC * itemsC);

        NumberFormat formatter = new DecimalFormat("0.00E0");

        String header = "\n----\n\tusers\titems\tsize\tminU\tmaxU\tminI\tmaxI\tmedianU\tmedianI\tgMeanU\tgMeanI\taMeanU\taMeanI\tsparsity\n\t";
        StringBuilder sb = new StringBuilder(header);
        sb.append(formatter.format(usersC));
        sb.append("\t");
        sb.append(formatter.format(itemsC));
        sb.append("\t");
        sb.append(formatter.format(sum));
        sb.append("\t");
        sb.append(formatter.format(minU));
        sb.append("\t");
        sb.append(formatter.format(maxU));
        sb.append("\t");
        sb.append(formatter.format(minI));
        sb.append("\t");
        sb.append(formatter.format(maxI));
        sb.append("\t");
        sb.append(formatter.format(medianU));
        sb.append("\t");
        sb.append(formatter.format(medianI));
        sb.append("\t");
        sb.append(formatter.format(gMeanU));
        sb.append("\t");
        sb.append(formatter.format(gMeanI));
        sb.append("\t");
        sb.append(formatter.format(aMeanU));
        sb.append("\t");
        sb.append(formatter.format(aMeanI));
        sb.append("\t");
        sb.append(formatter.format(sparsity));
        sb.append("\n----");

        return sb.toString();
    }

    protected static void printError(String msg){
        System.err.println(msg);
    }

    protected static void printError(String msg, Exception e){
        System.err.println(msg);
        System.err.println(e.toString());
        e.printStackTrace();
    }

    protected static void print(String msg){
        System.out.println(msg);
    }

//--------------------------------------------------------------------------------------------------
}