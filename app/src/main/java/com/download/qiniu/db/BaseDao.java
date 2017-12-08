package com.download.qiniu.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 操作数据库 (注意有的操作需要加上店铺编号条件)
 * @author chenwenlu
 * @Description
 * @date 2015年10月21日 下午4:22:04 
 * @param <T> 要操作的表对应的bean
 */
public abstract class BaseDao<T extends IOperateType> {
//    protected DatabaseHelper mDatabaseHelper;  
//    protected Context mContext;  
//    public BaseDao(Context context) {  
//        mContext = context;  
//        getHelper();  
//    }  
//    public DatabaseHelper getHelper() {  
//        if (mDatabaseHelper == null) {  
//            mDatabaseHelper = OpenHelperManager.getHelper(mContext, DatabaseHelper.class);  
//        }  
//        return mDatabaseHelper;  
//    }  
	/**
	 * 店铺编号
	 */
//	private String storeNo;

//	public final String StoreNo="StoreNo";
	
    public abstract Dao<T, Integer> getDao() throws SQLException;  
    
    /**
     * 插入一行数据
     * @param t
     * @return
     * @throws SQLException
     */
    public void save(T t) throws SQLException {  
         getDao().createOrUpdate(t);  
    }  
  
    public void saveList(List<T> ts) throws SQLException{
    	for (T t : ts) {
//    		if(t.OperateType==3){
//    			delete(t);
//    		}else{
    			getDao().createOrUpdate(t);
//    		}
			
		}
    }
    
    
    public List<T> query(PreparedQuery<T> preparedQuery) throws SQLException {  
        Dao<T, Integer> dao = getDao();  
        return dao.query(preparedQuery);  
    }  
    
    public int update(PreparedUpdate<T> preparedUpdate) throws SQLException{
    	Dao<T, Integer> dao = getDao(); 
    	return dao.update(preparedUpdate);
    }
    
    

    
    
    /**
     * 类似于where子句
     * @param attributeName 字段名
     * @param attributeValue 字段值
     * @return
     * @throws SQLException
     */
    public List<T> query(String attributeName, String attributeValue) throws SQLException {  
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();  
        queryBuilder.where().eq(attributeName, attributeValue);
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
        return query(preparedQuery);  
    }  
  
    /**
     * 分页查询
     * @param offset 查询起始位置
     * @param limit 查询数量
     * @return
     * @throws SQLException 
     */
    public List<T> queryPage(long offset,long limit) throws SQLException{
    	QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder(); 
    	queryBuilder.offset(offset);
    	queryBuilder.limit(limit);
//    	queryBuilder.where().eq(StoreNo, storeNo);
    	PreparedQuery<T> preparedQuery = queryBuilder.prepare();
    	 return query(preparedQuery);
    }
    
    /**
     * 分页查询
     * @param offset 查询起始位置
     * @param limit 查询数量
     * @return
     * @throws SQLException 
     */
    public List<T> queryPage(long offset,long limit,String key,String value) throws SQLException{
    	QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder(); 
    	queryBuilder.offset(offset);
    	queryBuilder.limit(limit);
//    	queryBuilder.where().eq(StoreNo, storeNo).and().eq(key, value);
    	PreparedQuery<T> preparedQuery = queryBuilder.prepare();
    	 return query(preparedQuery);
    }
    
    /**
     * 只用于销售明细列表
     * @param offset
     * @param limit
     * @param ascending
     * @return
     * @throws SQLException
     */
    public List<T> queryPage(long offset,long limit,boolean ascending) throws SQLException{
    	QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder(); 
    	queryBuilder.offset(offset);
    	queryBuilder.limit(limit);
//    	queryBuilder.where().eq(StoreNo, storeNo);
    	queryBuilder.orderBy("CreatedOn", ascending);
    	PreparedQuery<T> preparedQuery = queryBuilder.prepare();
    	 return query(preparedQuery);
    }
    
    public List<T> queryPageById(int id,long offset,long limit) throws SQLException{
    	QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder(); 
    	queryBuilder.offset(offset);
    	queryBuilder.limit(limit);
//    	queryBuilder.where().eq(StoreNo, storeNo).and().eq("MemberId", id);
    	PreparedQuery<T> preparedQuery = queryBuilder.prepare();
    	 return query(preparedQuery);
    }
   
    /**
     * 只用于销售明细列表
     * @param id
     * @param offset
     * @param limit
     * @param ascending
     * @return
     * @throws SQLException
     */
    public List<T> queryPageById(int id,long offset,long limit,boolean ascending) throws SQLException{
    	QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder(); 
    	queryBuilder.offset(offset);
    	queryBuilder.limit(limit);
//    	queryBuilder.where().eq(StoreNo, storeNo).and().eq("MemberId", id);
    	queryBuilder.orderBy("CreatedOn", ascending);
    	PreparedQuery<T> preparedQuery = queryBuilder.prepare();
    	 return query(preparedQuery);
    }
    
    /**
     * 模糊查询
     * @param value
     * @return
     * @throws SQLException 
     */
    public List<T> queryLike(String[] column,String[] value) throws SQLException{
		QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
		Where<T, Integer> wheres=queryBuilder.where();
		//wheres.eq(StoreNo, storeNo).and();//这里这样写与后面写生成的sql语句是不一样的
		//这样写只会与第一个后续语句and
		for(int i=0;i<column.length;i++){
    		if(i<column.length-1){
    			wheres.like(column[i], "%"+value[i]+"%").or();
    		}else{
    			wheres=	wheres.like(column[i], "%"+value[i]+"%");
    		}
    	}
//		wheres.and().eq(StoreNo, storeNo);
		PreparedQuery<T> preparedQuery = queryBuilder.prepare();
		 return query(preparedQuery);
    	
    	
    }
    
    public List<T> queryColum(String colum) throws SQLException{
    	 QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder(); 
    	 queryBuilder.selectColumns(colum);
//    	 queryBuilder.where().eq(StoreNo, storeNo);
    	 PreparedQuery<T> preparedQuery = queryBuilder.prepare();
    	 return query(preparedQuery);
    }
    
    public List<T> query(String[] attributeNames, String[] attributeValues) throws SQLException,  
            IllegalArgumentException {  
        if (attributeNames.length != attributeValues.length) {  
            throw new IllegalArgumentException("params size is not equal");  
        }  
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();  
        Where<T, Integer> wheres = queryBuilder.where();  
        for (int i = 0; i < attributeNames.length; i++) {  
            wheres.eq(attributeNames[i], attributeValues[i]);
        }  
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
        return query(preparedQuery);  
    }  
  
    
    public List<T> queryAll() throws SQLException {  
         QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
//         queryBuilder.where().eq(StoreNo, storeNo);
         PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
         return query(preparedQuery);  
//        Dao<T, Integer> dao = getDao();  
//        return dao.queryForAll();  
    }  
  
    public T queryById(String idName, String idValue) throws SQLException {  
        List<T> lst = query(idName, idValue);  
        if (null != lst && !lst.isEmpty()) {  
            return lst.get(0);  
        } else {  
            return null;  
        }  
    }  
  
    public T queryById(String id) throws SQLException {  
        List<T> lst = query("Id", id);  
        if (null != lst && !lst.isEmpty()) {  
            return lst.get(0);  
        } else {  
            return null;  
        }  
    }  
    public int delete(PreparedDelete<T> preparedDelete) throws SQLException {  
        Dao<T, Integer> dao = getDao();  
        return dao.delete(preparedDelete);  
    }  
  
    public int delete(T t) throws SQLException {  
        Dao<T, Integer> dao = getDao();  
        return dao.delete(t);  
    }  
  
    public int delete(List<T> lst) throws SQLException {  
        Dao<T, Integer> dao = getDao(); 
        return dao.delete(lst);  
    }  
  
    public int delete(String[] attributeNames, String[] attributeValues) throws SQLException,  
    IllegalArgumentException {  
        List<T> lst = query(attributeNames, attributeValues);  
        if (null != lst && !lst.isEmpty()) {  
            return delete(lst);  
        }  
        return 0;  
    }  
  
    public int deleteById(String idName, String idValue) throws SQLException,  
    IllegalArgumentException {  
        T t = queryById(idName, idValue);  
        if (null != t) {  
            return delete(t);  
        }  
        return 0;  
    }  
  
    public int update(T t) throws SQLException {  
        Dao<T, Integer> dao = getDao();  
        return dao.update(t);  
    }  
  
    public boolean isTableExsits() throws SQLException {  
        return getDao().isTableExists();  
    }  
  
    public long countOf() throws SQLException {  
        return getDao().countOf();  
    }  
  
    public List<T> query(Map<String, Object> map) throws SQLException {  
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();  
        if (!map.isEmpty()) {  
            Where<T, Integer> wheres = queryBuilder.where();  
            Set<String> keys = map.keySet();  
            ArrayList<String> keyss = new ArrayList<String>();  
            keyss.addAll(keys);  
            for (int i = 0; i < keyss.size(); i++) {  
                if (i == 0) {  
                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));  
                } else {  
                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));  
                }  
            }  
        }  
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
        return query(preparedQuery);  
    }  
  
    public List<T> query(Map<String, Object> map, Map<String, Object> lowMap,  
            Map<String, Object> highMap) throws SQLException {  
        QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();  
        Where<T, Integer> wheres = queryBuilder.where();  
        if (!map.isEmpty()) {  
            Set<String> keys = map.keySet();  
            ArrayList<String> keyss = new ArrayList<String>();  
            keyss.addAll(keys);  
            for (int i = 0; i < keyss.size(); i++) {  
                if (i == 0) {  
                    wheres.eq(keyss.get(i), map.get(keyss.get(i)));  
                } else {  
                    wheres.and().eq(keyss.get(i), map.get(keyss.get(i)));  
                }  
            }  
        }  
        if (!lowMap.isEmpty()) {  
            Set<String> keys = lowMap.keySet();  
            ArrayList<String> keyss = new ArrayList<String>();  
            keyss.addAll(keys);  
            for (int i = 0; i < keyss.size(); i++) {  
                if(map.isEmpty()){  
                    wheres.gt(keyss.get(i), lowMap.get(keyss.get(i)));  
                }else{  
                    wheres.and().gt(keyss.get(i), lowMap.get(keyss.get(i)));  
                }  
            }  
        }  
  
        if (!highMap.isEmpty()) {  
            Set<String> keys = highMap.keySet();  
            ArrayList<String> keyss = new ArrayList<String>();  
            keyss.addAll(keys);  
            for (int i = 0; i < keyss.size(); i++) {  
                wheres.and().lt(keyss.get(i), highMap.get(keyss.get(i)));  
            }  
        }  
        PreparedQuery<T> preparedQuery = queryBuilder.prepare();  
        return query(preparedQuery);  
    }  
}


