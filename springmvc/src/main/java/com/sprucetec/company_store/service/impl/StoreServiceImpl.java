package com.sprucetec.company_store.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sprucetec.company_store.entity.StoreExt;
import com.sprucetec.company_store.manager.StoreManager;
import com.sprucetec.company_store.service.StoreService;
@Service
public class StoreServiceImpl implements StoreService{
	@Autowired
	private StoreManager storeManager;
	
	@Override
	public String getStreetByStoreExtId(Long id) {
		StoreExt s=storeManager.findById(id);
		if(s!=null){
			return s.getStreet();
		}
		return null;
	}

}
