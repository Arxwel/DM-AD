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

	// Id du père
	public int pere;

	// Tableau du alpha des voisins
	public int alphaVoisins[];

	// Le chemin courant du noeud
	public ArrayList<Integer> path;

	// Chemins des voisins
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
		int            j    = 1;

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

		if (i < 0) {
			return this;
		}

		Iterator<Edge> iter = this.outgoingConnections.iterator();

		for (int j = 1; j < i; j++)
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
		int    nbVoisin = nbVoisin() + 1;
		Random r        = new Random();

		this.alphaVoisins = new int[nbVoisin];
		this.pathvoisins  = new ArrayList<ArrayList<Integer>>();
		this.path         = new ArrayList<Integer>();

		// Initialise les chemins des voisins
		for (int i = 0; i < nbVoisin; i++) {
			this.pathvoisins.add(new ArrayList<Integer>());
			this.alphaVoisins[i] = 0;
		}

		if (this.ID == 1) { // Cas du noeud racine
			this.pere    = -1;
			this.couleur = Color.yellow;
			this.path.add(-1);
		} else { // Cas des noeuds non racine
			this.pere = 0;
			int size = r.nextInt(nbVoisin); // Au moins de taille 1

			for (int i = 0; i < size; i++) {
				path.add(r.nextInt(nbVoisin + 2) - 1);
			}

		}

		// Démarrage du timer d'envoi
		(new SendTimer()).startRelative(15, this);

	}

	public boolean allAlphaIsKnown() {

		for (int i = 1; i < this.alphaVoisins.length; i++) {

			if (this.alphaVoisins[i] == 0) {
				return false;
			}

		}

		return true;
	}

	public int indexOfMinAlpha() {
		int index = 1;
		int tmp   = this.alphaVoisins[index];

		for (int i = index; i < this.alphaVoisins.length; i++) {

			if (this.alphaVoisins[i] < tmp) {
				tmp   = this.alphaVoisins[i];
				index = i;
			}

		}

		return index;
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
			Edge    e       = it.next();
			int     index   = this.getIndex(e.endNode);
			boolean isChild = false;

			if (this.allAlphaIsKnown()) {
				ArrayList<Integer> tmp = this.computePath(this.pathvoisins.get(index), this.alphaVoisins[index]);
				int min = this.getIndexMinPath();
				isChild = this.comparaisonPath(tmp, this.pathvoisins.get(min)) <= 0;
//				isChild = (index == this.indexOfMinAlpha());
			}

			this.send(new DFSMessage(this, index, this.path, isChild), e.endNode);
		}

		(new SendTimer()).startRelative(15, this);

	}

	/**
	 * Compare lexicographiquement path2 à receivedpath
	 * 
	 * @param path1
	 * @param path2
	 * @return 0 si égalité, -1 si path1 < path2, 1 si path1 > pat2
	 */
	public int comparaisonPath(ArrayList<Integer> path1, ArrayList<Integer> path2) {
		int i   = 0, j = 0;
		int lp1 = path1.size(), lp2 = path2.size();

		while (i < lp1 && j < lp2) {
			if (path1.get(i) < path2.get(j))
				return -1;
			else if (path1.get(i) > path2.get(j)) {
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

		if (this.couleur != Color.yellow) {
			if (this.couleur == Color.blue)
				this.couleur = Color.red;
			else
				this.couleur = Color.blue;
		}

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
				this.inverse();
				int                canalSender = getIndex(msg.sender);
				ArrayList<Integer> newPath     = this.computePath(msg.path, alphaVoisins[canalSender]);

				alphaVoisins[canalSender] = msg.idChannel;
				path                      = (ArrayList<Integer>) newPath.clone();
				pathvoisins.set(canalSender, newPath);

				if (msg.isChild && this.pere == 0) {
					this.pere    = msg.sender.ID;
					this.couleur = Color.yellow;
//					ArrayList<Integer> tmp = (ArrayList<Integer>) this.path.clone();
//					tmp.remove(tmp.size() - 1);
//
//					if (this.comparaisonPath(tmp, msg.path) == 0) {
//						this.pere    = msg.sender.ID;
//						this.couleur = Color.yellow;
//						System.out.println(ID + ": " + msg.sender.ID);
//					}

				}

			}

		}

	}

	public int getIndexMinPath() {
		int                index = 1;
		ArrayList<Integer> tmp   = this.pathvoisins.get(index);

		for (int i = index; i < this.pathvoisins.size(); i++) {

			if (this.comparaisonPath(this.pathvoisins.get(i), tmp) == -1) {
				tmp   = this.pathvoisins.get(i);
				index = i;
			}

		}

		return index;
	}

	public String displayPath(ArrayList<Integer> p) {
		String   r   = "";
		Iterator ite = p.iterator();

		while (ite.hasNext()) {
			r += ite.next() + ", ";
		}

		return r;
	}

	// les fonctions ci-dessous ne doivent pas être modifiées

	public void neighborhoodChange() {
		this.start();
	}

	public void postStep() {}

	public void checkRequirements() throws WrongConfigurationException {}

	// affichage du noeud

	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		this.setColor(this.couleur);
		String text = "" + this.ID;
		super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, Color.black);
	}

}
