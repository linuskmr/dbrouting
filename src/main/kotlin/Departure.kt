import java.time.LocalTime

data class Departure(val station: Station, val time: LocalTime) {
	override fun toString(): String {
		return "$station @ $time"
	}
}