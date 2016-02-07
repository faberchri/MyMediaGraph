package org.mymedialite.eval.measures.diversity.vargas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mymedialite.data.IPosOnlyFeedback;
import org.mymedialite.datatype.IBooleanMatrix;
import org.mymedialite.eval.measures.IMeasure;
import org.mymedialite.eval.measures.IMeasure.TopNMeasure;
import org.mymedialite.eval.measures.diversity.CosineAttributeTypeItemSimilarity;
import org.mymedialite.eval.measures.diversity.IItemSimilarity;
import org.mymedialite.eval.measures.diversity.JaccardAttributeTypeItemSimilarity;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import es.uam.eps.ir.Dataset;
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
import es.uam.eps.ir.diversity.probability.discovery.GenericItemDiscovery;
import es.uam.eps.ir.diversity.probability.discovery.GenericItemFreeDiscovery;
import es.uam.eps.ir.diversity.probability.relevance.ItemRelevanceProbability;
import es.uam.eps.ir.diversity.probability.relevance.RatingsRelativeItemExpRelevance;
import es.uam.eps.ir.diversity.probability.relevance.RatingsRelativeItemLinRelevance;
import es.uam.eps.ir.diversity.probability.relevance.UniformItemRelevance;

public class CombinedDiversityMetric extends TopNMeasure {

	private final NoveltyMetric adaptee;

	private final String name;

	public CombinedDiversityMetric(int topN, NoveltyMetric adaptee,
			String name) {
		super(topN);
		this.adaptee = adaptee;
		this.name = name;
	}

	@Override
	public double compute(Integer userId, List<Integer> recommendations,
			Set<Integer> correctItems, Collection<Integer> ignoreItems) {
		return adaptee.compute(
				userId.longValue(),
				copyIntIntoLongList(getTopNRecommendations(recommendations,
						ignoreItems)));
	}

	@Override
	public double compute(List<Integer> recommendations,
			Set<Integer> correctItems, Collection<Integer> ignoreItems) {
		// NOT POSSIBLE
		return Double.NaN;
	}

	@Override
	public String getName() {
		return name;
	}

	public static List<Long> copyIntIntoLongList(List<Integer> c) {
		List<Long> s = new ArrayList<>(c.size());
		for (Integer i : c) {
			s.add(i.longValue());
		}
		return s;
	}

	public static IMeasure getLinearRelevanceExponentialDiscountEPC(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.expDiscount(expDisc)
				.linearRelevance()
				.epcNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-EPC@" + topN);
	}
	
	public static IMeasure getLinearRelevanceExponentialDiscountEIP(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.expDiscount(expDisc)
				.linearRelevance()
				.eipNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-EIP@" + topN);
	}
	
	public static IMeasure getLinearRelevanceExponentialDiscountEFD(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.expDiscount(expDisc)
				.linearRelevance()
				.efdNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-EFD@" + topN);
	}
	
	public static IMeasure getLinearRelevanceExponentialDiscountJaccardEPD(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			IBooleanMatrix attributeTypes,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.jaccardDistance(attributeTypes)
				.expDiscount(expDisc)
				.linearRelevance()
				.epdLinearRelevance()
				.epdNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-Jaccard-EPD@" + topN);
	}
	
	public static IMeasure getLinearRelevanceExponentialDiscountCosineEPD(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			IBooleanMatrix attributeTypes,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.cosineDistance(attributeTypes)
				.expDiscount(expDisc)
				.linearRelevance()
				.epdLinearRelevance()
				.epdNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-Cosine-EPD@" + topN);
	}
	
	public static IMeasure getLinearRelevanceExponentialDiscountJaccardEILD(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			IBooleanMatrix attributeTypes,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.jaccardDistance(attributeTypes)
				.expDiscount(expDisc)
				.linearRelevance()
				.eildNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-Jaccard-EILD@" + topN);
	}
	
	public static IMeasure getLinearRelevanceExponentialDiscountCosineEILD(
			IPosOnlyFeedback train, IPosOnlyFeedback test,
			Collection<Integer> testUsers, Collection<Integer> candidateItems,
			IBooleanMatrix attributeTypes,
			int topN, double expDisc) {
		return new CombinedMetricBuilder()
				.setTrain(train)
				.setTest(test, testUsers, candidateItems)
				.setCut(topN)
				.cosineDistance(attributeTypes)
				.expDiscount(expDisc)
				.linearRelevance()
				.eildNovelty()
				.build("Linear-relevance-exponential-discount-(" + expDisc
						+ ")-Cosine-EILD@" + topN);
	}

	public static class CombinedMetricBuilder {
		private Dataset train;
		private Dataset test;
		private int cut = -1;

		private NoveltyModel nov;
		private DiscountModel disc;
		private DistanceModel dist;
		private ItemRelevanceProbability rel;
		private ItemRelevanceProbability epdRel;

		public CombinedMetricBuilder setTrain(IPosOnlyFeedback train) {
			this.train = new TrainDatasetAdapter(train);
			return this;
		}

		public CombinedMetricBuilder setTest(IPosOnlyFeedback test,
				Collection<Integer> testUsers,
				Collection<Integer> candidateItems) {
			this.test = new TestDatasetAdapter(test, testUsers, candidateItems);
			return this;
		}

		public CombinedMetricBuilder setCut(int cut) {
			if (this.cut > 0) {
				throw new IllegalStateException(
						"You're not allowed to set the cut-off value multiple times.");
			}
			this.cut = cut;
			return this;
		}

		public IMeasure build(String name) {
			Preconditions.checkNotNull(name);
			Preconditions.checkArgument(!name.isEmpty());
			Preconditions.checkNotNull(nov);
			Preconditions.checkNotNull(disc);
			Preconditions.checkNotNull(rel);
			Preconditions.checkArgument(cut > 0);
			NoveltyMetric nm = new NoveltyMetric(nov, disc, rel, cut);
			return new CombinedDiversityMetric(cut, nm, name);
		}

		public CombinedMetricBuilder epcNovelty() {
			Preconditions.checkNotNull(train);
			this.nov = new ComplementNoveltyModel(new GenericItemDiscovery(train));
			return this;
		}

		public CombinedMetricBuilder eipNovelty() {
			Preconditions.checkNotNull(train);
			this.nov = new LogNoveltyModel(new GenericItemDiscovery(train));
			return this;
		}
		
		public CombinedMetricBuilder efdNovelty() {
			Preconditions.checkNotNull(train);
			this.nov = new LogNoveltyModel(new GenericItemFreeDiscovery(train));
			return this;
		}

		public CombinedMetricBuilder epdNovelty() {
			Preconditions.checkNotNull(epdRel);
			Preconditions.checkNotNull(dist);
			Preconditions.checkNotNull(train);
			this.nov = new ExpectedNoveltyModel(dist, epdRel, train);
			return this;
		}

		public CombinedMetricBuilder eildNovelty() {
			Preconditions.checkArgument(cut > 0);
			Preconditions.checkNotNull(dist);
			Preconditions.checkNotNull(disc);
			Preconditions.checkNotNull(rel);
			this.nov = new ListDistanceNovelty(dist, disc, rel, cut);
			return this;
		}

		public CombinedMetricBuilder noDiscount() {
			this.disc = new NoDiscount();
			return this;
		}

		public CombinedMetricBuilder logDiscount() {
			this.disc = new LogDiscount();
			return this;
		}

		public CombinedMetricBuilder zipfDiscount() {
			this.disc = new ZipfDiscount();
			return this;
		}

		public CombinedMetricBuilder expDiscount(double beta) {
			Preconditions.checkArgument(beta > 0.0);
			this.disc = new ExpDiscount(beta);
			return this;
		}

		public CombinedMetricBuilder jaccardDistance(
				IBooleanMatrix attributeTypes) {
			Preconditions.checkNotNull(attributeTypes);
			final IItemSimilarity sim = new JaccardAttributeTypeItemSimilarity(
					attributeTypes);
			this.dist = new SimilarityBasedDistanceModel(sim);
			return this;
		}

		public CombinedMetricBuilder cosineDistance(
				IBooleanMatrix attributeTypes) {
			Preconditions.checkNotNull(attributeTypes);
			final IItemSimilarity sim = new CosineAttributeTypeItemSimilarity(
					attributeTypes);
			this.dist = new SimilarityBasedDistanceModel(sim);
			return this;
		}

		public CombinedMetricBuilder noRelevance() {
			this.rel = new UniformItemRelevance();
			return this;
		}

		public CombinedMetricBuilder exponentialRelvance() {
			Preconditions.checkNotNull(test);
			this.rel = new RatingsRelativeItemExpRelevance(test);
			return this;
		}

		public CombinedMetricBuilder linearRelevance() {
			Preconditions.checkNotNull(test);
			this.rel = new RatingsRelativeItemLinRelevance(test);
			return this;
		}
		public CombinedMetricBuilder epdNoRelevance() {
			this.epdRel = new UniformItemRelevance();
			return this;
		}

		public CombinedMetricBuilder epdExponentialRelvance() {
			Preconditions.checkNotNull(epdRel);
			this.epdRel = new RatingsRelativeItemExpRelevance(train);
			return this;
		}

		public CombinedMetricBuilder epdLinearRelevance() {
			Preconditions.checkNotNull(train);
			this.epdRel = new RatingsRelativeItemLinRelevance(train);
			return this;
		}
	}

	public static class SimilarityBasedDistanceModel extends DistanceModel {
		
		private final IItemSimilarity sim;

		public SimilarityBasedDistanceModel(IItemSimilarity sim) {
			super(sim.getName());
			this.sim = sim;
		}

		@Override
		public double distance(long item1, long item2) {
			return 1.0 - similarity(Ints.checkedCast(item1),
					Ints.checkedCast(item2));
		}

		@Override
		public double maxDistance() {
			return 1.0;
		}

		protected double similarity(int item1, int item2) {
			return sim.getSimilarity(item1, item2);
		}

	}

}
