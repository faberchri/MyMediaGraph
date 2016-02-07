package ch.uzh.ifi.ddis.mymedialite.graph.pathcount


class AbsoluteNormalizedItemBasedPureCF3Path
extends ItemBasedPureCF3Path {

	def leverage = 0.5

	def getSimilarShows(def show) {
		def similarShows = super.getSimilarShows(show)
		similarShows.each{
			it.value = it.value * getItemWeight(it.key)
		}
		return similarShows
	}

	def getItemWeight(def item){
		def pop = item.in(userItemFeedbackEdgeLabel).count()
		return 1.0d / Math.pow(pop, leverage)
	}

	@Override
	public String toString() {
		return super.toString() + " leverage=" + leverage
	}
}
