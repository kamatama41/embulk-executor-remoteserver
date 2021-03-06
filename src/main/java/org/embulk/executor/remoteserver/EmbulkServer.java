package org.embulk.executor.remoteserver;

import com.github.kamatama41.nsocket.SocketServer;

import java.io.IOException;

public class EmbulkServer implements AutoCloseable {
    private SocketServer server;

    private EmbulkServer(SocketServer  server) {
        this.server = server;
    }

    static EmbulkServer start(String host, int port, int numOfWorkers, TLSConfig tlsConfig) throws IOException {
        SocketServer server = new SocketServer();
        ServerSessionRegistry sessionRegistry = new ServerSessionRegistry();
        server.setHost(host);
        server.setPort(port);
        server.setDefaultContentBufferSize(4 * 1024 * 1024); // 4MB
        server.setNumOfWorkers(numOfWorkers);
        server.registerSyncCommand(new InitializeSessionCommand(sessionRegistry));
        server.registerSyncCommand(new RemoveSessionCommand(sessionRegistry));
        server.registerCommand(new StartTaskCommand(sessionRegistry));
        if (tlsConfig != null) {
            server.setSslContext(tlsConfig.getSSLContext());
            if (tlsConfig.isEnableClientAuth()) {
                server.enableSslClientAuth();
            }
        }
        server.start();
        return new EmbulkServer(server);
    }

    @Override
    public void close() throws IOException {
        server.stop();
    }
}
