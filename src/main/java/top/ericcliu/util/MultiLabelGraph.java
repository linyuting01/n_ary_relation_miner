/*
 * Copyright (c) @ EriccLiu 2018.
 */

package top.ericcliu.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Table;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author liubi
 * @date 2018-12-04 13:27
 **/
public class MultiLabelGraph {
    /**
     * valueGraph: graph
     * nodeLabels: key: nodeId Value: labelId of corresponding node
     * labelNodes: key: labelId Value: nodeId of corresponding label
     */
    private Multimap<Integer, Integer> nodeLabels = null;
    private Multimap<Integer, Integer> labelNodes = null;
    private ImmutableValueGraph valueGraph = null;
    private Integer typeRelatedNum = -1;
    public String graphName;
    /**
     * typeId ie. root node id
     */
    private Integer typeId;
    private final Integer replacedTypeId = Integer.MIN_VALUE;
    /**
     * key1 : labelA , key2 : labelB
     * value: Map<DFScode,EdgeInstance> using one edge DFS code to represent edge
     */
    private Table<Integer, Integer, Map<DFScode, EdgeInstance>> graphEdge = null;

    public Integer getTypeRelatedNum() {
        return typeRelatedNum;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public MultiLabelGraph(Boolean small) throws Exception {
        if (small) {
            this.graphName = "small";
            this.typeId = 3;
            Integer nodeCount = 6;
            MutableValueGraph graph = ValueGraphBuilder
                    .undirected()
                    .expectedNodeCount(nodeCount)
                    .build();
            for (int i = 0; i < nodeCount; i++) {
                graph.addNode(i);
            }

            Multimap<Integer, Integer> nodeLabels = MultimapBuilder.hashKeys().hashSetValues().build();
            nodeLabels.put(0, 0);
            nodeLabels.put(0, 1);
            nodeLabels.put(1, 1);
            nodeLabels.put(2, 2);
            //nodeLabels.put(2, 3);
            nodeLabels.put(2, this.replacedTypeId);  // -1 means type id , ie root node id of a pattern, make it the minGspanEdge, extension start from this node
            nodeLabels.put(3, 5);
            //nodeLabels.put(4, 3);
            nodeLabels.put(4, this.replacedTypeId);
            nodeLabels.put(4, 6);
            nodeLabels.put(5, 4);
            nodeLabels.put(5, 5);
            this.nodeLabels = nodeLabels;

            Multimap<Integer, Integer> labelNodes = MultimapBuilder.hashKeys().hashSetValues().build();
            labelNodes.put(0, 0);
            labelNodes.put(1, 0);
            labelNodes.put(1, 1);
            //labelNodes.put(3, 2);
            labelNodes.put(this.replacedTypeId, 2);
            labelNodes.put(2, 2);
            labelNodes.put(5, 3);
            //labelNodes.put(3, 4);
            labelNodes.put(this.replacedTypeId, 4);
            labelNodes.put(6, 4);
            labelNodes.put(4, 5);
            labelNodes.put(5, 5);
            this.labelNodes = labelNodes;

            Table<Integer, Integer, Map<DFScode, EdgeInstance>> graphEdge = HashBasedTable.create();
            addEdgeToGraph(graph, 0, 2, 1, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 3, 2, 2, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 3, 4, 2, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 1, 4, 1, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 2, 5, 3, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 5, 4, 2, nodeLabels, graphEdge);

            this.valueGraph = ImmutableValueGraph.copyOf(graph);
            this.graphEdge = graphEdge;

        } else {
            this.graphName = "big";
            this.typeId = 1;
            Integer nodeCount = 17;
            MutableValueGraph graph = ValueGraphBuilder
                    .undirected()
                    .expectedNodeCount(nodeCount)
                    .build();
            for (int i = 1; i < nodeCount + 1; i++) {
                graph.addNode(i);
            }

            Multimap<Integer, Integer> nodeLabels = MultimapBuilder.hashKeys().hashSetValues().build();
            nodeLabels.put(1, 6);
            nodeLabels.put(1, 7);
            nodeLabels.put(2, 7);
            nodeLabels.put(2, 8);
            nodeLabels.put(3, 8);
            nodeLabels.put(4, 6);
            nodeLabels.put(5, 2);
            nodeLabels.put(5, 10);
            nodeLabels.put(6, 3);
            nodeLabels.put(6, 20);
            nodeLabels.put(7, 4);
            nodeLabels.put(7, 20);
            nodeLabels.put(7, 3);
            nodeLabels.put(8, 5);
            nodeLabels.put(8, 10);
            nodeLabels.put(9, 3);
            nodeLabels.put(9, 15);
            nodeLabels.put(10, 2);
            nodeLabels.put(10, 20);
            nodeLabels.put(11, 0);
            //nodeLabels.put(11, 1);
            nodeLabels.put(11, this.replacedTypeId);
            nodeLabels.put(12, 0);
            nodeLabels.put(12, 6);
            nodeLabels.put(13, 9);
            nodeLabels.put(14, 0);
            //nodeLabels.put(14, 1);
            nodeLabels.put(14, this.replacedTypeId);
            nodeLabels.put(14, 7);
            nodeLabels.put(15, 2);
            nodeLabels.put(15, 20);
            nodeLabels.put(16, 3);
            nodeLabels.put(16, 10);
            nodeLabels.put(17, 3);
            nodeLabels.put(17, 4);
            nodeLabels.put(17, 15);
            this.nodeLabels = nodeLabels;

            Multimap<Integer, Integer> labelNodes = MultimapBuilder.hashKeys().hashSetValues().build();
            labelNodes.put(6, 1);
            labelNodes.put(7, 1);
            labelNodes.put(7, 2);
            labelNodes.put(8, 2);
            labelNodes.put(8, 3);
            labelNodes.put(6, 4);
            labelNodes.put(2, 5);
            labelNodes.put(10, 5);
            labelNodes.put(3, 6);
            labelNodes.put(20, 6);
            labelNodes.put(4, 7);
            labelNodes.put(20, 7);
            labelNodes.put(3, 7);
            labelNodes.put(5, 8);
            labelNodes.put(10, 8);
            labelNodes.put(3, 9);
            labelNodes.put(15, 9);
            labelNodes.put(2, 10);
            labelNodes.put(20, 10);
            labelNodes.put(0, 11);
            //labelNodes.put(1, 11);
            labelNodes.put(this.replacedTypeId, 11);
            labelNodes.put(0, 12);
            labelNodes.put(6, 12);
            labelNodes.put(9, 13);
            labelNodes.put(0, 14);
            //labelNodes.put(1, 14);
            labelNodes.put(this.replacedTypeId, 14);
            labelNodes.put(7, 14);
            labelNodes.put(2, 15);
            labelNodes.put(20, 15);
            labelNodes.put(3, 16);
            labelNodes.put(10, 16);
            labelNodes.put(3, 17);
            labelNodes.put(4, 17);
            labelNodes.put(15, 17);
            this.labelNodes = labelNodes;

            Table<Integer, Integer, Map<DFScode, EdgeInstance>> graphEdge = HashBasedTable.create();
            addEdgeToGraph(graph, 5, 1, 6, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 7, 1, 7, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 7, 2, 7, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 7, 4, 7, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 8, 2, 8, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 8, 3, 9, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 9, 3, 7, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 10, 4, 6, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 11, 5, 1, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 11, 6, 2, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 11, 7, 3, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 11, 8, 4, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 11, 13, 5, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 12, 13, 5, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 12, 10, 1, nodeLabels, graphEdge);
            //addEdgeToGraph(graph, 12, 10, 6, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 12, 9, 2, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 12, 8, 4, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 12, 7, 3, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 14, 13, 5, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 14, 15, 1, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 14, 16, 3, nodeLabels, graphEdge);
            addEdgeToGraph(graph, 14, 17, 3, nodeLabels, graphEdge);

            this.graphEdge = graphEdge;
            this.valueGraph = ImmutableValueGraph.copyOf(graph);
        }
    }

    /**
     * initiate from DFS code
     *
     * @param dfScode
     * @throws Exception
     */
    public MultiLabelGraph(DFScode dfScode) throws Exception {
        this.graphName = "dfScode";
        this.typeId = dfScode.getEdgeSeq().get(0).getLabelA();
        MutableValueGraph graph = ValueGraphBuilder
                .undirected()
                .expectedNodeCount(dfScode.getNodes().size())
                .build();
        for (Integer nodeId : dfScode.getNodes()) {
            graph.addNode(nodeId);
        }
        Multimap<Integer, Integer> nodeLabels = MultimapBuilder.hashKeys().hashSetValues().build();
        Multimap<Integer, Integer> labelNodes = MultimapBuilder.hashKeys().hashSetValues().build();
        for (Map.Entry<Integer, Integer> nodeLabel : dfScode.getNodeLabelMap().entrySet()) {
            nodeLabels.put(nodeLabel.getKey(), nodeLabel.getValue());
            labelNodes.put(nodeLabel.getValue(), nodeLabel.getKey());
        }
        this.nodeLabels = nodeLabels;
        this.labelNodes = labelNodes;

        Table<Integer, Integer, Map<DFScode, EdgeInstance>> graphEdge = HashBasedTable.create();
        for (GSpanEdge edge : dfScode.getEdgeSeq()) {
            addEdgeToGraph(graph, edge.getNodeA(), edge.getNodeB(), edge.getEdgeLabel(), nodeLabels, graphEdge);
        }
        this.valueGraph = ImmutableValueGraph.copyOf(graph);
        this.graphEdge = graphEdge;
    }

    /**
     * init from Json file
     *
     * @param filepath
     */
    public MultiLabelGraph(String filepath) throws Exception {
        this(TypeRelatedGraph.readFromFile(filepath));
        this.graphName = filepath;
        // new MultiLabelGraph(typeRelatedGraph); 在java中，如果一个构造方法想调用另一个构造方法，需要是用this(参数列表)的形式，自动调用对应的构造方法。不可以直接使用类名进行调用。
    }

    public MultiLabelGraph(TypeRelatedGraph typeRelatedGraph) throws Exception {
        this.graphName = "typeRelatedGraph";
        this.typeId = typeRelatedGraph.getTypeId();
        this.typeRelatedNum = typeRelatedGraph.getNodes().size();

        this.nodeLabels = MultimapBuilder.treeKeys().hashSetValues().build();
        for (Integer key : typeRelatedGraph.getNodeLabels().keySet()) {
            for (Integer value : typeRelatedGraph.getNodeLabels().get(key)) {
                if (value.equals(this.typeId)) {
                    this.nodeLabels.put(key, this.replacedTypeId);
                } else {
                    this.nodeLabels.put(key, value);
                }
            }
        }

        this.labelNodes = MultimapBuilder.treeKeys().hashSetValues().build();
        for (Integer node : this.nodeLabels.keySet()) {
            for (Integer label : this.nodeLabels.get(node)) {
                this.labelNodes.put(label, node);
            }
        }

        Table<Integer, Integer, Map<DFScode, EdgeInstance>> graphEdge = HashBasedTable.create();
        MutableValueGraph graph = ValueGraphBuilder.undirected().build();
        if (typeRelatedGraph.getTriples() != null && !typeRelatedGraph.getTriples().isEmpty()) {
            for (int[] spo : typeRelatedGraph.getTriples()) {
                if (spo[0] != spo[2]) {
                    graph.addNode(spo[0]);
                    graph.addNode(spo[2]);
                    addEdgeToGraph(graph, spo[0], spo[2], spo[1], nodeLabels, graphEdge);
                }
            }
        }
        this.graphEdge = graphEdge;

        this.valueGraph = ImmutableValueGraph.copyOf(graph);
    }

    private static void addEdgeToGraph(MutableValueGraph graph,
                                       Integer nodeIdA,
                                       Integer nodeIdB,
                                       Integer edgeLabel,
                                       Multimap<Integer, Integer> nodeLabels,
                                       Table<Integer, Integer, Map<DFScode, EdgeInstance>> graphEdge) throws Exception {
        // 每个node上 有不同的label 通过排列组合得到 多个边
        graph.putEdgeValue(nodeIdA, nodeIdB, edgeLabel);
        for (Integer label1 : nodeLabels.get(nodeIdA)) {
            for (Integer label2 : nodeLabels.get(nodeIdB)) {
                Integer nodeLabelA;
                Integer nodeLabelB;
                if (label2 < label1) {
                    nodeLabelA = label2;
                    nodeLabelB = label1;
                    // 逆序边  在数据图中 nodeIdB --> nodeIdA
                } else {
                    nodeLabelA = label1;
                    nodeLabelB = label2;
                }
                DFScode dfScode = new DFScode(new GSpanEdge(0, 1, nodeLabelA, nodeLabelB, edgeLabel, 1));
                EdgeInstance instance;
                Map<DFScode, EdgeInstance> edge;
                if (graphEdge.contains(nodeLabelA, nodeLabelB)) {
                    edge = graphEdge.get(nodeLabelA, nodeLabelB);
                } else {
                    edge = new HashMap<>();
                }
                if (edge.containsKey(dfScode)) {
                    instance = edge.get(dfScode);
                } else {
                    instance = new EdgeInstance();
                }
                int[] instanceMap = new int[2];

                if (label2 < label1) {
                    instanceMap[0] = nodeIdB;
                    instanceMap[1] = nodeIdA;
                } else {
                    instanceMap[0] = nodeIdA;
                    instanceMap[1] = nodeIdB;
                }
                if (instance.addInstance(dfScode, instanceMap)) {
                    edge.put(dfScode, instance);
                    graphEdge.put(nodeLabelA, nodeLabelB, edge);
                } else {
                    throw new Exception("add instance error");
                }
            }
        }
    }

    public Multimap<Integer, Integer> getNodeLabels() {
        return nodeLabels;
    }

    public ImmutableValueGraph getValueGraph() {
        return valueGraph;
    }

    public Table<Integer, Integer, Map<DFScode, EdgeInstance>> getGraphEdge() {
        return graphEdge;
    }

    public Multimap<Integer, Integer> getLabelNodes() {
        return labelNodes;
    }

    public void setLabelNodes(Multimap<Integer, Integer> labelNodes) {
        this.labelNodes = labelNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MultiLabelGraph that = (MultiLabelGraph) o;

        if (!Objects.equals(nodeLabels, that.nodeLabels)) {
            return false;
        }
        if (!Objects.equals(valueGraph, that.valueGraph)) {
            return false;
        }
        return Objects.equals(graphEdge, that.graphEdge);
    }

    @Override
    public int hashCode() {
        int result = nodeLabels != null ? nodeLabels.hashCode() : 0;
        result = 31 * result + (valueGraph != null ? valueGraph.hashCode() : 0);
        result = 31 * result + (graphEdge != null ? graphEdge.hashCode() : 0);
        return result;
    }

    public static void main(String[] args) throws Exception {

/*        File dir = new File("D_10P");
        FileWriter res = new FileWriter("res");


        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {

                System.out.println(file.getAbsolutePath());
                res.write(file.getAbsolutePath()+System.lineSeparator());
                MultiLabelGraph graphFromFile = new MultiLabelGraph(file.getAbsolutePath());
                if (graphFromFile.labelNodes.size() == graphFromFile.nodeLabels.size()) {
                    res.write("true"+System.lineSeparator());
                } else {
                    res.write("false"+System.lineSeparator());
                }

                res.write(graphFromFile.labelNodes.toString()+System.lineSeparator());
                res.write(graphFromFile.nodeLabels.toString()+System.lineSeparator());
                res.write(graphFromFile.valueGraph.nodes().size()+System.lineSeparator());
                res.write(graphFromFile.valueGraph.toString()+System.lineSeparator());
*//*                System.out.println(graphFromFile.labelNodes.size() == graphFromFile.nodeLabels.size());
                System.out.println(graphFromFile.labelNodes);
                System.out.println(graphFromFile.nodeLabels);
                System.out.println(graphFromFile.valueGraph.nodes().size());
                System.out.println(graphFromFile.valueGraph);*//*

                for (Integer row : graphFromFile.graphEdge.rowKeySet()) {
                    for (Map.Entry<Integer, Map<DFScode, EdgeInstance>> entry : graphFromFile.graphEdge.row(row).entrySet()) {
                        for (Map.Entry<DFScode, EdgeInstance> entry1 : entry.getValue().entrySet()) {
                            if (entry1.getValue().calMNI() > 1) {
                                res.write("labelA: " + row+System.lineSeparator());
                                res.write("labelB: " + entry.getKey()+System.lineSeparator());
                                res.write("DFS code " + entry1.getKey()+System.lineSeparator());
                                res.write("DFS code MNI " + entry1.getValue().calMNI()+System.lineSeparator());
                                res.write("____________________________________________________________________________________________________________________________"+System.lineSeparator());
                                System.out.println("labelA : " + row);
                                System.out.println("labelB: " + entry.getKey());
                                System.out.println("DFS code " + entry1.getKey());
                                System.out.println("DFS code MNI " + entry1.getValue().calMNI());
                                System.out.println("____________________________________________________________________________________________________________________________");
                            }
                        }
                    }
                }

            }
        }
        res.close();*/



        MultiLabelGraph graphBig= new MultiLabelGraph("TEST14963072.json");
        System.out.println("graphBig.labelNodes.size() == graphBig.nodeLabels.size()");
        System.out.println(graphBig.labelNodes.size() == graphBig.nodeLabels.size());
        System.out.println("graphBig.labelNodes");
        System.out.println(graphBig.labelNodes.keySet().size());
        System.out.println(graphBig.labelNodes);
        System.out.println("graphBig.nodeLabels");
        System.out.println(graphBig.nodeLabels.keySet().size());
        System.out.println(graphBig.nodeLabels);
        System.out.println("graphBig.valueGraph.nodes()");
        System.out.println(graphBig.valueGraph.nodes().size());
        System.out.println(graphBig.valueGraph);

        for (Integer row : graphBig.graphEdge.rowKeySet()) {
            for (Map.Entry<Integer, Map<DFScode, EdgeInstance>> entry : graphBig.graphEdge.row(row).entrySet()) {
                for (Map.Entry<DFScode, EdgeInstance> entry1 : entry.getValue().entrySet()) {
                    if (entry1.getValue().calMNI() > 1) {
                        System.out.println("labelA : " + row);
                        System.out.println("labelB: " + entry.getKey());
                        System.out.println("DFS code " + entry1.getKey());
                        System.out.println("DFS code MNI " + entry1.getValue().calMNI());
                        System.out.println("____________________________________________________________________________________________________________________________");
                    }
                }
            }
        }
    }
}
