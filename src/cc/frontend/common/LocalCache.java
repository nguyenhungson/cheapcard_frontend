/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.common;

import com.vng.jcore.cache.lruexpire.LruExpireCache;
import com.vng.jcore.cache.lruexpire.LruExpireCacheManager;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author thangblc
 */
public class LocalCache
{
    protected static final Lock createLock_ = new ReentrantLock();
    private static LruExpireCache<String, List> lstCache = null;
    private static LruExpireCache<String, Object> lstCacheObject = null;
    private static LocalCache _instance = null;
    
    static 
    {
        lstCache = LruExpireCacheManager.getCache("commonLst", 1000);
        lstCacheObject = LruExpireCacheManager.getCache("commonLst", 1000);
    }
    public static LocalCache getInstance() {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new LocalCache();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    public void setStrCache(String key, List value)
    {
        lstCache.put(key, value,-1);
    }
    
    public void setStrCache(String key, List value, long expired)
    {
        lstCache.put(key, value,expired);
    }
    
    public List getStrCache(String key)
    {
        return lstCache.get(key);
    }
    
    public String dumpStrCache()
    {
        return lstCache.dumpProfilerHtml();
    }
    
    public void removeCache(String key)
    {
        lstCache.remove(key);
    }
    
    public void setObjectCache(String key, Object value, int time){
        lstCacheObject.put(key, value, time);
    }
    
    public Object getObjectCache(String key){
        return lstCacheObject.get(key);
    }
    
    public void removeObjectCache(String key){
        lstCacheObject.remove(key);
    }
    
}
