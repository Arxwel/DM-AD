package projects.dmad.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

	/**
	 * Id du père
	 */
	public int                           pere;
	/**
	 * Tableau du alpha des voisins
	 */
	public int                           alphaVoisins[];
	/**
	 * Le chemin courant du noeud
	 */
	public ArrayList<Integer>            path;
	/**
	 * Chemins des voisins
	 */
	public ArrayList<ArrayList<Integer>> pathvoisins;
	public Color                         couleur = Color.blue;

	public void preStep() {}

	/**
	 * Fonction d'initiation. Effet de bord: Lance le timer d'initiation
	 */
	public void init() {
		(new InitTimer()).startRelative(1, this);
	}

	/**
	 * Permet d'avoir le numéro de canal d'un noeud voisin
	 * 
	 * @param n Noeud voisin
	 * @return le numéro de canal
	 */
	public int getIndex(Node n) {
		Iterator<Edge> iter = this.outgoingConnections.iterator();
		int            j    = 0;

		while (true) {
			if (iter.next().endNode.ID == n.ID)
				return j;
			j++;
		}

	}

	/**
	 * Permet d'obtenir le noeud voisin en donnant le numéro de canal
	 * 
	 * @param i Le numéro de canal
	 * @return Le noeud voisin
	 */
	public Node getVoisin(int i) {
		if (i >= this.nbVoisin() || i < 0)
			return this;
		Iterator<Edge> iter = this.outgoingConnections.iterator();
		for (int j = 0; j < i; j++)
			iter.next();
		return iter.next().endNode;
	}

	/**
	 * Retourne le nombre de voisin du noeud
	 * 
	 * @return
	 */
	public int nbVoisin() {
		return this.outgoingConnections.size();
	}

	/**
	 * Méthode d'initialisation du noeud. Est appelée losque le timer
	 * d'initialisation expire.
	 */
	public void start() {

		// Initialisation des attributs
		int nbVoisin = nbVoisin();
		int nbNode   = Tools.getNodeList().size();

		Random r = new Random();

		this.alphaVoisins = new int[nbVoisin];

		this.pathvoisins = new ArrayList<ArrayList<Integer>>();
		this.path        = new ArrayList<Integer>();

		// Initialise les chemins des voisins
		for (int i = 0; i < nbVoisin; i++) {
			this.pathvoisins.add(new ArrayList<Integer>());
		}

		if (this.ID == 1) { // Cas du noeud racine
			this.pere = -1;
			this.path.add(-1);
		} else { // Cas des noeuds non racine
			this.pere = 0;
			int size = r.nextInt(nbVoisin);

			for (int i = 0; i < size; i++) {
				path.add(r.nextInt(nbVoisin + 2) - 1);
			}

		}

		// Démarrage du timer d'envoi
		(new SendTimer()).startRelative(15, this);

	}

	/**
	 * Méthode permettant l'envoi de l'état du noeud à ses voisins.
	 */
	public void envoie() {
		Iterator<Edge>     it       = this.outgoingConnections.iterator();
		ArrayList<Integer> pathpere = (ArrayList<Integer>) this.path.clone();

		if (pathpere.size() > 1) {
			pathpere.remove(pathpere.size() - 1);
		}

		while (it.hasNext()) {
			Edge e     = it.next();
			int  index = this.getIndex(e.endNode);

			if (this.ID == 1) { // Cas noeud racine
				this.send(new DFSMessage(this, index, this.path, false), e.endNode);
			} else {
				ArrayList<Integer> currentPath;
				currentPath = this.pathvoisins.get(index);
				this.send(new DFSMessage(this, index, this.path, this.comparisonPath(pathpere, currentPath) == 0),
						e.endNode);
			}

		}

		System.out.println("send");
		(new SendTimer()).startRelative(15, this);

	}

	/**
	 * Compare lexicographiquement path2 à receivedpath
	 * 
	 * @param path2
	 * @param receivedpath
	 * @return 0 si égalité, -1 si path2 < receivedpath, 1 i path2 > receivedpath
	 */
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

	/**
	 * Permet d'ajouter une étape au chemin. Effet de bord: Si le nouveu chemin est
	 * de taille n+1 (n le nombre de noeuds dans le graphe) la méthode va garder les
	 * n derniers éléments.
	 * 
	 * @param oldPath Le chemin à modifier
	 * @param e       L'étape à ajouter au chemine
	 * @return Le nouveux chemin
	 */
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
