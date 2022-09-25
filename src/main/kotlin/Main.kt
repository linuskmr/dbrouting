import java.time.Duration
import java.time.LocalTime

fun main(args: Array<String>) {
	val braunschweig = Station("Braunschweig")
	val berlin = Station("Berlin")
	val hamburg = Station("Hamburg")
	val hannover = Station("Hannover")
	val frankfurt = Station("Frankfurt")

	val ice573 = Train(
		kind = "ICE", id = "573", rides = listOf(
			Ride(
				from = Departure(frankfurt, LocalTime.of(8, 5)),
				to = Departure(hannover, LocalTime.of(10, 42)),
				price = 42
			),
			Ride(
				from = Departure(hannover, LocalTime.of(10, 45)),
				to = Departure(hamburg, LocalTime.of(12, 0)),
				price = 21
			),
			Ride(
				from = Departure(hamburg, LocalTime.of(12, 5)),
				to = Departure(berlin, LocalTime.of(13, 54)),
				price = 35
			)
		)
	)

	val re70 = Train(
		kind = "RE", id = "60", rides = listOf(
			Ride(
				from = Departure(braunschweig, LocalTime.of(9, 52)),
				to = Departure(hannover, LocalTime.of(10, 37)),
				price = 10
			),
		)
	)
	val graph = Graph(
		stations = setOf(braunschweig, berlin, hamburg, hannover, frankfurt),
	)



	val travel = graph.dijkstra(
		start = Dijkstra.Start(
			station = braunschweig,
			time = LocalTime.of(9, 42)
		),
		destination = hamburg,
		weightCalculator = { rideInfo ->
			var weight = rideInfo.nextRide.price.toDouble()
			weight += Duration.between(
				rideInfo.previousRide?.to?.time,
				rideInfo.nextRide.from.time
			).toMinutes().toDouble()
			weight
		}
	)
	if (travel == null) {
		println("No path found :(")
	}
	println(travel)
}

fun Duration.toHhMm(): String = "${toHoursPart()}h ${toMinutesPart()}min"