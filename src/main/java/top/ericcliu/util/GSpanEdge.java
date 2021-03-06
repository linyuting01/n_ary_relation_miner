package top.ericcliu.util;


import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Represents the edge tuples used in gSpan to represent one edge in
 * the DFS-Code.
 * <p>
 * It can/will be stored in local object pool to avoid object generation/garbage
 * collection.
 * 
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 * 
 * @param <NodeType>
 *            the type of the node labels (will be hashed and checked with
 *            .equals(..))
 * @param <EdgeType>
 *            the type of the edge labels (will be hashed and checked with
 *            .equals(..))
 */
public class GSpanEdge<NodeType, EdgeType> implements
		Comparable<GSpanEdge<NodeType, EdgeType>>,
		Serializable {

	private int nodeA, nodeB;  // nodeId

	private int labelA, labelB; // label of node

	private int edgeLabel, direction; // edgeLabel ; direction of edge


    public GSpanEdge(){
    }

    public GSpanEdge(int nodeA, int nodeB, int labelA, int labelB, int edgeLabel, int direction) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.labelA = labelA;
        this.labelB = labelB;
        this.edgeLabel = edgeLabel;
        this.direction = direction;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see Comparable #compareTo(T)
	 */
	@Override
    public int compareTo(final GSpanEdge<NodeType, EdgeType> arg0) {
		return compareTo(arg0, arg0.nodeB);
	}

	/**
	 * compares this edge with the given <code>other</code> one,
	 *
	 * @param other
	 * @param nodeB
	 *            thid node is used as the second node for the other edge
	 * @return <0; 0; or >0
	 */
	public final int compareTo(final GSpanEdge<NodeType, EdgeType> other,
			final int nodeB) {
		if (this.nodeA == other.nodeA) {
			if (this.nodeB != nodeB) {
				return this.nodeB - nodeB;
			}
			if (this.direction != other.direction) {
				return other.direction - this.direction;
			}
			if (this.labelA != other.labelA) {
				return this.labelA - other.labelA;
			}
			if (this.edgeLabel != other.edgeLabel) {
				return this.edgeLabel - other.edgeLabel;
			}
			return this.labelB - other.labelB;
		} else { // TODO: das laesst sich bestimmt noch irgendwie schoener
		    // schreiben
			if (this.nodeA < this.nodeB) {
				if (this.nodeB == other.nodeA) {
					return -1;
					// see paper
				} else {
					if (other.nodeA > this.nodeA) {
						if (other.nodeA > this.nodeB) {
							return -1;
						} else {
							return 1;
						}
					} else {
						if (this.nodeA >= nodeB) {
							return 1;
						} else {
							return -1;
						}
					}
				}
			} else if (other.nodeA < nodeB) {
				if (nodeB == this.nodeA) {
					return 1;
					// see paper
				} else {
					if (other.nodeA > this.nodeA) {
						if (other.nodeA >= this.nodeB) {
							return -1;
						} else {
							return 1;
						}
					} else {
						if (this.nodeA > nodeB) {
							return 1;
						} else {
							return -1;
						}
					}
				}
			} else { // compare two backwards edges with different nodeA
				return this.nodeA - other.nodeA;
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new GSpanEdge<NodeType, EdgeType>( nodeA,  nodeB,  labelA,  labelB,  edgeLabel,  direction);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see Object#equals(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GSpanEdge && compareTo((GSpanEdge) obj) == 0;
	}


	/** @return the direction of the edge */
	public final int getDirection() {
		return direction;
	}

	/** @return the edge label index of the edge */
	public final int getEdgeLabel() {
		return edgeLabel;
	}

	/** @return the node label index of the first node of the edge */
	public final int getLabelA() {
		return labelA;
	}

	/** @return the node label index of the second node of the edge */
	public final int getLabelB() {
		return labelB;
	}

	/** @return the DFS-index of the first node of the edge */
	public final int getNodeA() {
		return nodeA;
	}

	/** @return the DFS-index of the second node of the edge */
	public final int getNodeB() {
		return nodeB;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return nodeA << 20 + nodeB << 16 + labelA << 12 + labelB << 8 + edgeLabel << 4 + direction;
	}

	/** @return if this edge is a forward edge */
/*	public final boolean isForward() {
		return nodeA < nodeB;
	}*/

	@Override
	public String toString() {
		return "GSpanEdge{" +
				"nodeA=" + nodeA +
				", nodeB=" + nodeB +
				", labelA=" + labelA +
				", labelB=" + labelB +
				", edgeLabel=" + edgeLabel +
				", direction=" + direction +
				'}';
	}

	public static void main(String[] args){
		LinkedList<Integer> linkedList = new LinkedList<>();
		for(int i =0; i<100;i++){
			linkedList.add(i);
		}
		Iterator<Integer> itDesc = linkedList.descendingIterator();
		while (itDesc.hasNext()){
			System.out.println(itDesc.next());
		}
	    GSpanEdge edge1 = new GSpanEdge(1,2,3,4,5,1);
	    System.out.println(edge1.toString());
	    GSpanEdge edge2 = new GSpanEdge(2,3,3,4,5,1);
        System.out.println(edge2.toString());
        System.out.println(edge1.compareTo(edge2));
    }
}
