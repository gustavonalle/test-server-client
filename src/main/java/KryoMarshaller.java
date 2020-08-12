import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.infinispan.commons.configuration.ClassWhiteList;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.io.ExposedByteArrayOutputStream;
import org.infinispan.commons.marshall.AbstractMarshaller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class KryoMarshaller extends AbstractMarshaller {

    private static final List<SerializerRegister> serializerServices = new ArrayList<>();

    static {
        ServiceLoader.load(SerializerRegister.class, KryoMarshaller.class.getClassLoader()).forEach(serializerServices::add);
    }

    private final KryoPool pool;

    public KryoMarshaller() {
        KryoFactory factory = () -> {
            Kryo kryo = new Kryo();
            serializerServices.forEach(service -> service.register(kryo));
            return kryo;
        };
        this.pool = new KryoPool.Builder(factory).softReferences().build();
    }

    @Override
    public void initialize(ClassWhiteList classWhiteList) {

    }

    @Override
    public Object objectFromByteBuffer(byte[] bytes, int offset, int length) throws IOException, ClassNotFoundException {
        return pool.run((kryo) -> {
            try (Input input = new Input(bytes, offset, length)) {
                return kryo.readClassAndObject(input);
            }
        });
    }

    @Override
    protected ByteBuffer objectToBuffer(Object obj, int estimatedSize) throws IOException, InterruptedException {
        return pool.run((kryo) -> {
            try (Output output = new Output(new ExposedByteArrayOutputStream(estimatedSize), estimatedSize)) {
                kryo.writeClassAndObject(output, obj);
                return ByteBufferImpl.create(output.toBytes());
            }
        });
    }

    @Override
    public boolean isMarshallable(Object obj) throws Exception {
        try {
            objectToBuffer(obj);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public MediaType mediaType() {
        return MediaType.APPLICATION_KRYO;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
