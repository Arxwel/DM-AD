package projects.dmad.nodes.timers;

import projects.dmad.nodes.nodeImplementations.DfsNode;
import sinalgo.nodes.timers.Timer;

public class InitTimer extends Timer {

	public void fire() {
		DfsNode n=  (DfsNode) this.node;
		n.start();
	}
	

}
