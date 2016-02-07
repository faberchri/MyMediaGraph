package ch.uzh.ifi.ddis.mymedialite.graph.util;

/**
 * UpdateableTreeSet elements must implement this interface in order to provide
 * a structured way of updating themselves at the right moment during an update
 * operation, i.e. after temporary removal from the collection and before being
 * added back into the collection.
 * 
 * @author Alexander Kriegisch, <a
 *         href="http://scrum-master.de">Scrum-Master.de</a>
 */
public interface Updateable<V> {
	/**
	 * Update method for elements which do not need to be given new values, but
	 * have a way of updating themselves in another way. If you do not need this
	 * method, just specify a version calling {@link #update(Object)} with a
	 * null value.
	 */
	void update();

	/**
	 * Update method for elements which need one or more new values. If more
	 * than one value is necessary, {@code newValue} could e.g. be a Map or a
	 * List. If you do not need this method, just specify a version dropping the
	 * value and calling {@link #update()} instead.
	 */
	void update(V newValue);
}
