import java.util.PriorityQueue

class Station(val name: String) {
	/**
	 * Departures sorted by departure time.
	 */
	val departures = PriorityQueue<Ride> { a, b ->
		a.from.time.compareTo(b.from.time)
	}

	/**
	 * Arrivals sorted by arrival time.
	 */
	val arrivals = PriorityQueue<Ride> { a, b ->
		a.to.time.compareTo(b.to.time)
	}

	fun addRide(ride: Ride) {
		if (ride.from.station == this) {
			departures.add(ride)
		} else if (ride.to.station == this) {
			arrivals.add(ride)
		}
	}

	override fun toString() : String {
		return name
	}
}