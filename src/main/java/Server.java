import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.server.core.admin.embeddedserver.EmbeddedServerAdminOperationHandler;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {
        try {
            String config_path = "infinispan.xml";

            DefaultCacheManager defaultCacheManager = new DefaultCacheManager(config_path);
            HotRodServerConfigurationBuilder builder = new HotRodServerConfigurationBuilder();
            builder.host("127.0.0.1")
                    .port(11222)
                    .adminOperationsHandler(new EmbeddedServerAdminOperationHandler());

            HotRodServer server = new HotRodServer();
            server.start(builder.build(), defaultCacheManager);
            System.out.println("server started...");

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }
}
