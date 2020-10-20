package projects.dmad.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class TokenMessage extends Message {
	public Node sender;

	public TokenMessage(Node s) {
		sender = s;
	}

	@Override
	public Message clone() {
		return new TokenMessage(sender);
	}

}
