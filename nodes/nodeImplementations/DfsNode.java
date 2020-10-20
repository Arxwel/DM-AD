package projects.dmad.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import projects.dmad.nodes.messages.InitConnectionMsg;
import projects.dmad.nodes.messages.TokenMessage;
import projects.dmad.nodes.timers.InitTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

public class DfsNode extends Node {

	public int pere;
	public HashMap<Integer, Node> alphaVoisins;
	public boolean visite[];
	public Color couleur = Color.blue;

	public void preStep() {
	}

	// ATTENTION lorsque init est appelé les liens de
	// communications n'existent pas il faut attendre une unité de
	// temps, avant que les connections soient réalisées nous
	// utilisons donc un timer

	public void init() {
		(new InitTimer()).startRelative(1, this);
	}

	// getIndex retourne le numéro de canal correspondant au
	// voisin n

	public int getIndex(Node n) {
		Iterator<Edge> iter = this.outgoingConnections.iterator();
		int j = 1;

		while (true) {
			if (iter.next().endNode.ID == n.ID)
				return j;
			j++;
		}

	}

	// fonction inverse de getIndex : retourne le voisin
	// correspondant au numéro de canal i

	public Node getVoisin(int i) {
		Iterator<Edge> iter = this.outgoingConnections.iterator();

		for (int j = 1; j < i; j++)
			iter.next();

		return iter.next().endNode;
	}

	// degré du processus

	public int nbVoisin() {
		return this.outgoingConnections.size();
	}

	// Lorsque le timer précédent expire, la fonction start est
	// appelée elle correspond ainsi à l'initialisation REELLE du
	// processus

	public void start() {

		// ATTENTION on alloue nbVoisin()+1 cases indicées de
		// 0 à nbVoisin() La case 0 doit être ignorée dans la
		// suite l'idée est de suivre la même numérotation de
		// canaux que l'algorithme : de 1 à nbVoisin()

		this.alphaVoisins = new HashMap<Integer,Node>();
		this.broadcast(new InitConnectionMsg(this));

//		this.visite = new boolean[this.nbVoisin() + 1];
//
//		for (int i = 0; i <= this.nbVoisin(); i++)
//			this.visite[i] = false;
//
//		if (this.ID == 1) {
//			this.visite[1] = true;
//			this.pere = -1; // -1 correspond à la valeur TOP dans l'algo
//			this.send(new TokenMessage(this), this.getVoisin(1));
//			this.inverse();
//		} else {
//			this.pere = 0; // 0 correspond à NIL dans l'algo
//		}

	}

	// fini() detecte la terminaison locale du parcours
	boolean fini() {
		int i = 1;

		while (i <= this.nbVoisin()) {
			if (!this.visite[i])
				return false;
			i++;
		}

		return true;
	}

	// nextVisite retourne le prochain canal à visiter
	int nextVisite() {

		for (int i = 1; i <= this.nbVoisin(); i++) {

			if (this.pere != i && !this.visite[i]) {
				return i;
			}

		}

		return this.pere;
	}

	// vous utiliserez la fonction ci-dessous pour changer la couleur
	// d'un noeud lors de la première visite de ce noeud
	public void inverse() {
		if (this.couleur == Color.blue)
			this.couleur = Color.red;
		else
			this.couleur = Color.blue;
	}

	// Cette fonction gère la réception de message Elle est
	// appelée régulièrement même si aucun message n'a été reçu
	// Elle doit être complétée à l'aide des fonctions précédentes
	// Pour la décision, vous utiliserez la fonction
	// Tools.stopSimulation() qui a pour effet de stopper la
	// simulation
	public void handleMessages(Inbox inbox) {

		// Test si il y a des messages
		while (inbox.hasNext()) {
			Message m = inbox.next();

			if (m instanceof InitConnectionMsg) {
				this.handleInitConnectionMsg((InitConnectionMsg) m);
			}

//			if (m instanceof TokenMessage) { // Si le processus a reçu le jeton
//				TokenMessage msg = (TokenMessage) m;
//				int canal = this.getIndex(msg.sender);
//
//				if (this.pere == 0) {
//					this.pere = canal;
//					this.inverse();
//				}
//
//				if (this.fini()) {
//					// Infinite code
//					for (int i = 0; i <= this.nbVoisin(); i++)
//						this.visite[i] = false;
//					this.visite[1] = true;
//					this.send(new TokenMessage(this), this.getVoisin(1));
//					this.inverse();
//				} else {
//					int suivant = this.nextVisite();
//
//					if (suivant != this.pere) {
//						if (this.pere != canal && !this.visite[canal])
//							suivant = canal;
//						this.send(new TokenMessage(this), this.getVoisin(suivant));
//						this.visite[suivant] = true;
//					} else {
//						this.send(new TokenMessage(this), this.getVoisin(this.pere));
//						this.visite[this.pere] = true;
//
//						// Infinite code
//						for (int i = 0; i <= this.nbVoisin(); i++)
//							this.visite[i] = false;
//						this.pere = 0;
//					}
//
//				}
//
//			}

		}

	}

	public void handleInitConnectionMsg(InitConnectionMsg msg) {
		if (msg.asw == -1) {
			this.send(new InitConnectionMsg(this, this.getIndex(msg.sender)), msg.sender);
		} else {
			this.alphaVoisins.put(msg.asw, msg.sender);
		}
	}

	// les fonctions ci-dessous ne doivent pas être modifiées

	public void neighborhoodChange() {
	}

	public void postStep() {
	}

	public void checkRequirements() throws WrongConfigurationException {
	}

	// affichage du noeud

	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		this.setColor(this.couleur);
		String text = "" + this.ID;
		super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
	}

}
