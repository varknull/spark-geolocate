package kdtree

object Utils extends Serializable {
  val R = 6378137
  val PI_360 = math.Pi / 360;

	/**
    * Computes the euclidean distance between two 2D points
    * */
	def euclideanDistance(x1:Double, x2:Double, y1:Double, y2:Double):Double = {
		val dx = x1-x2
		val dy = y1-y2
		return math.sqrt((dx*dx)+(dy*dy))
	}

  /**
    * The distance between two geo coordinates, in meters.
    * Uses fast (but approximated) haversine formula
    */
  def haversineDistance(x1:Double, x2:Double, y1:Double, y2:Double): Double = {
    val cLat = Math.cos((x1 + x2) * PI_360);
    val dLat = (x2 - x1) * PI_360;
    val dLon = (y2 - y1) * PI_360;

    val f = dLat * dLat + cLat * cLat * dLon * dLon;
    val c = 2 * Math.atan2(Math.sqrt(f), Math.sqrt(1 - f));
    R * c
  }
}
