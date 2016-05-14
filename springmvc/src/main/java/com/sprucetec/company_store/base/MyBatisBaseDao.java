package com.sprucetec.company_store.base;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.sprucetec.bone.common.PageCondition;

/**
 * MyBatis DAO通用操作类
 * @author LDJ
 * @param <T>
 * @param <PK>
 * @Date 2016-01-30
 */
public interface MyBatisBaseDao<T, PK extends Serializable>{
	/**
	 * 增加实体
	 * 
	 * @param object
	 */
	public Integer insert(@Param("obj") T object);
	
 

	/**
	 * 按条件查询实体
	 * 
	 * @param obj
	 * @return
	 */
	public List<T> findByCondition(@Param("obj") T object);

	/**
	 * 按条件查询实体并分页
	 * 
	 * @param obj
	 * @return
	 */
	public List<T> findByCondition(@Param("condition") PageCondition condition,@Param("obj") T object);

	/**
	 * 按主键查询
	 * 
	 * @param pk
	 * @return
	 */
	public T findByPK(@Param("id") PK pk, Class<T> cls);

	/**
	 * 更新实体
	 * 
	 * @param object
	 */
	public int update(T object);

	/**
	 * 按主键删除实体
	 * 
	 * @param pk
	 */
	public int delete(@Param("id") PK pk, Class<T> cls);
	

	/**
	 * 按条件查询总记录数
	 * 
	 * @param object
	 * @return
	 */
	public long getTotalCount(@Param("obj") T object);
	
	/**
	 * 批量插入
	 * @description 基本原生SQL的数据库插入
	 * @param cls
	 * @param domainList 
	 * @throws
	 */
	public int insertBatch(@Param("list") List<T> domainList);
//	
//	/**
//	 * 批量更新
//	 * @param statementname 更新SQL的ID（sqlMap中）
//	 * @param domainList 需要更新的集合
//	 * @param count 表示多少笔数据提交一次
//	 */
//	public void updateBatch(Class<T> cls, @Param("list")List<T> domainList,Integer count);
//	
}
