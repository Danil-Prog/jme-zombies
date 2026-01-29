package org.jme.zombies.game.controls;

import com.jme3.recast4j.ai.NavMeshAgent;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.jme.zombies.game.utils.SceneFactory;

import java.util.Objects;

import static org.jme.zombies.game.utils.SceneFactory.getComponent;

public class AgentAnimationControl extends AbstractControl {

    private NavMeshAgent agent;
    private AnimatorControl animator;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            this.agent = SceneFactory.getComponent(spatial, NavMeshAgent.class);
            Objects.requireNonNull(agent, "Agent not found: " + spatial);

            this.animator = getComponent(spatial, AnimatorControl.class);
            Objects.requireNonNull(animator, "Animator not found: " + spatial);
        }
    }

    @Override
    public void controlUpdate(float tpf) {
        if (agent.remainingDistance() < agent.getStoppingDistance() && !agent.pathPending()) {
            animator.setAnimation("Armature|Attack");
            animator.setSpeed(1);
        } else {
            animator.setAnimation("Armature|Walk2");
            animator.setSpeed(1.8f);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
