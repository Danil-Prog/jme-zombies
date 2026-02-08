package org.jme.zombies.rpc;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityId;

/**
 * Remote function calls from client to server
 */
public interface ICommand {

    /**
     * Player movement.
     */
    void onMovement(EntityId entity, Vector3f position);

    /**
     * Entity fired a shot.
     */
    void onShoot(Vector3f location);

}
