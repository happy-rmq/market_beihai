package com.lenovo.market.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static String getDateFromLong(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年M月d HH:mm:ss");
        String dateString = formatter.format(time);
        return dateString;
    }

    public static String getFormatedDateString(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return formatter.format(time);
    }

    /**
     * if指定时间不为当前年份，则return"yyyy年MM月dd 上午/下午 hh:mm"；<br>
     * if指定时间不为当前年的当前月份，则return"MM月dd 上午/下午 hh:mm";<br>
     * if指定时间为当前年当前月的当天，则return"上午/下午 hh:mm";
     * 
     * @param time
     * @return
     */
    public static String getDateStrFromLong(String time) {
        if (time.contains("-")) {
            return time;
        }
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);
        int current_month = calendar.get(Calendar.MONTH) + 1;
        int current_day = calendar.get(Calendar.DAY_OF_MONTH);

        // 指定的日期
        long t = Long.parseLong(time);
        Date date = new Date(t);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int am_pm = calendar.get(Calendar.AM_PM);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String am_pm_str = am_pm == 0 ? "早上" : "下午";
        String hour_str = hour < 10 ? "0" + hour : "" + hour;
        String minute_str = minute < 10 ? "0" + minute : "" + minute;

        StringBuffer buffer = new StringBuffer();
        if (current_year != year) {
            // eg: 2013年7月1 下午14:00
            buffer.append(year).append("年").append(month).append("月").append(day).append(" ").append(am_pm_str).append(hour_str).append(":").append(minute_str);
        } else {
            if (current_month == month && current_day == day) {
                // eg: 下午14:00
                buffer.append(am_pm_str).append(hour_str).append(":").append(minute_str);
            } else {
                // eg: 7月1 下午14:00
                buffer.append(month).append("月").append(day).append(" ").append(am_pm_str).append(hour_str).append(":").append(minute_str);
            }
        }
        return buffer.toString();
    }

    /**
     * 返回longstr对应的时间距当前时间的差值 如果longstr对应的时间大于当前时间则返回null
     * 
     * @param longstr
     * @return
     */
    public static String getHoursOrDaysAgo(String longstr) {
        String str = null;
        if (null == longstr || longstr.equals(""))
            return null;
        long time = Long.parseLong(longstr);
        Calendar calendar = Calendar.getInstance();
        int current_year = calendar.get(Calendar.YEAR);
        int current_day_of_year = calendar.get(Calendar.DAY_OF_YEAR);
        int current_hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        int current_minute = calendar.get(Calendar.MINUTE);
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int day_of_year = calendar.get(Calendar.DAY_OF_YEAR);
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int ago_of_year = current_year - year;
        if (ago_of_year > 1) {
            str = (ago_of_year - 1) + "年前";
        } else if (ago_of_year == 1) {
            str = "去年";
        } else if (ago_of_year == 0) {
            // 当年
            int ago_of_day = current_day_of_year - day_of_year;
            if (ago_of_day == 1) {
                str = "昨天";
            } else if (ago_of_day > 1) {
                str = (ago_of_day - 1) + "天前";
            } else if (ago_of_day == 0) {
                int ago_of_hour = current_hour_of_day - hour_of_day;
                if (ago_of_hour > 1) {
                    str = (ago_of_hour - 1) + "小时前";
                } else if (ago_of_hour == 1) {
                    if (current_minute >= minute) {
                        str = "1小时前";
                    } else {
                        str = (60 - minute + current_minute) + "分钟前";
                    }
                } else if (ago_of_hour == 0) {
                    int ago_of_minute = current_minute - minute;
                    if (ago_of_minute > 1) {
                        str = (ago_of_minute - 1) + "分钟前";
                    } else if (ago_of_minute >= 0 && ago_of_minute <= 1) {
                        str = "1分钟前";
                    }
                }
            }

        }
        return str;
    }

    public static String getLongStrFromDateStr(String time) {
        if (!time.contains("-")) {
            return time;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime() + "";
    }
}
