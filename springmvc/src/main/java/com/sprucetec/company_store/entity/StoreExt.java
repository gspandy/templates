package com.sprucetec.company_store.entity;

import com.sprucetec.company_store.base.BaseEntity;


public class StoreExt extends BaseEntity{
	private static final long serialVersionUID = 1L;
	private String street;
	private Integer floor=0;
	private Integer hasLift=-1;
	private Long companyId;
	private String saleCode="";
	
	public String getSaleCode() {
		return saleCode;
	}
	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getHasLift() {
		return hasLift;
	}
	public void setHasLift(Integer hasLift) {
		this.hasLift = hasLift;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	
	
}
