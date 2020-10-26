package projects.dmad.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;

import projects.dmad.nodes.messages.DFSMessage;
import projects.dmad.nodes.timers.InitTimer;
import projects.dmad.nodes.timers.SendTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.tools.Tools;

public class DfsNode extends Node {

	public int                           pere;
	public int                           alphaVoisins[];
	public ArrayList<Integer>            path;
	public ArrayList<ArrayList<Integer>> pathvoisins;
	public Color                         couleur = Color.blue;

	public void preStep() {}

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
		int            j    = 1;

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
		int nbNode   = Tools.getNodeList().size();

		Random r = new Random();

		this.alphaVoisins = new int[nbVoisin+1];

		this.pathvoisins = new ArrayList<ArrayList<Integer>>();
		this.path        = new ArrayList();
		
		for(int i = 0 ; i <= nbVoisin; i++) {
			this.pathvoisins.add(new ArrayList<Integer>());
		}

		if (this.ID == 1) {
			this.pere = -1;
			this.path.add(-1);
		} else {
			this.pere = 0;
			int size = r.nextInt(nbVoisin);

			for (int i = 0; i < size; i++) {
				path.add(r.nextInt(nbVoisin + 2) - 1);
			}

		}

		(new SendTimer()).startRelative(15, this);

	}

	// fini() detecte la terminaison locale du parcours
	public boolean fini() {
		return true;
	}

	public void envoie() {
		Iterator<Edge>     it       = this.outgoingConnections.iterator();
		ArrayList<Integer> pathpere = (ArrayList<Integer>) this.path.clone();
		if(pathpere.size() > 1) {
			pathpere.remove(pathpere.size() - 1);
		}

		while (it.hasNext()) {
			Edge e     = it.next();
			int  index = this.getIndex(e.endNode);

			if (this.ID == 1) {
				this.send(new DFSMessage(this, index, this.path, false), e.endNode);
			} else {
				ArrayList<Integer> currentPath;

				if (this.pathvoisins.size() > index) {
					currentPath = this.pathvoisins.get(index);
				} else {
					currentPath = new ArrayList<Integer>();
				}

				this.send(new DFSMessage(this, index, this.path, this.comparisonPath(pathpere, currentPath) == 0),
						e.endNode);
			}

		}

		System.out.println("send");

	}

	/* Retourne vrai si le path p1 et plus petit lexicographiquement que p2 */
	public int comparisonPath(ArrayList<Integer> path2, ArrayList<Integer> receivedpath) {
		int i   = 0, j = 0;
		int lp1 = path2.size(), lp2 = receivedpath.size();

		while (i < lp1 && j < lp2) {
			if (path2.get(i) < receivedpath.get(j))
				return -1;
			else if (path2.get(i) > receivedpath.get(j)) {
				return 1;
			}
			i++;
			j++;
		}

		if (lp1 == lp2)
			return 0;
		else if (lp1 < lp2) {
			return -1;
		}
		return 1;
	}

	// vous utiliserez la fonction ci-dessous pour changer la couleur
	// d'un noeud lors de la première visite de ce noeud
	public void inverse() {
		if (this.couleur == Color.blue)
			this.couleur = Color.red;
		else
			this.couleur = Color.blue;
	}

	public ArrayList<Integer> computePath(ArrayList<Integer> oldPath, int e) {
		ArrayList<Integer> newPath = (ArrayList<Integer>) oldPath.clone();

		// Si le path est déjà de longueur N on supprime le premier élément
		if (newPath.size() == Tools.getNodeList().size()) {
			newPath.remove(0);
		}

		// On ajoute le nouvel élément au Path
		newPath.add(e);

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
				pathvoisins.add(canalSender, (ArrayList<Integer>) msg.path);

				// Maj Path
				ArrayList<Integer> receivedpath = computePath(msg.path, canalSender);

				if (comparisonPath(path, receivedpath) == 1) {
					path = receivedpath;
					pere = msg.sender.ID;
					this.inverse();
				}

			}

		}

	}

	// les fonctions ci-dessous ne doivent pas être modifiées

	public void neighborhoodChange() {}

	public void postStep() {}

	public void checkRequirements() throws WrongConfigurationException {}

	// affichage du noeud

	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		this.setColor(this.couleur);
		String text = "" + this.ID;
		super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
	}

}
