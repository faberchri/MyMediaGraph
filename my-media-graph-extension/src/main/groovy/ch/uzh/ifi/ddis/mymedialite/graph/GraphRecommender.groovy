package ch.uzh.ifi.ddis.mymedialite.graph

import org.mymedialite.IItemAttributeAwareRecommender
import org.mymedialite.datatype.SparseBooleanMatrix
import org.mymedialite.itemrec.IIncrementalItemRecommender
import org.mymedialite.itemrec.ItemRecommender

import com.tinkerpop.blueprints.TransactionalGraph
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph
import com.tinkerpop.gremlin.groovy.Gremlin

abstract class GraphRecommender extends ItemRecommender implements
IItemAttributeAwareRecommender, IIncrementalItemRecommender {
	
	static {
		Gremlin.load()
	  }

	protected static final String vertexType = 'type'

	protected static final String userVertexType = 'User'
	protected static final String itemVertexType = 'Item'
	protected static final String attributeVertexType = 'Attribute'

	protected static final String myMediaLiteUserIdVertexProperty = 'mymedialite_user_id'
	protected static final String myMediaLiteItemIdVertexProperty = 'mymedialite_item_id'
	protected static final String myMediaLiteAttributeIdVertexProperty = 'mymedialite_attribute_id'

	protected static final String userItemFeedbackEdgeLabel = 'watche'
	protected static final String itemAttributeEdgeLabel = 'hasAttribute'

	def graph
	def itemAttributes

	public GraphRecommender() {

	}

	@Override
	public int numItemAttributes() {
		return itemAttributes.numberOfColumns()
	}

	@Override
	public SparseBooleanMatrix getItemAttributes() {
		if (itemAttributes == null) {
			// read item attributes from deserialized graph back into SparseBooleanMatrix
			this.itemAttributes = loadItemAttributesFromGraph()
		}
		return itemAttributes
	}

	private SparseBooleanMatrix loadItemAttributesFromGraph(){
		println "-- Start loading item attributes from graph --"
		SparseBooleanMatrix res = new SparseBooleanMatrix()
		for (itemId in 0..maxItemID){
			def itemVertex = getVertexByItemId(itemId)
			if (itemVertex != null) {
				def attributeIds = itemVertex.out(itemAttributeEdgeLabel).myMediaLiteAttributeIdVertexProperty.toList()
				attributeIds.each{ attributeId ->
					res.set(itemId, attributeId, true)
				}
			}
		}
		println "-- Loading item attributes from graph completed --"
		return res
	}

	@Override
	public void setItemAttributes(SparseBooleanMatrix s) {
		this.itemAttributes = s
	}

	@Override
	public abstract double predict(int userId, int itemId);

	@Override
	public void train() {
		println '-- Start train() of GraphRecommender --'
		println '-- Start graph creation --'
		this.graph = new TinkerGraph();
		println "-- New TinkerGraph created: $graph --"

		graph.createKeyIndex(myMediaLiteAttributeIdVertexProperty,Vertex.class)
		graph.createKeyIndex(myMediaLiteUserIdVertexProperty,Vertex.class)
		graph.createKeyIndex(myMediaLiteItemIdVertexProperty,Vertex.class)
		println "-- Graph key indices for MyMediaLite ids created --"

		def batchGraph = BatchGraph.wrap(graph);
		println "-- TinkerGrap wrapped into BatchGraph: $batchGraph --"

		def itemIdVertexMap = createItemVertices(batchGraph)
		println "-- Item vertices created (${itemIdVertexMap.size()}) --"
		def userIdVertexMap = createUserVertices(batchGraph)
		println "-- User vertices created (${userIdVertexMap.size()}) --"
		def attributeIdVertexMap = createAttributeVertices(batchGraph)
		println "-- Attribute vertices created (${attributeIdVertexMap.size()}) --"

		def numOfAddedEdges = addFeedbackEdges(batchGraph, userIdVertexMap, itemIdVertexMap)
		println "-- User item feedback edges added ($numOfAddedEdges) --"
		numOfAddedEdges = addItemAttributesEdges(batchGraph, attributeIdVertexMap, itemIdVertexMap)
		println "-- Item to attribute edges added ($numOfAddedEdges) --"

		batchGraph.stopTransaction(TransactionalGraph.Conclusion.SUCCESS)
		println '-- Graph created and all transactions committed --';
		println "-- Datataset successfully loaded into TinkerGraph: $graph --"
		println '-- End train() of GraphRecommender --'
	}

	private Map createUserVertices(def batchGraph){
		def idToVertices = new HashMap()
		for (i in 0..maxUserID) {
			def v = batchGraph.addVertex(userVertexType + i.toString(), [(vertexType):userVertexType, (myMediaLiteUserIdVertexProperty):i])
			idToVertices.put(i, v)
		}
		return idToVertices
	}

	private Map createItemVertices(def batchGraph){
		def idToVertices = new HashMap()
		for (i in 0..maxItemID) {
			def v = batchGraph.addVertex(itemVertexType  + i.toString(), [(vertexType):itemVertexType, (myMediaLiteItemIdVertexProperty):i])
			idToVertices.put(i, v)
		}
		return idToVertices
	}

	private int addFeedbackEdges(def batchGraph, def userIdToVertices, def itemIdToVertices){
		def booleanMatrix = feedback.userMatrix()
		def c = 0
		for (userId in 0..maxUserID) {
			for (itemId in 0..maxItemID) {
				if(booleanMatrix.get(userId, itemId)){
					def userVertex = userIdToVertices.get(userId);
					def itemVertex = itemIdToVertices.get(itemId)
					batchGraph.addEdge(null, userVertex, itemVertex, userItemFeedbackEdgeLabel)
					c++
				}
			}
		}
		return c
	}

	private Map createAttributeVertices(def batchGraph){
		def idToVertices = new HashMap()
		for (i in 0..<getItemAttributes().numberOfColumns()) {
			def v = batchGraph.addVertex(attributeVertexType + i.toString(),[(vertexType):attributeVertexType, (myMediaLiteAttributeIdVertexProperty):i])
			idToVertices.put(i, v)
		}
		return idToVertices
	}

	private int addItemAttributesEdges(def batchGraph, def attributeIdToVertices, def itemIdToVertices){
		def c = 0
		for (itemId in 0..<getItemAttributes().numberOfRows()) {
			for (attributeId in 0..<getItemAttributes().numberOfColumns()) {
				if(getItemAttributes().get(itemId, attributeId)){
					def attributeVertex = attributeIdToVertices.get(attributeId)
					def itemVertex = itemIdToVertices.get(itemId)
					if (itemVertex != null){
						batchGraph.addEdge(null, itemVertex, attributeVertex, itemAttributeEdgeLabel)
					}
					c++
				}
			}
		}
		return c
	}

	private Vertex getVertexByMyMediaLiteId(String myMediaLiteIdType, int id) {
		return graph.V(myMediaLiteIdType,id).next()
	}

	protected Vertex getVertexByUserId(int id) {
		return getVertexByMyMediaLiteId(myMediaLiteUserIdVertexProperty, id)
	}

	protected Vertex getVertexByItemId(int id) {
		return getVertexByMyMediaLiteId(myMediaLiteItemIdVertexProperty, id)
	}

	protected Vertex getVertexByAttributeId(int id) {
		return getVertexByMyMediaLiteId(myMediaLiteAttributeIdVertexProperty, id)
	}

	@Override
	public void addFeedback(int user_id, int item_id) {
		def userVertex = getVertexByUserId(user_id)
		if (userVertex == null) {
			userVertex = graph.addVertex(userVertexType + Integer.toString(user_id))
			userVertex.setProperty(vertexType, userVertexType)
			userVertex.setProperty(myMediaLiteUserIdVertexProperty, user_id)
			maxUserID++
		}
		def itemVertex = getVertexByItemId(item_id)
		if (itemVertex == null) {
			itemVertex = graph.addVertex(itemVertexType + Integer.toString(item_id))
			itemVertex.setProperty(vertexType, itemVertexType)
			itemVertex.setProperty(myMediaLiteItemIdVertexProperty, item_id)
			maxItemID++
		}
		graph.addEdge(null, userVertex, itemVertex, userItemFeedbackEdgeLabel);
		// no commit needed since TinkerGraoh does not implement TransactionalGraph
	}

	@Override
	public void addFeedback(int user_id, List<Integer> item_ids) {
		item_ids.each{ item_id ->
			addFeedback(user_id, item_id)
		}
	}

	@Override
	public void removeFeedback(int user_id, int item_id) {
		// userVertex / itemVertex are not removed from the graph even if their edge count is 0
		def userVertex = getVertexByUserId(user_id)
		if (userVertex == null) {
			throw new IllegalStateException("Attempt to remove feedback edge from unknown user vertex. user_id: $user_id")
		}
		def itemVertex = getVertexByItemId(item_id)
		if (itemVertex == null) {
			throw new IllegalStateException("Attempt to remove feedback edge from unknown item vertex. item_id: $item_id")
		}

		if (userVertex.out(userItemFeedbackEdgeLabel).retain([itemVertex]).hasNext()){
			// we remove only one edge between userVertex and itemVertex,
			// hence if we had multiple edges between the two vertices they remain connected
			def edgeToRemove = userVertex.outE.as('x').inV.retain([itemVertex]).back('x')
			graph.removeEdge(edgeToRemove)
			// no commit needed since TinkerGraoh does not implement TransactionalGraph
		} else {
			throw new IllegalStateException("No feedback edge present between user vertex ${userVertex.map} and item vertex ${itemVertex.map}.")
		}
	}

	@Override
	public void removeUser(int user_id) {
		def userVertex = getVertexByUserId(user_id)
		if (userVertex == null) {
			throw new IllegalStateException("Attempt to remove unknown user vertex. user_id: $user_id")
		}
		// removing the edges from the userVertex is handled by the TinkerGraph removeVertex method
		graph.removeVertex(userVertex)
		// no commit needed since TinkerGraoh does not implement TransactionalGraph
		// maxUserID remains constant
	}

	@Override
	public void removeItem(int item_id) {
		def itemVertex = getVertexByItemId(item_id)
		if (itemVertex == null) {
			throw new IllegalStateException("Attempt to remove unknown item vertex. user_id: $item_id")
		}
		// removing the edges from the itemVertex is handled by the TinkerGraph removeVertex method
		graph.removeVertex(itemVertex)
		// no commit needed since TinkerGraoh does not implement TransactionalGraph
		// maxItemId remains constant
	}

	@Override
	public void loadModel(String filename) throws IOException {
		def input = new ObjectInputStream(new FileInputStream(filename));
		this.graph = input.readObject();
		input.close();
	}

	@Override
	public void loadModel(BufferedReader reader) throws IOException {
		// FIXME stupid implementation
		def tmpFile = File.createTempFile("GraphRecommenderOutput-" + UUID.randomUUID().toString(), "graphml")
		tmpFile.deleteOnExit();
		def writer = new PrintWriter(tmpFile)
		def line;
		while ((line = reader.readLine()) != null) {
			writer.write(line);
			writer.flush();
		}
		writer.close();

		def graph = new TinkerGraph();
		GraphMLReader.inputGraph(graph, new FileInputStream(tmpFile));
		tmpFile.delete();
		this.graph = graph
	}

	@Override
	public void saveModel(String filename) throws IOException {
		def out = new ObjectOutputStream(new FileOutputStream(filename));
		out.writeObject(graph);
		out.close();
	}

	@Override
	public void saveModel(PrintWriter writer) throws IOException {
		// FIXME stupid implementation
		def tmpFile = File.createTempFile("GraphRecommenderOutput-" + UUID.randomUUID().toString(), "graphml")
		tmpFile.deleteOnExit();
		def os = new FileOutputStream(tmpFile);
		GraphMLWriter.outputGraph(graph, os);
		os.close();

		def br = new BufferedReader(new FileReader(tmpFile));
		def line;
		while ((line = br.readLine()) != null) {
			writer.write(line);
			writer.flush();
		}
		br.close();
		writer.close();
		tmpFile.delete();
	}
}
