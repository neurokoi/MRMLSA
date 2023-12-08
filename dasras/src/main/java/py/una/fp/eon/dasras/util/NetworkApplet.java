package py.una.fp.eon.dasras.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;

import py.una.fp.eon.core.Network;

public class NetworkApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
	private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);
	private JGraphModelAdapter<String, DefaultWeightedEdge> m_jgAdapter;

	@Override
	public void init() {
		//"resources/"
		Network network = new Network("/src/main/resources/","network");
		this.m_jgAdapter = new JGraphModelAdapter<String, DefaultWeightedEdge>(network.getGraph());
		JGraph jgraph = new JGraph(m_jgAdapter);

		adjustDisplaySettings(jgraph);
		getContentPane().add(jgraph);
		resize(DEFAULT_SIZE);

		// position vertices nicely within JGraph component
		int initx=50;
		int inity= 250;
		int pos=1;
		for (String v :network.getGraph().vertexSet()){
			positionVertexAt(v, initx, inity);
			pos++;
			if (pos%2 == 0){
				initx=initx + 100;
				inity=inity - 150;
			}else{
				initx=initx+ 100;
				inity=inity+ 150;
			}
		}

		// that's all there is to it!...
	}

	private void adjustDisplaySettings(JGraph jg) {
		jg.setPreferredSize(DEFAULT_SIZE);

		Color c = DEFAULT_BG_COLOR;
		String colorStr = null;

		try {
			colorStr = getParameter("bgcolor");
		} catch (Exception e) {
		}

		if (colorStr != null) {
			c = Color.decode(colorStr);
		}

		jg.setBackground(c);
	}

	@SuppressWarnings("rawtypes")
	private void positionVertexAt(Object vertex, int x, int y) {
		DefaultGraphCell cell = m_jgAdapter.getVertexCell(vertex);
		Map<?, ?> attr = cell.getAttributes();
		Rectangle b = GraphConstants.getBounds(attr).getBounds();

		GraphConstants.setBounds(attr, new Rectangle(x, y, b.width, b.height));

		Map<DefaultGraphCell, Map> cellAttr = new HashMap<DefaultGraphCell, Map>();
		cellAttr.put(cell, attr);
		m_jgAdapter.edit(cellAttr, null, null, null);
	}

}
