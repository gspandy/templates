package com.sprucetec.company_store.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sprucetec.company_store.entity.StoreExt;
import com.sprucetec.company_store.service.StoreService;

@Controller
@RequestMapping("/test")
public class TestController {
	private Logger logger = Logger.getLogger(TestController.class);
	
	@Autowired
	private StoreService storeService;
	@RequestMapping(value = "/test1/{a}/{b}/{c}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> test1(@PathVariable("a") Long a,@PathVariable("b") Long b,@PathVariable("c") Integer c) {
		try {
			storeService.update(a,b,c);
			storeService.batchUpdate(a,b,c);
			storeService.batchUpdate(b,a,c);
		} catch (Exception e) {
			System.out.println(getAllStackTraceStr(e));
			throw e;
		}
		return new HashMap<String, Object>();
	}
	
	public String getAllStackTraceStr(Throwable t){
		StringBuilder sbd=new StringBuilder();
		sbd.append(getStackTraceStr(t));
		Throwable c=t.getCause();
		do{
			sbd.append("Caused By: ").append(getStackTraceStr(c));
		}while((c=c.getCause())!=null);
		
		return sbd.toString();
	}
	
	public String getStackTraceStr(Throwable t){
		StringBuilder sbd=new StringBuilder();
		StackTraceElement[] steArr=t.getStackTrace();
		sbd.append(t.getClass().getName()).append(":").append(t.getMessage()).append("\n");
		for(StackTraceElement ste:steArr){
			sbd.append("\t").append(ste).append("\n");
		}
		return sbd.toString();
	}
}
