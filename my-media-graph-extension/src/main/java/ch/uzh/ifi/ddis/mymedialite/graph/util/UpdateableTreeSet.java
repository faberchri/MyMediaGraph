package ch.uzh.ifi.ddis.mymedialite.graph.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class is just a thin layer around its parent class, adding an element
 * updating and re-sorting feature which works as follows:
 * <p>
 * As you might know you should never add or remove elements of Java collections
 * while iterating over them in a loop. There is one exception: If you use an
 * {@link java.util.Iterator Iterator} you may safely use its
 * {@link java.util.Iterator#remove() remove} method. Many people do not know
 * that or are even unaware of that method's existence because they iterate
 * using {@code for} loops.
 * <p>
 * Another problem specifically with sorted collections is that even if they are
 * {@link java.lang.Comparable Comparable} or use an explicit
 * {@link java.util.Comparator Comparator}, their elements will be sorted at
 * <b>insertion time</b> only and the sort order not updated even if their
 * sorting keys change. If you want to achieve a refresh, you have to
 * <ol>
 * <li>remove them from the collection,
 * <li>update their values and then
 * <li>add them back again.
 * </ol>
 * This class does just that, but in a structured fashion as a bulk operation
 * <b>after</b> your loop has finished or as a single-element operation if you
 * want to do it outside a loop.
 * <p>
 * Usage example:
 * 
 * <pre>
 * import de.scrum_master.util.UpdateableTreeSet;
 * import de.scrum_master.util.UpdateableTreeSet.Updateable;
 * 
 * class MyType implements Updateable {
 *     void update(Object newValue) {
 *         // Change the receiver's value
 *     }
 * }
 * 
 * SortedSet<MyType> mySortedSet = new UpdateableTreeSet<MyType>();
 * 
 * // Add elements to mySortedSet...
 * 
 * for (MyType element : mySortedSet) {
 *     if (removeCondition)
 *         markForRemoval(element);
 *     if (updateCondition)
 *         markForUpdate(element, newValue);
 * }
 * 
 * mySortedSet.updateMarked();
 * </pre>
 * 
 * Special thanks go to user <a
 * href="http://stackoverflow.com/users/15472/tucuxi">tucuxi at
 * stackoverflow.com</a> because his <a
 * href="http://stackoverflow.com/a/2581450/1082681">answer</a> in one of the
 * discussion threads inspired me to implement this class based on his idea. My
 * version is just a bit more sophisticated because it supports deferred updates
 * and is thus useful within {@code for} loops.
 * <p>
 * 
 * @author Alexander Kriegisch, <a
 *         href="http://scrum-master.de">Scrum-Master.de</a>
 */
public class UpdateableTreeSet<E extends Updateable, V> extends TreeSet<E> {

	private static final long serialVersionUID = 1170156554123865966L;

	// Use identity maps so as to avoid compareTo and possibly to have double
	// candidates in the lists
	// if 'put' is called multiple times for the same element in different
	// states (thus with different
	// keys at the time of insertion). BTW: Too bad there is no identity set for
	// 'toBeRemoved'. :-(
	private final Map<E, V> toBeUpdated = new IdentityHashMap<>();
	private final Map<E, V> toBeRemoved = new IdentityHashMap<>();

	public UpdateableTreeSet() {
		super();
	}

	public UpdateableTreeSet(Collection<? extends E> c) {
		super(c);
	}

	public UpdateableTreeSet(Comparator<? super E> comparator) {
		super(comparator);
	}

	public UpdateableTreeSet(SortedSet<E> s) {
		super(s);
	}

	/**
	 * Mark an element for subsequent update by {@link #updateMarked}.
	 * <p>
	 * <b>Attention:</b> Beware of manually modifying a marked element before
	 * its scheduled update. It might not be found anymore (and thus not
	 * removed) because of its changed key, which later could lead to strange
	 * double entries in the collection.
	 */
	public void markForUpdate(E element, V newValue) {
		toBeUpdated.put(element, newValue);
	}

	/**
	 * Convenience method passing a null value to
	 * {@link #markForUpdate(Updateable, Object)}
	 */
	public void markForUpdate(E element) {
		toBeUpdated.put(element, null);
	}

	/**
	 * Mark an element for subsequent removal by {@link #updateMarked}.
	 * <p>
	 * <b>Attention:</b> Beware of manually modifying a marked element before
	 * its scheduled removal. It might not be found anymore (and thus not
	 * removed) because of its changed key, which later could lead to strange
	 * double entries in the collection.
	 */
	public void markForRemoval(E element) {
		toBeRemoved.put(element, null);
	}

	/**
	 * Performs
	 * <ol>
	 * <li>a bulk removal on all elements previously marked for removal,
	 * <li>a bulk update on all elements previously marked for update so as to
	 * trigger their re-sorting within the collection.
	 * </ol>
	 * The marks will be removed from the processed elements after the bulk
	 * operation, so you can start to mark other elements afterwards.
	 * <p>
	 * Please note that if any remove or update action fails, this will be
	 * silently ignored as long as there are no exceptions.
	 * <p>
	 * <b>Attention:</b> Do not call this method while looping over the
	 * collection and beware of manually modifying any marked elements before
	 * their scheduled update/removal. They might not be found anymore (and thus
	 * not removed) because of their changed keys, which later could lead to
	 * strange double entries in the collection.
	 */
	public synchronized void updateMarked() {
		removeAll(toBeRemoved.keySet());
		toBeRemoved.clear();
		// Make sure to remove *all* update candidates before updating them.
		// Otherwise re-insertion
		// might be wrong based other candidates still being in their (wrong)
		// old places.
		removeAll(toBeUpdated.keySet());
		// Kick off update hook
		for (E element : toBeUpdated.keySet())
			element.update(toBeUpdated.get(element));
		addAll(toBeUpdated.keySet());
		toBeUpdated.clear();
	}

	/**
	 * Updates the total sort order of the set by removing and re-adding
	 * <b>all</b> elements.
	 * <p>
	 * You may use this method in situations when properties of elements within
	 * the set have been mofified, which usually should be avoided. This method
	 * refreshes and in a way "repairs" the set, because if you modify elements
	 * within the set, their fixed sort order might contradict their actual
	 * state, which might disturb iterators and operations like
	 * {@link #contains} or {@link #remove}.
	 * <p>
	 * <b>Attention:</b> All previously recorded update/removal marks will be
	 * reset before the update, so do not expect any elements to be removed by
	 * this operation.
	 */
	public synchronized void updateAll() {
		toBeRemoved.clear();
		toBeUpdated.clear();
		for (E element : this)
			markForUpdate(element);
		updateMarked();
	}

	/**
	 * Performs an immediate update on a single element so as to trigger its
	 * re-ordering within the collection. Calling this method has no effect on
	 * the list of elements marked for removal or update.
	 * <ol>
	 * <b>Attention:</b> Do not call this method while looping over the
	 * collection.
	 * 
	 * @param element
	 *            the element to be updated
	 * @param newValue
	 *            element's new value (if any)
	 * 
	 * @return true if element was found in collection and updated (i.e. removed
	 *         and added back) successfully
	 */
	public synchronized boolean update(E element, V newValue) {
		if (remove(element)) {
			element.update(newValue);
			return add(element);
		}
		return false;
	}

	/**
	 * Convenience method passing a null value to
	 * {@link #update(Updateable, Object)}
	 */
	public boolean update(E element) {
		if (remove(element)) {
			element.update();
			return add(element);
		}
		return false;
	}
}
