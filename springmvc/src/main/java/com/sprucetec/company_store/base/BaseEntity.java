/**
 * 文件名：BaseEntity.java
 * 版本信息：Version 1.0
 * 日期：2016年2月2日
 * Copyright 北京云杉世界信息技术有限公司 Corporation 2016 
 * 版权所有
 */
package com.sprucetec.company_store.base;

import java.io.Serializable;

/**
 * 
* 类描述：
* @version: 1.0
* 日期：2016年2月23日
* Copyright 北京云杉世界信息技术有限公司 Corporation 2016 
* @author: zhangzuopeng(zhangzuopeng@meicai.cn)
 */
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	// 唯一id
	private Long id;
	// 是否删除
	private Integer isDeleted = 0;
	// 创建时间
	private Integer cT = 0;
	// 修改时间
	private Integer uT = 0;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Integer getCT() {
		return cT;
	}
	public void setCT(Integer cT) {
		this.cT = cT;
	}
	public Integer getUT() {
		return uT;
	}
	public void setUT(Integer uT) {
		this.uT = uT;
	}

	

}
