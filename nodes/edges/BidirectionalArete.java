package projects.dmad.nodes.edges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import projects.dmad.nodes.nodeImplementations.DfsNode;
import sinalgo.gui.helper.Arrow;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Position;
import sinalgo.nodes.edges.BidirectionalEdge;

public class BidirectionalArete extends BidirectionalEdge {

	public void draw(Graphics g, PositionTransformation pt) {
		Position p1 = startNode.getPosition();
		pt.translateToGUIPosition(p1);
		int      fromX = pt.guiX, fromY = pt.guiY; // temporarily store
		Position p2    = endNode.getPosition();
		pt.translateToGUIPosition(p2);
		DfsNode deb = (DfsNode) this.startNode;
		DfsNode fin = (DfsNode) this.endNode;

		if ( (deb.pere == fin.ID || fin.pere == deb.ID)) {
			Graphics2D g2     = (Graphics2D) g;
			Stroke     stroke = new BasicStroke(5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f);
			g2.setStroke(stroke);
			Color color = deb.height == deb.back ? Color.orange : Color.blue;
			Arrow.drawArrow(fromX, fromY, pt.guiX, pt.guiY, g2, pt, color);
		} else {
			Graphics2D g2     = (Graphics2D) g;
			Stroke     stroke = new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f);
			g2.setStroke(stroke);
			Arrow.drawArrow(fromX, fromY, pt.guiX, pt.guiY, g2, pt, Color.GRAY);
		}

	}

}
