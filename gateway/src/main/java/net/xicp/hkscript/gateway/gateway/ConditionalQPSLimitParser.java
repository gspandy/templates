package net.xicp.hkscript.gateway.gateway;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 配置之间用逗号隔开，例如 12:00~13:00/1000,2015-05-06~2015-08-10/5000,1000 12点到1点qps限制为1k
 * 2015-05-06到2015-08-10 qps限制为5k 除此之外qps限制为1k qps_limit/g_qps_limit的配置格式如下
 * ${startDate}~${endDate}/${qps_limit}或者${qps_limit} 1.可以根据开始/截至时间来配置每秒访问限制
 * 2.可以直接配置每秒访问限制 date的格式可以为yyyy-MM-dd/yyyy-MM-dd HH:mm/HH:mm/yyyy-MM-dd HH/MM-dd/MM:dd HH:mm 6种
 * 
 * @author daiyufeng@meicai.cn
 * 
 *
 */
public class ConditionalQPSLimitParser {

    private Logger logger = Logger.getLogger(this.getClass());

    public List<SimpleDateFormat> createPattern() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH");
        SimpleDateFormat format4 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat format5 = new SimpleDateFormat("MM:dd");
        SimpleDateFormat format6 = new SimpleDateFormat("MM:dd HH:mm");
        List ret = new ArrayList();
        ret.add(format1);
        ret.add(format2);
        ret.add(format3);
        ret.add(format4);
        ret.add(format5);
        ret.add(format6);
        return ret;
    }

    public List<ConditionalQPSLimit> parse(String qpsLimit) {

        String[] limits = qpsLimit.split(",");
        List<ConditionalQPSLimit> ret = new ArrayList(limits.length);
        for (String limit : limits) {
            ConditionalQPSLimit limitCondition = this.parseCondition(limit);
            if (limitCondition != null) {
                ret.add(limitCondition);
            }
        }

        return ret;
    }

    /**
     * 解析条件 $startTime~$endTime/$qpsLimit或者直接qpsLimit
     * 时间格式支持yyyy-MM-dd或者yyyy-MM-dd HH:mm或者HH:mm 起始时间之间用~波浪号隔开
     * 起始时间后面跟/斜杠，然后设置qpsLimit
     * 
     * @param limit
     * @return
     */
    public ConditionalQPSLimit parseCondition(String limit) {
        int indexOfTilde = limit.indexOf("~");
        int indexOfSlash = limit.indexOf("/");
        if (indexOfTilde > 0 && indexOfSlash > indexOfTilde) {
            String startTimeStr = limit.substring(0, indexOfTilde);
            String endTimeStr = limit.substring(indexOfTilde + 1, indexOfSlash);
            String limitStr = limit.substring(indexOfSlash + 1);
            Long qpsLimit = null;
            try {
                qpsLimit = Long.parseLong(limitStr.trim());

            } catch (Exception e) {
                logger.warn("parse limit error:" + limit);
            }

            Calendar startTimeC = null;
            Calendar endTimeC = null;
            List<SimpleDateFormat> dateFormats = this.createPattern();
            Iterator<SimpleDateFormat> itDateFormat = dateFormats.iterator();
            String startTimeFormat = null;
            while (itDateFormat.hasNext()) {
                try {
                    SimpleDateFormat format = itDateFormat.next();

                    Date startTime = format.parse(startTimeStr.trim());
                    startTimeC = Calendar.getInstance();
                    startTimeC.setTime(startTime);
                    startTimeFormat = format.toPattern();
                    break;
                } catch (Exception e) {

                }

            }

            String endTimeFormat = null;
            itDateFormat = dateFormats.iterator();

            while (itDateFormat.hasNext()) {
                try {
                    SimpleDateFormat format = itDateFormat.next();

                    Date endTime = format.parse(endTimeStr.trim());
                    endTimeC = Calendar.getInstance();
                    endTimeC.setTime(endTime);
                    endTimeFormat = format.toPattern();
                    break;
                } catch (Exception e) {

                }

            }

            if (startTimeC == null || endTimeC == null || qpsLimit == null) {
                logger.warn("parse error:" + limit);
            }
            else{
                DateRangeConditionForQPSLimit dateRangeConditionForQPSLimit = new DateRangeConditionForQPSLimit();
                dateRangeConditionForQPSLimit.setStartTime(startTimeC);
                if(!startTimeFormat.contains("yyyy")){
                    dateRangeConditionForQPSLimit.setStartTimeFormat(DateRangeConditionForQPSLimit.WITHOUT_YEAR);
                }
                
                if(!startTimeFormat.contains("MM")){
                    dateRangeConditionForQPSLimit.setStartTimeFormat(DateRangeConditionForQPSLimit.WITHOUT_YEAR_AND_MONTH);
                }
                
                dateRangeConditionForQPSLimit.setEndTime(endTimeC);
                
                if(!endTimeFormat.contains("yyyy")){
                    dateRangeConditionForQPSLimit.setEndTimeFormat(DateRangeConditionForQPSLimit.WITHOUT_YEAR);
                }
                
                if(!endTimeFormat.contains("MM")){
                    dateRangeConditionForQPSLimit.setEndTimeFormat(DateRangeConditionForQPSLimit.WITHOUT_YEAR_AND_MONTH);
                }
                
                
                dateRangeConditionForQPSLimit.setQpsLimit(qpsLimit);
                return dateRangeConditionForQPSLimit;
            }

        } else {
            try {
                Long qpsLimit = Long.parseLong(limit.trim());
                UnconditionalQpsLimit l = new UnconditionalQpsLimit();
                l.setQpsLimit(qpsLimit);
                return l;

            } catch (Exception e) {
                logger.warn("parse limit error:" + limit);
            }
        }
        return null;

    }
}
