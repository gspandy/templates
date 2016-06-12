package net.xicp.hkscript.gateway.gateway;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.text.StrTokenizer;

/**
 * 在某一时间范围内qps限制
 * 格式如下
 * 日期范围
 * qps_limit/g_qps_limit的配置格式如下
 * ${startDate}~${endDate}/${qps_limit}或者${qps_limit}
 * 1.可以根据开始/截至时间来配置每秒访问限制
 * 2.可以直接配置每秒访问限制
 * date的格式可以为yyyy-MM-dd/yyyy-MM-dd HH:mm/HH:mm/yyyy-MM-dd HH 4种
 * 
 * 
 * 
 * @author daiyufeng@meicai.cn
 *
 */
public class DateRangeConditionForQPSLimit implements ConditionalQPSLimit {
    
    private Calendar startTime;
    
    private Calendar endTime;
    
    private Long qpsLimit;
    
    public static int WITHOUT_YEAR = 1;
    public static int WITHOUT_YEAR_AND_MONTH = 2;
    
    /**
     * 校验是
     */
    private int startTimeFormat = 0;
    
    public int getStartTimeFormat() {
        return startTimeFormat;
    }

    public void setStartTimeFormat(int startTimeFormat) {
        this.startTimeFormat = startTimeFormat;
    }


    private int endTimeFormat = 0;
    

    public int getEndTimeFormat() {
        return endTimeFormat;
    }

    public void setEndTimeFormat(int endTimeFormat) {
        this.endTimeFormat = endTimeFormat;
    }

    public void setQpsLimit(Long qpsLimit) {
        this.qpsLimit = qpsLimit;
    }

    public boolean matches(Date date) {
        //return (startTime==null||this.startTime.before(date))&&(endTime==null&&this.endTime.after(date));
        Calendar startTimeForCheck = null;
        Calendar checkeDate = Calendar.getInstance();
        checkeDate.setTime(date);
        if(startTimeFormat == 0){
            startTimeForCheck = startTime;
        }
        //没有年份
        else if(startTimeFormat == WITHOUT_YEAR&&startTime!=null){
            startTimeForCheck = Calendar.getInstance();
            startTimeForCheck.setTime(startTime.getTime());
            startTimeForCheck.set(Calendar.YEAR, checkeDate.get(Calendar.YEAR));
        }
        //年份/月份都没有
        else if(startTimeFormat == WITHOUT_YEAR_AND_MONTH&&startTime!=null){
            startTimeForCheck = Calendar.getInstance();
            startTimeForCheck.setTime(startTime.getTime());
            startTimeForCheck.set(Calendar.YEAR, checkeDate.get(Calendar.YEAR));
            startTimeForCheck.set(Calendar.MONTH, checkeDate.get(Calendar.MONTH));
            startTimeForCheck.set(Calendar.DAY_OF_MONTH, checkeDate.get(Calendar.DAY_OF_MONTH));
        }
        
        Calendar endTimeForCheck = null;
        if(endTimeFormat == 0){
            endTimeForCheck = endTime;
        }
        //没有年份
        else if(endTimeFormat == WITHOUT_YEAR && endTime!=null){
            endTimeForCheck = Calendar.getInstance();
            endTimeForCheck.setTime(endTime.getTime());
            endTimeForCheck.set(Calendar.YEAR, checkeDate.get(Calendar.YEAR));
        }
        //年份/月份都没有
        else if(endTimeFormat == WITHOUT_YEAR_AND_MONTH&&endTime!=null){
            endTimeForCheck = Calendar.getInstance();
            endTimeForCheck.setTime(endTime.getTime());
            endTimeForCheck.set(Calendar.YEAR, checkeDate.get(Calendar.YEAR));
            endTimeForCheck.set(Calendar.MONTH, checkeDate.get(Calendar.MONTH));
            endTimeForCheck.set(Calendar.DAY_OF_MONTH, checkeDate.get(Calendar.DAY_OF_MONTH));
        }        
        return (startTimeForCheck==null||startTimeForCheck.getTimeInMillis()<=date.getTime())&&(endTime==null||endTimeForCheck.getTimeInMillis()>date.getTime());
    }

    public Long getQpsLimit() {
        return qpsLimit;
    }
    
    public void setStartTime(Calendar startTime){
        this.startTime = Calendar.getInstance();
        this.startTime.setTime(startTime.getTime());
    }


    public void setEndTime(Calendar endTime) {
        this.endTime = Calendar.getInstance();
        this.endTime.setTime(endTime.getTime());
    }

}
