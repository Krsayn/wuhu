package util;

import java.util.HashMap;
import java.util.Map;

public class redisUtil {
    public static void createRedis(String id,String name,String GWName ){
        Jedis jedis;
        jedis = new Jedis("localhost", 6379);

        Map<String, String> map = new HashMap<String, String>();
        map.put("id",id);
        map.put("name",name);
        map.put("GWName",GWName);
        jedis.hmset(id,map);
    }
}
