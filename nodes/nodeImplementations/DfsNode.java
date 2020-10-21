package projects.dmad.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import projects.dmad.nodes.messages.DFSMessage;
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
	public int alphaVoisins[], path[];
	public int pathvoisins[][];
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

		// Initialisation des attributs
		int nbVoisin = nbVoisin();
		int nbNode = Tools.getNodeList().size();

		Random r = new Random();

		this.alphaVoisins = new int[nbVoisin];

		this.pathvoisins = new int[nbVoisin][nbNode];

		if (this.ID == 1) {
			this.pere = -1;
			this.path = new int[1];
			this.path[0] = -1;
		} else {
			this.pere = 0;
			int size = r.nextInt(nbVoisin);
			this.path = new int[size];
			for (int i = 0; i < path.length; i++) {
				path[i] = r.nextInt(nbVoisin + 2) - 1;
			}
		}

	}

	// fini() detecte la terminaison locale du parcours
	boolean fini() {
		return true;
	}

	void envoie(boolean isChild) {
		Iterator<Edge> it = this.outgoingConnections.iterator();
		while (it.hasNext()) {
			Edge e = it.next();
			this.send(new DFSMessage(this, getIndex(e.endNode), this.path, isChild), e.endNode);
		}
	}

	/* Retourne vrai si le path p1 et plus petit lexicographiquement que p2 */
	boolean isShorterPath(int[] p1, int[] p2) {
		int i = 0, j = 0;
		int lp1 = p1.length, lp2 = p2.length;
		while (i < lp1 && j < lp2) {
			if (p1[i] < p2[j])
				return true;
			i++;
			j++;
		}
		if (i == lp1)
			return p1[i - 1] < p2[j];
		return false;
	}

	// vous utiliserez la fonction ci-dessous pour changer la couleur
	// d'un noeud lors de la première visite de ce noeud
	public void inverse() {
		if (this.couleur == Color.blue)
			this.couleur = Color.red;
		else
			this.couleur = Color.blue;
	}

	int[] computePath(int[] oldPath, int e) {
		int newPath[] = oldPath;

		// Si le path est déjà de longueur N on supprime le premier élément
		if (oldPath.length == Tools.getNodeList().size()) {
			newPath = Arrays.copyOfRange(oldPath, 1, oldPath.length - 1);
		}

		// On ajoute le nouvel élément au Path
		newPath = Arrays.copyOf(newPath, newPath.length + 1);
		newPath[newPath.length - 1] = e;

		return newPath;
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

			if (m instanceof DFSMessage) {
				DFSMessage msg = (DFSMessage) m;
				if (pere == 0) {
					pere = msg.sender.ID;
				}
				int canalSender = getIndex(msg.sender);

				// Maj attributs
				alphaVoisins[canalSender] = msg.idChannel;
				pathvoisins[canalSender] = msg.path;

				// Maj Path
				int receivedpath[] = computePath(msg.path, canalSender);
				if (!isShorterPath(path, receivedpath)) {
					path = receivedpath;
					pere = msg.sender.ID;
				}
			}

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
