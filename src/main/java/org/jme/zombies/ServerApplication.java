package org.jme.zombies;

import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.FieldSerializer;
import com.jme3.network.service.HostedService;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.server.EntityDataHostedService;
import java.io.IOException;
import org.jme.zombies.game.component.NodeComponent;
import org.jme.zombies.game.component.PositionComponent;
import org.jme.zombies.game.server.GameLogic;
import static com.jme3.network.MessageConnection.CHANNEL_DEFAULT_RELIABLE;

public class ServerApplication {

    private final Server server;
    private final DefaultEntityData entityData;
    private final GameLogic gameLogic;

    public ServerApplication() throws IOException {
        this.server = Network.createServer("default-server", 1, 9942, 9942);
        this.entityData = new DefaultEntityData();
        this.gameLogic = new GameLogic(entityData);
    }

    public static void main(String[] args) throws IOException {
        var app = new ServerApplication();

        app.addService(new EntityDataHostedService(CHANNEL_DEFAULT_RELIABLE, app.entityData));
        app.registerClasses();

        app.server.start();
        System.out.println("Server started.  Press Ctrl-C to stop.");
        app.run();
    }

    private void run() {
        try {
            while (true) {
                gameLogic.update();
                server.getServices().getService(EntityDataHostedService.class).sendUpdates();
                Thread.sleep(100); // 10 times a second
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Closing connections...");
            for (HostedConnection connection : server.getConnections()) {
                connection.close("Shutting down.");
            }
            System.out.println("Shutting down server...");
            server.close();
        }
    }

    private void addService(HostedService service) {
        server.getServices().addService(service);
    }

    private void registerClasses() {
        Serializer.registerClass(PositionComponent.class, new FieldSerializer());
        Serializer.registerClass(NodeComponent.class, new FieldSerializer());
    }
}
