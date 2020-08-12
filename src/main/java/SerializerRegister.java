import com.esotericsoftware.kryo.Kryo;
import org.infinispan.marshaller.kryo.SerializerRegistryService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SerializerRegister implements SerializerRegistryService {
    @Override
    public void register(Kryo kryo) {
        kryo.register(HashMap.class);
        kryo.register(HashSet.class);
        kryo.register(Map.class, kryo.getSerializer(HashMap.class));
        kryo.register(Set.class , kryo.getSerializer(HashSet.class));
        kryo.register(Employee.class);
    }
}
