package ind.gopinnath.twitter.trenzz.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {

	public static LocalDateTime buildTweetHour() {
		return LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
	}
	
	public static Long getEpochValue(LocalDateTime tweetHour)	{
		return tweetHour.toEpochSecond(OffsetDateTime.now().getOffset());
	}
	
	public static LocalDateTime getBeginingOfWeekHour() {
		LocalDateTime previousSunday = LocalDateTime
				.now()
				.truncatedTo(ChronoUnit.DAYS)
				.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
				.minus(1,ChronoUnit.WEEKS);
		return previousSunday;
	}
}
