import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import static java.lang.Math.sqrt;
// Cele trei biblioteci sunt pentru scrierea în fișier
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MyPanel extends JPanel {
	private int nodeNr = 1;
	private int node_diam = 30;
	private Vector<Node> listaNoduri;
	private Vector<Arc> listaArce;
	Point pointStart = null;
	Point pointEnd = null;
	boolean isDragging = false;
	boolean isOriented = false;
	private Node selectedNode = null; // Nodul selectat pentru repoziționare
	private Point offset = null; // Diferența dintre poziția click-ului și centrul nodului

	private JCheckBox orientedCheckBox;

	public MyPanel() {
		listaNoduri = new Vector<Node>();
		listaArce = new Vector<Arc>();
		// borderul panel-ului
		setBorder(BorderFactory.createLineBorder(Color.black));

		//CheckBox pentru alegerea tipului de graf
		orientedCheckBox=new JCheckBox("Graf Orientat");
		orientedCheckBox.setBounds(10,10,120,30);
		orientedCheckBox.addActionListener(e -> {
			isOriented = orientedCheckBox.isSelected();
			listaNoduri.clear();
			listaArce.clear();
			nodeNr=1;
			repaint();
		});
		this.add(orientedCheckBox);

		addMouseListener(new MouseAdapter() {
			// evenimentul care se produce la apasarea mouse-ului
			public void mousePressed(MouseEvent e) {
				// Click stânga pentru desenarea arcelor
				if (e.getButton() == MouseEvent.BUTTON1) {
					Node clickNode = getNodeAtPosition(e.getX(), e.getY());
					if (clickNode != null) {
						pointStart = e.getPoint();
					}
				}

				// Click dreapta pentru mutarea nodurilor
				if (e.getButton() == MouseEvent.BUTTON3) {
					selectedNode = getNodeAtPosition(e.getX(), e.getY());
					if (selectedNode != null) {
						offset = new Point(e.getX() - selectedNode.getCoordX(), e.getY() - selectedNode.getCoordY());
					}
				}
			}


			// evenimentul care se produce la eliberarea mouse-ului
			public void mouseReleased(MouseEvent e) {
				if (selectedNode != null) {
					selectedNode = null;
				} else if (!isDragging) {
					addNode(e.getX(), e.getY());
				} else if (pointStart != null) {
					Node startNode = getNodeAtPosition(pointStart.x, pointStart.y);
					Node endNode = getNodeAtPosition(e.getX(), e.getY());

					if (startNode != null && endNode != null && startNode != endNode) {
						Arc arc = new Arc(new Point(startNode.getCoordX() + node_diam / 2, startNode.getCoordY() + node_diam / 2),
								new Point(endNode.getCoordX() + node_diam / 2, endNode.getCoordY() + node_diam / 2), isOriented);
						listaArce.add(arc);

						saveAdjacencyMatrix("matrice_adiacenta.txt");
					}
				}
				pointStart = null;
				pointEnd = null;
				isDragging = false;
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			// evenimentul care se produce la drag&drop pe mouse
			public void mouseDragged(MouseEvent e) {
				if (selectedNode != null) {
					selectedNode.setCoordX(e.getX() - offset.x);
					selectedNode.setCoordY(e.getY() - offset.y);


					// Repoziționarea arcelor aferente nodului mutat
					for (Arc arc : listaArce) {
						if(sqrt((arc.getStart().x-selectedNode.getCoordX())*(arc.getStart().x-selectedNode.getCoordX())+(arc.getStart().y-selectedNode.getCoordY())
								*(arc.getStart().y-selectedNode.getCoordX()))<node_diam/2){
							arc.setStart(selectedNode.getCoordX(),selectedNode.getCoordY());
						}

						if(sqrt((arc.getEnd().x-selectedNode.getCoordX())*(arc.getEnd().x-selectedNode.getCoordX())+(arc.getEnd().y-selectedNode.getCoordY())
								*(arc.getEnd().y-selectedNode.getCoordX()))<node_diam/2) {
							arc.setEnd(selectedNode.getCoordX(), selectedNode.getCoordY());
						}
					}

					//saveAdjacencyMatrix("matrice_adiacenta.txt");
					repaint();

				} else if (pointStart != null) {
					pointEnd = e.getPoint();
					isDragging = true;
					repaint();
				}
			}
		});
	}

	// Metoda care verifică dacă două noduri se suprapun pe baza coordonatelor lor
	private boolean checkOverlap(int x, int y) {
		for (Node node : listaNoduri) {
			double distance = sqrt(Math.pow(node.getCoordX() - x, 2) + Math.pow(node.getCoordY() - y, 2));
			if (distance < 2 * node_diam) {
				return true; // Nodurile se suprapun
			}
		}
		return false;
	}

	// Metoda pentru a găsi un nod la o poziție specifică
	private Node getNodeAtPosition(int x, int y) {
		for (Node node : listaNoduri) {
			double distance = sqrt(Math.pow(node.getCoordX() + node_diam / 2 - x, 2) + Math.pow(node.getCoordY() + node_diam / 2 - y, 2));
			if (distance < node_diam / 2) {
				return node; // Returnează nodul pe care se află punctul
			}
		}
		return null; // Dacă nu se află pe niciun nod
	}

	// Metoda de adăugare a nodului cu verificarea suprapunerii
	private void addNode(int x, int y) {
		if(checkOverlap(x,y)) {
			return;
		}
//		while (checkOverlap(x, y)) {
//			x += node_diam; // Deplasare pe axa X
//			y += node_diam; // Deplasare pe axa Y
//			// Verificare pentru a nu ieși din panou
//			if (x > getWidth() - node_diam) {
//				x = node_diam;
//			}
//			if (y > getHeight() - node_diam) {
//				y = node_diam;
//			}
//		}

		Node node = new Node(x, y, nodeNr);
		listaNoduri.add(node);
		nodeNr++;
		repaint();

		// Salvează matricea de adiacență după adăugarea nodului
		saveAdjacencyMatrix("matrice_adiacenta.txt");
	}

	// se execută atunci când apelăm repaint()
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // apelez metoda paintComponent din clasa de bază
		g.drawString("This is my Graph!", 10, 20);
		// desenează arcele existente în listă
		for (Arc a : listaArce) {
			a.drawArc(g);
		}
		// desenează arcul curent; cel care e în curs de desenare
		if (pointStart != null) {
			g.setColor(Color.RED);
			g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
		}
		// desenează lista de noduri
		for (int i = 0; i < listaNoduri.size(); i++) {
			listaNoduri.elementAt(i).drawNode(g, node_diam);
		}
	}

	// Metoda pentru a obține matricea de adiacență
	public int[][] getAdjacencyMatrix() {
		int nrNodes = listaNoduri.size(); // Numărul de noduri
		int[][] matrix = new int[nrNodes][nrNodes];

		for (Arc arc : listaArce) {
			// Obține coordonatele start și end din obiectul arc
			Point startPoint = arc.getStart();
			Point endPoint = arc.getEnd();

			// Obține nodurile corespunzătoare pe baza coordonatelor
			Node startNode = getNodeAtPosition(startPoint.x, startPoint.y);
			Node endNode = getNodeAtPosition(endPoint.x, endPoint.y);

			if (startNode != null && endNode != null) {
				int startIndex = listaNoduri.indexOf(startNode);
				int endIndex = listaNoduri.indexOf(endNode);

				matrix[startIndex][endIndex] = 1; // Adaugă un arc în matrice
				if(!isOriented)
					matrix[endIndex][startIndex] = 1; // Dacă graful este neorientat, adaugă și invers
			}
		}
		return matrix;
	}

	// Metoda pentru a salva matricea de adiacență într-un fișier
	public void saveAdjacencyMatrix(String filename) {
		int[][] matrix = getAdjacencyMatrix();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			writer.write(String.valueOf(listaNoduri.size())); // Scrie numărul de noduri pe prima linie
			writer.newLine(); // Trecere la urmatoarea linie

			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					writer.write(String.valueOf(matrix[i][j]) + " ");
				}
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace(); // Gestionează excepțiile
		}
	}
}
