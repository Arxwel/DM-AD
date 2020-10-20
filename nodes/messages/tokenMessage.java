package projects.dmad.nodes.messages;


import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class tokenMessage extends Message {
public Node sender;

public tokenMessage(Node s){
	sender=s;
}

@Override
public Message clone() {
	// TODO Auto-generated method stub
	return new tokenMessage(sender);
}

	
}
