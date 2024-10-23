import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

// Clasa pentru un arc
public class Arc
{
	private Point start;
	private Point end;
	private boolean isOriented;

	public Arc(Point start, Point end,boolean isOriented)
	{
		this.start = start;
		this.end = end;
		this.isOriented=isOriented;
	}

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}

	public void setStart(int x, int y){
		start.x=x;
		start.y=y;
	}

	public void setEnd(int x, int y){
		end.x=x;
		end.y=y;
	}

	public void drawArc(Graphics g)
	{
		if (start != null)
		{
            g.setColor(Color.RED);
            g.drawLine(start.x, start.y, end.x, end.y);
			if(isOriented)
				drawArrowHead(g,start,end);
		}
	}

	private void drawArrowHead(Graphics g, Point start, Point end){
		int node_diam=30;
		g.setColor(Color.RED);
		g.drawLine(start.x, start.y, end.x, end.y);
		double v1=Math.sqrt((Math.pow(start.x-end.x,2))+Math.pow(start.y-end.y,2));
		double u1=(start.x-end.x)/v1;
		double u2=(start.y-end.y)/v1;
		int arrowSize=30;
		double angle = Math.atan2(end.y - start.y, end.x-start.x);
		int x1 = (int) (end.x - arrowSize * Math.cos(angle - Math.PI / 6));
		int y1 = (int) (end.y - arrowSize * Math.sin(angle - Math.PI / 6));
		int x2 = (int) (end.x - arrowSize * Math.cos(angle + Math.PI / 6));
		int y2 = (int) (end.y - arrowSize * Math.sin(angle +Math.PI/6));
		g.drawLine((int)(end.x+ node_diam/2*u1), (int)(end.y+ node_diam/2*u2), x1, y1);
		g.drawLine((int)(end.x+ node_diam/2*u1), (int)(end.y+ node_diam/2*u2), x2, y2);
		g.drawLine(x1,y1,x2,y2);

/*
		int[] xPoints = {end.x, x1, x2};
		int[] yPoints = {end.y, y1, y2};

		g.fillPolygon(new Polygon(xPoints, yPoints, 3));
*/
    }
}
