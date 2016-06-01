package com.sprucetec.company_store.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprucetec.company_store.dao.StoreExtDao;
import com.sprucetec.company_store.entity.StoreExt;
import com.sprucetec.company_store.manager.StoreManager;
import com.sprucetec.company_store.service.StoreService;
@Service
public class StoreServiceImpl implements StoreService{
	@Autowired
	private StoreManager storeManager;
	@Autowired
	private StoreExtDao storeDao;
	@Transactional
	@Override
	public void update(Long a,Long b,Integer c) {
		StoreExt s=storeManager.findById(a);
		storeManager.update(s);
		System.out.println("request"+c+":update obj"+a);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		StoreExt s1=storeManager.findById(b);
		storeManager.update(s1);
		System.out.println("request"+c+":update obj"+b);
	}

	@Transactional
	@Override
	public void batchUpdate(Long a,Long b,Integer c) {
		StoreExt s=storeManager.findById(a);
		StoreExt s1=storeManager.findById(b);
		List<StoreExt> list=new ArrayList<StoreExt>();
		list.add(s);
		list.add(s1);
		this.storeDao.batchUpdate(list);
	}
}
