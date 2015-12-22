/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.common;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.spy.memcached.MemcachedClient;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class CallMemcache
{
    protected static final Lock createLock_ = new ReentrantLock();
    private static MemcachedClient memcache = null;
    private static Logger logger = Logger.getLogger(CallMemcache.class);
    private static CallMemcache _instance = null;
    
    public static CallMemcache getInstance() {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new CallMemcache();
                }
            }
            catch(Exception ex){
                logger.error("Establish memcache fail", ex);
            }
            finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    public CallMemcache() throws Exception{
        memcache = new MemcachedClient(new InetSocketAddress(TGRConfig.gMemcache.getHost(), Integer.parseInt(TGRConfig.gMemcache.getPort())));
    }
    
    public void setCache(String key, Object value, int timeExpire){
        memcache.set(key, timeExpire, value);
    }
    
    public Object getCache(String key){
        try{
            return memcache.get(key);
        }
        catch(Exception ex){
            logger.error("Not get cache", ex);
            return null;
        }
    }
    
    public void removeCache(String key){
        memcache.delete(key);
    }
    
}
