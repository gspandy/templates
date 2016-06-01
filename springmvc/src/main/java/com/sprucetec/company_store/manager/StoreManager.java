package com.sprucetec.company_store.manager;

import com.sprucetec.company_store.entity.StoreExt;

public interface StoreManager {
	StoreExt findById(Long id);

	void update(StoreExt ext);
}
