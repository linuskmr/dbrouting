class Train(
	val kind: String,
	val id: String,
	val rides: List<Ride>,
) {
	val name: String
		get() = "$kind $id"

	init {
		for (ride in rides) {
			// Add train to the ride
			ride.train = this

			// Add rides to the stations
			ride.from.station.addRide(ride)
			ride.to.station.addRide(ride)
		}
	}

	override fun toString(): String {
		return name
	}
}