package projects.dmad.nodes.messages;

import java.util.ArrayList;
import java.util.List;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class DFSMessage extends Message {
	public boolean isChild;
	public int idChannel;
	public int back;
	public ArrayList<Integer> path;
	public Node sender;

	public DFSMessage(Node sender, int id, int back, ArrayList<Integer> path, boolean isChild) {
		this.sender = sender;
		this.idChannel = id;
		this.back = back;
		this.path = path;
		this.isChild = isChild;
	}

	@Override
	public Message clone() {
		return new DFSMessage(sender, idChannel, back, path, isChild);
	}
	
	@Override
	public String toString() {
		return sender.ID + " " + idChannel + " " + isChild;
	}

}
