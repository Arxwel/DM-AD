package projects.dmad.nodes.timers;

import java.util.ArrayList;
import java.util.Arrays;

import projects.dmad.nodes.nodeImplementations.DfsNode;
import sinalgo.nodes.timers.Timer;

public class SendTimer extends Timer {

	public void fire() {
		DfsNode n=  (DfsNode) this.node;
		n.envoie();
	}
	

}
