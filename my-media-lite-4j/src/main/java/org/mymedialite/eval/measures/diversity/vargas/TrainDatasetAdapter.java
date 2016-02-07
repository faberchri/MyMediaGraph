package org.mymedialite.eval.measures.diversity.vargas;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mymedialite.data.IPosOnlyFeedback;

import com.google.common.primitives.Ints;

import es.uam.eps.ir.Dataset;

public class TrainDatasetAdapter implements Dataset {

	protected final IPosOnlyFeedback feedback;
	
	public TrainDatasetAdapter(IPosOnlyFeedback feedback) {
		this.feedback = feedback;
	}

	@Override
	public double getRating(Long user, Long item) {
		if (feedback.userMatrix().get(Ints.checkedCast(user), Ints.checkedCast(item))){
			return 1.0;
		}
		return 0.0;
	}

	@Override
	public double getMaxRating() {
		return 1.0;
	}

	@Override
	public Set<Long> getUsers(Long item) {
		return copyIntIntoLongSet( feedback.itemMatrix().getEntriesByRow(Ints.checkedCast(item)) );
	}

	@Override
	public Set<Long> getItems(Long user) {
		return copyIntIntoLongSet( feedback.userMatrix().getEntriesByRow(Ints.checkedCast(user)) );
	}

	@Override
	public Set<Long> getUsers() {
		return copyIntIntoLongSet(feedback.allUsers());
	}

	@Override
	public Set<Long> getItems() {
		return copyIntIntoLongSet(feedback.allItems());
	}

	@Override
	public int getNumRatings() {
		return feedback.size();
	}
	
	public static Set<Long> copyIntIntoLongSet(Collection<Integer> c){
		Set<Long> s = new HashSet<>(c.size());
		for (Integer i : c) {
			s.add(i.longValue());
		}
		return s;
	}

}