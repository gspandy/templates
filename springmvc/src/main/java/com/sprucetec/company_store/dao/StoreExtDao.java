package com.sprucetec.company_store.dao;



import java.util.List;

import com.sprucetec.company_store.base.MyBatisBaseDao;
import com.sprucetec.company_store.entity.StoreExt;


public interface StoreExtDao extends MyBatisBaseDao<StoreExt, Long>{
	public Integer insert(StoreExt ext);
	public Integer batchUpdate(List<StoreExt> list);
}