import java.time.Duration


data class Ride(
	val from: Departure,
	val to: Departure,

	/**
	 * In Euro.
	 */
	val price: Int,
) {

	lateinit var train: Train

	/**
	 * Duration of the drive.
	 */
	val duration: Duration
		get() = Duration.between(from.time, to.time)

	override fun toString(): String {
		return "$train: $from → $to | ${duration.toHhMm()} | $price €"
	}
}