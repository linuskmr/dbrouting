import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.Callable

class Graph(val stations: Set<Station>) {
	fun dijkstra(
		start: Dijkstra.Start,
		destination: Station,
		weightCalculator: (Dijkstra.RideInfo) -> Double
	): Travel? {
		return Dijkstra(
			start = start,
			destination = destination,
			weightCalculator = weightCalculator,
			graph = this
		).call()
	}
}

class Dijkstra(
	val start: Dijkstra.Start,
	val destination: Station,
	val weightCalculator: (RideInfo) -> Double,
	graph: Graph,
): Callable<Travel?> {
	class Start(val station: Station, val time: LocalTime)
	class RideInfo(val previousRide: Ride?, val nextRide: Ride) {
		val transferTime = previousRide?.let { Duration.between(it.to.time, nextRide.from.time) }
	}


	private val stations = graph.stations

	// Mark all stations as unvisited
	private val unvisited = stations.toMutableSet()

	// Stores for each station with which ride we can reach it
	private val rides = mutableMapOf<Station, Ride>()
	// Stores the shortest currently known distance to each station
	private val distances = mutableMapOf<Station, Double>().also {
		stations.forEach { station -> it[station] = Double.POSITIVE_INFINITY }
		it[start.station] = 0.0
	}

	override fun call(): Travel? {
		while (unvisited.isNotEmpty()) {
			// Find an unvisited station with the shortest distance
			val current: Station = unvisited.minBy { distances[it]!! }
			if (current == destination) {
				// Shortest path for destination was already calculated. This round would only update the distance for
				// the destination's neighbors, but doesn't change the shortest path to the destination.
				break
			}
			unvisited.remove(current)

			val possibleRides: List<Ride> = possibleRides(current)

			// Update the distance to the neighbors through the current station
			for (ride in possibleRides) {
				useRide(ride, current)
			}
		}
		return getTravel()
	}

	private fun getTravel(): Travel? {
		if (distances[destination] == Double.POSITIVE_INFINITY) {
			// No path to destination was found
			return null
		}

		// Reconstruct the path from target to start, because rides is in reverse order
		return buildTravelPath()
	}

	/**
	 * Returns all rides that start at the given station and are possible to take at the given time.
	 */
	private fun buildTravelPath(): Travel {
		val travelPath = buildList {
			var ride = rides[destination]!!
			while (true) {
				add(ride)
				if (ride.from.station == start.station) {
					// We reached the start. Looking up the ride to start would fail, so stop here
					break
				}
				ride = rides[ride.from.station]!!
			}
		}.reversed()
		return Travel(travelPath)
	}

	/**
	 * Uses the given ride to update the distance to the ride's target station.
	 */
	private fun useRide(ride: Ride, current: Station) {
		val neighbour = ride.to.station
		val oldWeight = distances[neighbour]!!
		val newWeight = distances[current]!! + ride.price // TODO: weightCalculator(ride)
		if (newWeight >= oldWeight) {
			// This ride is not be better than the already known
			return
		}
		// Update distance and ride to the neighbour
		distances[neighbour] = newWeight
		rides[neighbour] = ride
	}

	/**
	 * Find rides that start not earlier than arrivalTimeAtCurrent and start.time
	 */
	private fun possibleRides(current: Station): List<Ride> {
		val possibleRides: List<Ride> = current.departures
			.filter { ride ->
				val arrivalTimeAtCurrent = rides[current]?.to?.time
				if (arrivalTimeAtCurrent != null) {
					assert(arrivalTimeAtCurrent >= start.time)
				}
				val earliestDeparture = arrivalTimeAtCurrent?.plusMinutes(/*transfer time*/5) ?: start.time
				val isAfter = ride.from.time.isAfter(earliestDeparture)
				isAfter
			}
		return possibleRides
	}
}

class Travel(val rides: List<Ride>) {
	val price = rides.sumOf { it.price }
	val duration: Duration = Duration.between(rides.first().from.time, rides.last().to.time)
	val arrivalTime = rides.last().to.time
	val from = rides.first().from.station
	val to = rides.last().to.station

	override fun toString(): String {
		return buildString {
			appendLine("$from @ ${rides.first().from.time} → $to @ ${rides.last().to.time}")
			appendLine("$price € | ${duration.toHhMm()}")
			appendLine("---")
			rides.forEach { ride ->
				appendLine(ride)
			}
		}
	}
}