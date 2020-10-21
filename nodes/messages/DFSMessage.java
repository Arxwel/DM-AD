package projects.dmad.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class DFSMessage extends Message {

	public int idChannel;
	public int path[]; 
	public Node sender;
	
	public DFSMessage(Node sender, int id, int path[]) {
		this.sender = sender;
	}
	
	@Override
	public Message clone() {
		return null;
	}

}
