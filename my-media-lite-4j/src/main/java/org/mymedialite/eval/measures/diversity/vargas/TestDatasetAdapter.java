package org.mymedialite.eval.measures.diversity.vargas;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mymedialite.data.IPosOnlyFeedback;

import com.google.common.primitives.Ints;


public class TestDatasetAdapter extends TrainDatasetAdapter {
	
	private final Set<Long> testUsers;
	
	private final Set<Long> candidateItems;
	
	public TestDatasetAdapter(IPosOnlyFeedback feedback, Collection<Integer> testUsers,
			Collection<Integer> candidateItems) {
		super(feedback);
		this.testUsers = copyIntIntoLongSet(testUsers);
		this.candidateItems = copyIntIntoLongSet(candidateItems);
	}

	@Override
	public Set<Long> getUsers(Long item) {
		return copyIntIntoLongSet( feedback.itemMatrix().getEntriesByRow(Ints.checkedCast(item)), testUsers );
	}

	@Override
	public Set<Long> getItems(Long user) {
		return copyIntIntoLongSet( feedback.userMatrix().getEntriesByRow(Ints.checkedCast(user)), candidateItems );
	}

	@Override
	public Set<Long> getUsers() {
		return testUsers;
	}

	@Override
	public Set<Long> getItems() {
		return candidateItems;
	}
	
	public static Set<Long> copyIntIntoLongSet(Collection<Integer> c, Set<Long> restriction){
		Set<Long> s = new HashSet<>(c.size());
		for (Integer i : c) {
			long l = i.longValue();
			if (restriction.contains(l)){
				s.add(l);				
			}
		}
		return s;
	}

}
