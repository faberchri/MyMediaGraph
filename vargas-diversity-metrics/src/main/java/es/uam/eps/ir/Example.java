package es.uam.eps.ir;

import es.uam.eps.ir.discount.DiscountModel;
import es.uam.eps.ir.discount.ExpDiscount;
import es.uam.eps.ir.discount.LogDiscount;
import es.uam.eps.ir.discount.NoDiscount;
import es.uam.eps.ir.discount.ZipfDiscount;
import es.uam.eps.ir.diversity.distance.DistanceModel;
import es.uam.eps.ir.diversity.metrics.NoveltyMetric;
import es.uam.eps.ir.diversity.novelty.ComplementNoveltyModel;
import es.uam.eps.ir.diversity.novelty.ExpectedNoveltyModel;
import es.uam.eps.ir.diversity.novelty.ListDistanceNovelty;
import es.uam.eps.ir.diversity.novelty.LogNoveltyModel;
import es.uam.eps.ir.diversity.novelty.NoveltyModel;
import es.uam.eps.ir.diversity.probability.GenericItemProbability;
import es.uam.eps.ir.diversity.probability.discovery.GenericItemDiscovery;
import es.uam.eps.ir.diversity.probability.discovery.GenericItemFreeDiscovery;
import es.uam.eps.ir.diversity.probability.relevance.ItemRelevanceProbability;
import es.uam.eps.ir.diversity.probability.relevance.RatingsRelativeItemExpRelevance;
import es.uam.eps.ir.diversity.probability.relevance.RatingsRelativeItemLinRelevance;
import es.uam.eps.ir.diversity.probability.relevance.UniformItemRelevance;

/**
 *
 * @author saul
 */
public class Example {

    public static void main(String[] args) {
        // Implement the interface Dataset and load your train and test data separately
        Dataset train = null;
        Dataset test = null;

        // Cut-off of the metric, note that for small cut-offs the rank discount model makes very little difference
        int cut = 50;
        
        // Implement the abstract methods in DistanceModel
        DistanceModel distm = null;

        // Rank discount model
        DiscountModel discm;
//        discm = new NoDiscount(); // No rank discount
//        discm = new LogDiscount(); // Logarithmic discount, as in nDCG
//        discm = new ZipfDiscount(); // Zipfian discount, as in ERR
//        discm = new ExpDiscount(0.85); // Exponential discount, as in RBP
        
        // Relevance model
        ItemRelevanceProbability relm;
//        relm = new UniformItemRelevance(); // No relevance
//        relm = new RatingsRelativeItemExpRelevance(test); // Exponential relevance, as in ERR
//        relm = new RatingsRelativeItemLinRelevance(test); // Linear relevance, as in 
        
        // Different novelty models, depending on the final metric
        NoveltyModel novm;
//        novm = new ComplementNoveltyModel(new GenericItemDiscovery(train)); // For EPC
//        novm = new LogNoveltyModel(new GenericItemDiscovery(train)); // For EIP
//        novm = new LogNoveltyModel(new GenericItemFreeDiscovery(train)); // For EFD
//        novm = new ExpectedNoveltyModel(distm, relm, train); // For EPD
//        novm = new ListDistanceNovelty(distm, discm, relm, cut); // For EILD

        // Now some specific instantiations of the metrics...
        
        // EPC with discount 0.85^(k - 1) and exponential relevance at cut 50
        novm = new ComplementNoveltyModel(new GenericItemDiscovery(train));
        discm = new ExpDiscount(0.85);
        relm = new RatingsRelativeItemExpRelevance(test);
        cut = 50;
        NoveltyMetric epc = new NoveltyMetric(novm, discm, relm, cut);

        // EPD with discount 0.85^(k - 1) and exponential relevance at cut 50
        distm = null; // your own class extending DistanceModel
        relm = new RatingsRelativeItemExpRelevance(test);
        novm = new ExpectedNoveltyModel(distm, relm, train);
        discm = new ExpDiscount(0.85);
        cut = 50;
        NoveltyMetric epd = new NoveltyMetric(novm, discm, relm, cut);       
        
        // I guess EPD as above is wrong. I think it should be:
        // EPD with discount 0.85^(k - 1) and exponential relevance at cut 50
        distm = null; // your own class extending DistanceModel
        relm = new RatingsRelativeItemExpRelevance(train);
        novm = new ExpectedNoveltyModel(distm, relm, train);
        discm = new ExpDiscount(0.85);
        cut = 50;
        relm = new RatingsRelativeItemExpRelevance(test);
        epd = new NoveltyMetric(novm, discm, relm, cut);        

        // EILD
        distm = null;
        relm = new RatingsRelativeItemExpRelevance(test);
        discm = new ExpDiscount(0.85);
        cut = 50;
        novm = new ListDistanceNovelty(distm, discm, relm, cut);
        NoveltyMetric eild = new NoveltyMetric(novm, discm, relm, cut);

    }
}
