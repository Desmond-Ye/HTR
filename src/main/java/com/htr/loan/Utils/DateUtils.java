package com.htr.loan.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


public abstract class DateUtils {

	public static final SimpleDateFormat YYYYlMMlDD = new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat YYYYLMMLDD_BIG = new SimpleDateFormat("yyyy/MM/dd 23:59:59");
	public static final SimpleDateFormat YYYYLMMLDD_SMALL = new SimpleDateFormat("yyyy/MM/dd 00:00:00");


	/**
	 * 比较两个日期的大小
	 * @param sourceDate 源日期
	 * @param targetDate 目标日期
	 * @return  相差天数
	 */
	public static final long between(Date sourceDate, Date targetDate){
		if (null == sourceDate || null == targetDate) {
			throw new RuntimeException("日期转换不能完成,因为其中一个日期为空!!");
		}
		return dateToLocalDate(sourceDate).toEpochDay() - dateToLocalDate(targetDate).toEpochDay();
	}

	/**
	 * 比较两个日期的大小
	 * @param sourceDate 源日期
	 * @param targetDate 目标日期
	 * @return  相差天数
	 */
	public static final long between(Date sourceDate, LocalDate targetDate){
		if (null == sourceDate || null == targetDate) {
			throw new RuntimeException("日期转换不能完成,因为其中一个日期为空!!");
		}
		return dateToLocalDate(sourceDate).toEpochDay() - targetDate.toEpochDay();
	}

	/**
	 * 是否是当前时间
	 * @param date
	 * @param pattern 比较当前时间的格式
	 * @return
	 */
	private static boolean isThisTime(Date date,String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String param = sdf.format(date);//参数时间
		String now = sdf.format(new Date());//当前时间
		if(param.equals(now)){
			return true;
		}
		return false;
	}

	/**
	 * 是否是今天
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date) {
		return isThisTime(date,"yyyy-MM-dd");
	}

	/**
	 * 是否是本月
	 * @param date
	 * @return
	 */
	public static boolean isThisMonth(Date date) {
		return isThisTime(date,"yyyy-MM");
	}

	/**
	 * 是否是本年
	 * @param date
	 * @return
	 */
	public static boolean isThisYear(Date date) {
		return isThisTime(date,"yyyy");
	}


	/**
	 * 现在天数加一天
	 * @param date
	 * @return
	 */
	public static String addOneDays(final Date date, SimpleDateFormat sdf) {
		return sdf.format(org.apache.commons.lang3.time.DateUtils.addDays(date, 1));
	}

	/**
	 * 现在天数加一天
	 * @param date
	 * @return
	 */
	public static Date addOneYears(final Date date) {
		return org.apache.commons.lang3.time.DateUtils.addYears(date, 1);
	}

	/**
	 * 格式化日期
	 * @param date
	 * @param sdf
	 * @return
	 */
	public static String formatDate(final Date date, SimpleDateFormat sdf) {
		return sdf.format(date);
	}

	public static Date parseDate(final String str) {
		Date date = null;
		try {
			date = org.apache.commons.lang3.time.DateUtils.parseDate(str, Constants.POSSIBLE_DATE_FORMATS);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("简析日期错误!!");
		}
		return date;
	}

	/**
	 * 转换 Date类 为 LocalDate类
	 * @param date
	 * @return
	 */
	public static LocalDate dateToLocalDate(Date date) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		return  localDateTime.toLocalDate();
	}
}
