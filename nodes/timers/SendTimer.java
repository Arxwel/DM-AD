package projects.dmad.nodes.timers;

import java.util.Arrays;

import projects.dmad.nodes.nodeImplementations.DfsNode;
import sinalgo.nodes.timers.Timer;

public class SendTimer extends Timer {

	public void fire() {
		DfsNode n=  (DfsNode) this.node;
		int pathpere[] = Arrays.copyOfRange(n.path, 0, n.path.length-2);
		for (int i = 0; i < n.pathvoisins.length; i++) {
			int pathv[] = n.pathvoisins[i];
			
		}
		//n.envoie();
	}
	

}
