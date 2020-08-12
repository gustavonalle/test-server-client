import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.configuration.XMLStringConfiguration;

import java.util.*;

public class Client {

    public static void main(String[] args) {
        try {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.addServer().host("127.0.0.1").port(11222);
            builder.marshaller(new KryoMarshaller());
            RemoteCacheManager rcm = new RemoteCacheManager(builder.build());

            String cacheName = "test";
            String defaultConfiguration = "<infinispan>" +
                    "<cache-container>" +
                    "<distributed-cache name=\"%s\" mode=\"SYNC\">" +
                    "<encoding>" +
                    "<key media-type=\"application/x-kryo\"/>" +
                    "<value media-type=\"application/x-kryo\"/>" +
                    "</encoding>" +
                    "<expiration lifespan=\"%s\" />" +
                    "</distributed-cache>" +
                    "</cache-container>" +
                    "</infinispan>";
            String xmlConfiguration =  String.format(defaultConfiguration, cacheName, 600000000);

            RemoteCache<String, Map<Integer, Set<Employee>>> cache = rcm.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
                    .getOrCreateCache(cacheName, new XMLStringConfiguration(xmlConfiguration));

            Map<Integer, Set<Employee>> myMap = new HashMap<>();
            Set<Employee> mySet = new HashSet<>();

            for(int i = 0; i < 45; i++) {
                mySet.add(new Employee("Employee", i));
                myMap.put(i, mySet);
            }
            cache.put("employee",myMap);
            Map<Integer, Set<Employee>> result = cache.get("employee");
            System.out.printf("key = %s\n", result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
