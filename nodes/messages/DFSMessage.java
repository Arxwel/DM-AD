package projects.dmad.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class DFSMessage extends Message {
	public boolean isChild;
	public int idChannel;
	public int path[];
	public Node sender;

	public DFSMessage(Node sender, int id, int path[], boolean isChild) {
		this.sender = sender;
		this.idChannel = id;
		this.path = path;
		this.isChild = isChild;
	}

	@Override
	public Message clone() {
		return new DFSMessage(sender, idChannel, path, isChild);
	}

}
