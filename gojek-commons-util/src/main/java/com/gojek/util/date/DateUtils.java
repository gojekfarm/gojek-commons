/**
 *
 */
package com.gojek.util.date;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

/**
 * @author ganeshs
 *
 */
public class DateUtils {
	
	/**
	 * @author ganeshs
	 *
	 */
	public static class IntervalStartComparator implements Comparator<Interval> {
	    @Override
	    public int compare(Interval x, Interval y) {
	        return x.getStart().compareTo(y.getStart());
	    }
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	public static class IntervalEndComparator implements Comparator<Interval> {
	    @Override
	    public int compare(Interval x, Interval y) {
	        return x.getEnd().compareTo(y.getEnd());
	    }
	}
	
	/**
	 * Returns the gaps within the interval list
	 *
	 * @param intervals
	 * @return
	 */
	public static List<Interval> findGaps(List<Interval> intervals, Interval searchInterval) {
		List<Interval> gaps = new ArrayList<Interval>();
		if (! intervals.isEmpty()) {
			intervals = Ordering.from(new IntervalStartComparator()).sortedCopy(intervals);
			Interval first = intervals.get(0);
			if (first.getStart().isAfter(searchInterval.getStart())) {
				gaps.add(new Interval(searchInterval.getStart(), first.getStart()));
			}
			
			intervals = Ordering.from(new IntervalEndComparator()).sortedCopy(intervals);
			Interval last = intervals.get(intervals.size() - 1);
			if (last.getEnd().isBefore(searchInterval.getEnd())) {
				gaps.add(new Interval(last.getEnd(), searchInterval.getEnd()));
			}
			Interval current = intervals.get(0);
			for (int i = 1; i < intervals.size(); i++) {
				Interval next = intervals.get(i);
				Interval gap = current.gap(next);
				if (gap != null)
					gaps.add(gap);
				current = next;
			}
		} else {
			gaps.add(searchInterval);
		}
		return gaps;
	}
	
	/**
	 * Removes all the intervals that overlap with the given interval
	 *
	 * @param intervals
	 * @param searchInterval
	 * @return
	 */
	public static List<Interval> getOverlappingIntervals(List<Interval> intervals, Interval searchInterval) {
		List<Interval> result = new ArrayList<Interval>();
		for (Interval interval : intervals) {
			if (interval.overlaps(searchInterval)) {
				result.add(interval.overlap(searchInterval));
			}
		}
		return result;
	}
	
	/**
	 * Returns all the intervals that are contained within the search intervals
	 *
	 * @param intervals
	 * @param searchIntervals
	 * @return
	 */
	public static List<Interval> getContainedIntervals(List<Interval> intervals, List<Interval> searchIntervals) {
		List<Interval> result = new ArrayList<Interval>();
		for (Interval interval : intervals) {
			for (Interval searchInterval : searchIntervals) {
				if (searchInterval.contains(interval)) {
					result.add(interval);
					continue;
				} else if (interval.contains(searchInterval)) {
					result.add(searchInterval);
					continue;
				} else if (interval.overlaps(searchInterval)) {
					if (interval.getStart().isAfter(searchInterval.getStart())) {
						result.add(new Interval(interval.getStart(), searchInterval.getEnd()));
						continue;
					} else if (interval.getStart().isBefore(searchInterval.getStart())) {
						result.add(new Interval(searchInterval.getStart(), interval.getEnd()));
						continue;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static final List<LocalDate> getDaysBetween(DateTime startTime, DateTime endTime) {
		List<LocalDate> dates = new ArrayList<LocalDate>();
		int days = Days.daysBetween(startTime.toLocalDate(), endTime.toLocalDate()).getDays()+1;
		for (int i=0; i < days; i++) {
		    LocalDate d = startTime.toLocalDate().withFieldAdded(DurationFieldType.days(), i);
		    dates.add(d);
		}
		return dates;
	}
	
	/**
	 * Rounds the given time to nearest lowest duration
	 *
	 * @param epoch
	 * @param duration
	 * @return
	 */
	public static final DateTime roundFloorTime(Long epoch, int duration) {
		return roundFloorTime(new DateTime(epoch), duration);
	}
	
	/**
	 * Rounds the given time to nearest lowest duration
	 *
	 * @param duration
	 * @return
	 */
	public static final DateTime roundFloorTime(DateTime dateTime, int duration) {
		if (duration < 0 || duration > 60) {
			duration = 60;
		}
		int minute = dateTime.minuteOfHour().get();
		return dateTime.withMinuteOfHour(minute - (minute % duration)).withSecondOfMinute(0).withMillisOfSecond(0);
	}
	
	/**
	 * Rounds the given time to nearest highest duration
	 *
	 * @param epoch
	 * @param duration
	 * @return
	 */
	public static final DateTime roundCeilTime(Long epoch, int duration) {
		return roundCeilTime(new DateTime(epoch), duration);
	}
	
	/**
	 * Rounds the given time to nearest highest duration
	 *
	 * @param dateTime
	 * @param duration
	 * @return
	 */
	public static final DateTime roundCeilTime(DateTime dateTime, int duration) {
		if (duration <= 0) {
			duration = 1;
		}
		int minute = dateTime.minuteOfHour().get();
		int factor = minute / duration;
		if (factor > 0) {
			minute = duration * (factor + 1) - 1;
		} else {
			minute = duration - 1;
		}
		if (minute >= 60) {
			int hours = minute / 60;
			dateTime = dateTime.plusHours(hours);
			minute = minute % 60;
		}
		return dateTime.withMinuteOfHour(minute).withSecondOfMinute(59).withMillisOfSecond(999);
	}
	
	/**
	 * @param startTime
	 * @param endTime
	 * @param duration
	 */
	public static final List<Interval> getIntervals(DateTime startTime, DateTime endTime, int duration) {
		List<Interval> intervals = Lists.newArrayList();
		startTime = roundFloorTime(startTime, duration);
		endTime = roundCeilTime(endTime, duration);
		while (startTime.isBefore(endTime)) {
			DateTime end = startTime.plus(duration * 60000);
			intervals.add(new Interval(startTime, end));
			startTime = end;
		}
		return intervals;
	}
	
	public static void main(String[] args) {
	    DateTime time = DateTime.now().withMinuteOfHour(59);
	    System.out.println(roundCeilTime(time, 60));
	    System.out.println(roundCeilTime(time, 30));
	    
	    time = DateTime.now().withMinuteOfHour(31);
	    System.out.println(roundCeilTime(time, 60));
	    System.out.println(roundCeilTime(time, 30));
	    
	    time = DateTime.now().withMinuteOfHour(20);
	    System.out.println(roundCeilTime(time, 60));
	    System.out.println(roundCeilTime(time, 30));
	    
	    time = DateTime.now().withMinuteOfHour(0);
	    System.out.println(roundCeilTime(time, 60));
	    System.out.println(roundCeilTime(time, 30));
	    
	    time = DateTime.now().withMinuteOfHour(20);
	    System.out.println(roundCeilTime(time, 90));
    }
}