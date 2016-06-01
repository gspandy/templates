package com.sprucetec.company_store.manager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sprucetec.company_store.dao.StoreExtDao;
import com.sprucetec.company_store.entity.StoreExt;
import com.sprucetec.company_store.manager.StoreManager;
@Component
public class StoreManagerImpl implements StoreManager{
	@Autowired
	private StoreExtDao storeExtDao;
	@Override
	public StoreExt findById(Long id) {
		return storeExtDao.findByPK(id, StoreExt.class);
	}
	@Override
	public void update(StoreExt ext) {
		storeExtDao.update(ext);
	}

}
