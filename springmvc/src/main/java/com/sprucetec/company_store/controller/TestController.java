package com.sprucetec.company_store.controller;

import java.util.HashMap;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sprucetec.company_store.entity.StoreExt;
import com.sprucetec.company_store.service.StoreService;

@Controller
@RequestMapping("/test")
public class TestController {
	private Logger logger = Logger.getLogger(TestController.class);
	
	@Autowired
	private StoreService storeService;
	@RequestMapping(value = "/test1", method = RequestMethod.GET)
	public ModelAndView test1(StoreExt st) {
		
		HashMap<String, Object> map=new HashMap<String, Object>();
		map.put("address", storeService.getStreetByStoreExtId(28l));
		logger.info(st.getStreet());
		ModelAndView mv=new ModelAndView();
		//绑定参数
		mv.addObject("hello", map);
		
		//设置视图
		mv.setViewName("test1.jsp");
		return mv;
	}
	
}
