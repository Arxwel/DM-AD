package projects.dmad.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class InitConnectionMsg extends Message {

	public int asw;
	public Node sender;
	
	public InitConnectionMsg(Node sender, int asw) {
		this.sender = sender;
		this.asw = asw;
	}
	
	public InitConnectionMsg(Node sender) {
		this(sender, -1);
	}
	
	@Override
	public Message clone() {
		return new InitConnectionMsg(sender, asw);
	}
}
