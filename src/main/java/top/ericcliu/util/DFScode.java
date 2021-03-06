package top.ericcliu.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Table;
import javafx.util.Pair;
import org.apache.jena.base.Sys;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * @author liubi
 * @date 2019-01-19 20:15
 **/
public class DFScode implements Cloneable {
    /**
     * 边的集合，边的排序代表着边的添加次序
     */
    private ArrayList<GSpanEdge> edgeSeq;
    private Integer maxNodeId = -1;
    /**
     * key : nodes appeared in this DFS code, ie nodeId in DFScode, having no relation with dataGraph
     * value : node label of this node in DFS code
     */
    private Map<Integer, Integer> nodeLabelMap;


    public DFScode addEdge(GSpanEdge edge) throws CloneNotSupportedException {
        // 还需要 判断加入的边是否合法
        if (this.edgeSeq == null) {
            this.edgeSeq = new ArrayList<>();
        }
        if (this.nodeLabelMap == null) {
            this.nodeLabelMap = new HashMap<>();
        }

        this.edgeSeq.add(((GSpanEdge) edge.clone()));
        Integer nodeA = edge.getNodeA();
        Integer nodeB = edge.getNodeB();
        this.nodeLabelMap.put(nodeA, edge.getLabelA());
        this.nodeLabelMap.put(nodeB, edge.getLabelB());
        this.maxNodeId = nodeA > nodeB ? nodeA : nodeB;
        return this;
    }

    public DFScode(GSpanEdge edge) throws CloneNotSupportedException {
        this.edgeSeq = new ArrayList<>();
        this.nodeLabelMap = new HashMap<>();
        this.edgeSeq.add(((GSpanEdge) edge.clone()));
        Integer nodeA = edge.getNodeA();
        Integer nodeB = edge.getNodeB();
        this.nodeLabelMap.put(nodeA, edge.getLabelA());
        this.nodeLabelMap.put(nodeB, edge.getLabelB());
        this.maxNodeId = nodeA > nodeB ? nodeA : nodeB;
    }

    public DFScode(DFScodeJson dfScodeJson) {
        ObjectMapper mapper = new ObjectMapper();
        this.edgeSeq = new ArrayList<>(dfScodeJson.getEdgeSeq().size());
        for (Object object : dfScodeJson.getEdgeSeq()) {
            this.edgeSeq.add(mapper.convertValue(object, GSpanEdge.class));
        }
        this.maxNodeId = dfScodeJson.getMaxNodeId();
        this.nodeLabelMap = new TreeMap<>(dfScodeJson.getNodeLabelMap());
    }

    public DFScode() {
        this.edgeSeq = new ArrayList<>();
        this.nodeLabelMap = new HashMap<>();
    }

    public DFScode(ArrayList<GSpanEdge> edgeSeq) {
        this.edgeSeq = new ArrayList<>(edgeSeq);
        this.nodeLabelMap = new HashMap<>();
        for (GSpanEdge edge : edgeSeq) {
            Integer nodeA = edge.getNodeA();
            Integer nodeB = edge.getNodeB();
            this.nodeLabelMap.put(nodeA, edge.getLabelA());
            this.nodeLabelMap.put(nodeB, edge.getLabelB());
            if (nodeA > this.maxNodeId) {
                this.maxNodeId = nodeA;
            }
            if (nodeB > this.maxNodeId) {
                this.maxNodeId = nodeB;
            }
        }
    }

    /**
     * if this dfsCode is parent of  possibleChild return true else return false
     *
     * @param possibleChild
     * @return
     */
    public boolean isParentOf(DFScode possibleChild) {
        if (possibleChild.getEdgeSeq().isEmpty()) {
            return false;
        } else if (this.getEdgeSeq().isEmpty()) {
            return false;
        } else if (possibleChild.getEdgeSeq().size() <= this.getEdgeSeq().size()) {
            return false;
        } else {
            for (int i = 0; i < this.getEdgeSeq().size(); i++) {
                GSpanEdge parentEdge = this.getEdgeSeq().get(i);
                GSpanEdge childEdge = possibleChild.getEdgeSeq().get(i);
                if (!parentEdge.equals(childEdge)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static DFScode readFromFile(String filePath) throws Exception {
        File file = new File(filePath);
        DFScodeJson dfScodeJson;
        if (file.exists()) {
            // 增加jackson 对google guava的支持
            ObjectMapper mapper = new ObjectMapper();

            dfScodeJson = mapper.readValue(file, DFScodeJson.class);
        } else {
            throw new Exception("file does not exist");
        }
        return new DFScode(dfScodeJson);
    }

    public static boolean removeDupDump(Pair<String, Map<Integer, DFScode>> dfScodes) throws Exception {
        String graphFile = dfScodes.getKey();
        Map<Integer, DFScode> map = dfScodes.getValue();
        if (map.isEmpty()) {
            return false;
        } else if (map.size() == 1) {
            map.get(1).saveToFile("READ" + graphFile + "Id_1.json", false);
            return true;
            // id start from 1
        }
        else {
            for(int i=1;i<map.size()+1;i++){
                boolean flag = true;
                DFScode currentDFScode = map.get(i);
                for(int j=i+1;j<map.size()+1;j++){
                    DFScode nextDFScode = map.get(j);
                    if(currentDFScode.isParentOf(nextDFScode)){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    currentDFScode.saveToFile("NODUP" + graphFile + "Id_"+i+".json", false);
                }
            }
            return true;
        }
    }
    public static void removeDupDumpReadable(String dirPath ,String dataBasePath,boolean inOneDir) throws Exception {
        // boolean inOneDir true: 结果保存在一级目录中 false: 结果保存在第二级目录中
        try {
            File dirFile = new File(dirPath);
            File[] files ;
            if(dirFile.isDirectory()){
                files = dirFile.listFiles();
            }
            else {
                throw new Exception("dirPath must be a dir");
            }
            if(inOneDir){
                //结果保存在一级目录中
                ArrayList<Pair<String,Map<Integer, DFScode>>> dfScodes = new ArrayList<>();
                Map<Integer, DFScode> currentMap = new HashMap<>();
                String lastGraphFile = "";
                for(File file : files){
                    if(file.isDirectory()){
                        continue;
                    }
                    String fileName = file.getName();
                    if(fileName.length()>=2&&fileName.charAt(0)=='R' && fileName.charAt(1)=='E'&& file.length()>1){
                        String graphFile = fileName.split("Id_")[0];
                        Integer relationId = Integer.parseInt(fileName.split("Id_")[1].replace(".json",""));
                        if(!graphFile.equals(lastGraphFile)){
                            dfScodes.add(new Pair<>(lastGraphFile,currentMap));
                            lastGraphFile = graphFile;
                            currentMap = new HashMap<>();
                        }
                        DFScode dfScode = DFScode.readFromFile(fileName);
                        currentMap.put(relationId,dfScode);
                    }
                }
                for (Pair<String,Map<Integer, DFScode>> dFScodeOfFile : dfScodes){
                    //DFScode.removeDupDumpReadable(dFScodeOfFile,"C:\\bioportal.sqlite");
                    String graphFile = dFScodeOfFile.getKey();
                    Map<Integer, DFScode> map = dFScodeOfFile.getValue();
                    if (map.isEmpty()) {
                        continue;
                    }
                    else if (map.size() == 1) {
                        new DFScodeString(map.get(1),dataBasePath).saveToFile(dirFile.getAbsolutePath()+File.separator+"READ" + graphFile + "Id_1.json", false);
                        // id start from 1
                    }
                    else {
                        for(int i=1;i<map.size()+1;i++){
                            boolean flag = true;
                            DFScode currentDFScode = map.get(i);
                            for(int j=i+1;j<map.size()+1;j++){
                                DFScode nextDFScode = map.get(j);
                                if(currentDFScode.isParentOf(nextDFScode)){
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag){
                                new DFScodeString(currentDFScode,dataBasePath).saveToFile(dirFile.getAbsolutePath()+File.separator+"READ" + graphFile + "Id_"+i+".json", false);
                            }
                        }
                    }
                }
            }
            else {
                //结果保存在第二级目录中
                ArrayList<Pair<File,Map<Integer, DFScode>>> dfScodes = new ArrayList<>();
                for(File dir: files){
                    if(!dir.isDirectory()){
                        continue;
                    }
                    File[] reFiles = dir.listFiles();
                    Map<Integer, DFScode> currentMap = new HashMap<>();
                    for(File reFile :reFiles){
                        String fileName = reFile.getName();
                        if(fileName.length()>=2&&fileName.charAt(0)=='R' && fileName.charAt(1)=='E' && reFile.length()>1){
                            String graphFile = dir.getName();
                            Integer relationId = Integer.parseInt(fileName.split("Id_")[1].replace(".json",""));
                            System.out.println(dir.getAbsolutePath()+File.separator+fileName);
                            DFScode dfScode = DFScode.readFromFile(dir.getAbsolutePath()+File.separator+fileName);
                            currentMap.put(relationId,dfScode);
                        }
                    }
                    dfScodes.add(new Pair<>(dir,currentMap));
                }
                for (Pair<File,Map<Integer, DFScode>> dFScodeOfFile : dfScodes){
                    File graphFile = dFScodeOfFile.getKey();
                    Map<Integer, DFScode> map = dFScodeOfFile.getValue();
                    if (map.isEmpty()) {
                        continue;
                    }
                    else if (map.size() == 1) {
                        new DFScodeString(map.get(1),dataBasePath).saveToFile(graphFile.getAbsolutePath()+File.separator+"READRE_" + graphFile.getName() + "Id_1.json", false);
                        // id start from 1
                    }
                    else {
                        for(int i=1;i<map.size()+1;i++){
                            boolean flag = true;
                            DFScode currentDFScode = map.get(i);
                            for(int j=i+1;j<map.size()+1;j++){
                                DFScode nextDFScode = map.get(j);
                                if(currentDFScode.isParentOf(nextDFScode)){
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag){
                                new DFScodeString(currentDFScode,dataBasePath).saveToFile(graphFile.getAbsolutePath()+File.separator+"READRE_" + graphFile.getName() + "Id_"+i+".json", false);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }


    public boolean saveToFile(String filePath, boolean isAppend) throws Exception {
        File file = new File(filePath);
        FileWriter fileWriter;
        DFScodeJson dfScodeJson = new DFScodeJson(this);
        if (file.exists()) {
            fileWriter = new FileWriter(filePath, isAppend);
        } else {
            fileWriter = new FileWriter(filePath);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(fileWriter, dfScodeJson);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LinkedList<Integer> getRightMostPath() throws Exception {
        LinkedList<Integer> rightMostPath = new LinkedList<>();
        for (GSpanEdge edge : this.edgeSeq) {
            int nodeA = edge.getNodeA();
            int nodeB = edge.getNodeB();
            if (nodeA < nodeB) {
                //forward edge
                if (rightMostPath.size() == 0) {
                    rightMostPath.add(nodeA);
                    rightMostPath.add(nodeB);
                } else {
                    while (rightMostPath.getLast() != nodeA) {
                        rightMostPath.removeLast();
                    }
                    if (rightMostPath.size() == 0) {
                        throw new Exception("input error in DFScode, not connected graph");
                    }
                    rightMostPath.add(nodeB);
                }
            } else if (nodeA == nodeB) {
                throw new Exception("input error in GSpanEdge, nodeA == nodeB");
            }
            // backward edge 对于 最右路径没有 改变
            // 因此 DFScode生长时需要判断是否真的增长了
        }
        return rightMostPath;
    }

    private GSpanEdge minRightMostPathExtension(DFScode minDFSCode, Table<Integer, Integer, Set<GSpanEdge>> blocks) throws Exception {
        GSpanEdge minEdge = null;
        Set<GSpanEdge> childrenEdge = new LinkedHashSet<>();
        if (minDFSCode.edgeSeq.size() == 0) {
            for (Set<GSpanEdge> edges : blocks.values()) {
                childrenEdge.addAll(edges);
            }
        } else {
            LinkedList<Integer> rightMostPath = minDFSCode.getRightMostPath();
            Integer rightMostNode = rightMostPath.getLast();
            if (rightMostPath.size() == 0 || rightMostPath.size() == 1) {
                throw new Exception("right most path size is 0 or 1, ERROR");
            } else {
                // 1  not allow self loop
                // 0  allow self loop
                int leastRMPSize = 1;
                if (rightMostPath.size() > leastRMPSize) {
                    // backward extend, 最后1个节点，无需和最右节点组成新的边，且 不允许和最右节点组成后向边，构成self looped edge
                    ListIterator<Integer> rightMostPathIt = rightMostPath.listIterator();
                    int label1 = minDFSCode.getNodeLabel(rightMostNode);
                    while (rightMostPathIt.hasNext()) {
                        int node2 = rightMostPathIt.next();
                        int label2 = minDFSCode.getNodeLabel(node2);

                        Set<GSpanEdge> possibleChildren = new HashSet<>();
                        Set<GSpanEdge> set1 = blocks.get(label1, label2);
                        Set<GSpanEdge> set2 = blocks.get(label2, label1);
                        // 反转了
                        // 组成新边的时候 没有考虑反转
                        if (set1 != null) {
                            possibleChildren.addAll(set1);
                        }
                        if (set2 != null) {
                            possibleChildren.addAll(set2);
                        }
                        for (GSpanEdge possibleChild : possibleChildren) {
                            GSpanEdge possibleEdge = new GSpanEdge(rightMostNode, node2, label1, label2, possibleChild.getEdgeLabel(), 1);
                            GSpanEdge possibleEdgeReverse = new GSpanEdge(node2, rightMostNode, label2, label1, possibleChild.getEdgeLabel(), 1);
                            // 不允许有环
                            if (!minDFSCode.getEdgeSeq().contains(possibleEdge) && !minDFSCode.getEdgeSeq().contains(possibleEdgeReverse)) {
                                childrenEdge.add(possibleEdge);
                            }
                        }
                    }
                }
                // forward extend
                Iterator<Integer> descRMPit = rightMostPath.descendingIterator();
                while (descRMPit.hasNext()) {
                    Integer nodeInRMP = descRMPit.next();
                    Integer nodeInRMPLabel = minDFSCode.getNodeLabel(nodeInRMP);
                    Set<GSpanEdge> possibleChildren = new HashSet<>();
                    for (Set<GSpanEdge> edgesLabelB : blocks.row(nodeInRMPLabel).values()) {
                        possibleChildren.addAll(edgesLabelB);
                    }
                    for (GSpanEdge possibleChild : possibleChildren) {
                        int nodeId2 = minDFSCode.getMaxNodeId() + 1;
                        int nodeLabel2 = possibleChild.getLabelB();
                        int edgeLabel = possibleChild.getEdgeLabel();
                        GSpanEdge possibleEdge = new GSpanEdge(nodeInRMP, nodeId2, nodeInRMPLabel, nodeLabel2, edgeLabel, 1);
                        childrenEdge.add(possibleEdge);
                    }

                    possibleChildren = new HashSet<>();
                    for (Set<GSpanEdge> edgesLabelA : blocks.column(nodeInRMPLabel).values()) {
                        possibleChildren.addAll(edgesLabelA);
                    }
                    for (GSpanEdge possibleChild : possibleChildren) {
                        int nodeId2 = minDFSCode.getMaxNodeId() + 1;
                        int nodeLabel2 = possibleChild.getLabelA();
                        int edgeLabel = possibleChild.getEdgeLabel();
                        GSpanEdge possibleEdge = new GSpanEdge(nodeInRMP, nodeId2, nodeInRMPLabel, nodeLabel2, edgeLabel, 1);
                        childrenEdge.add(possibleEdge);
                    }
                }
            }
        }
        if (childrenEdge.size() == 0) {
            return null;
        } else {
            Iterator<GSpanEdge> childEdgeIt = childrenEdge.iterator();
            while (childEdgeIt.hasNext()) {
                GSpanEdge childEdge = childEdgeIt.next();
                if (minEdge == null || childEdge.compareTo(minEdge) < 0) {
                    minEdge = childEdge;
                }
            }
            return minEdge;
        }
    }

    public ArrayList<GSpanEdge> getEdgeSeq() {
        return edgeSeq;
    }

    public void setEdgeSeq(ArrayList<GSpanEdge> edgeSeq) {
        this.edgeSeq = edgeSeq;
    }

    public Set<Integer> getNodes() {
        return nodeLabelMap.keySet();
    }

    public Integer getMaxNodeId() {
        return maxNodeId;
    }

    /**
     * 获得DFScode中 节点的标签
     *
     * @param nodeId DFScode 中 节点id
     * @return
     */
    public Integer getNodeLabel(Integer nodeId) {
        return this.nodeLabelMap.get(nodeId);
    }

    public Map<Integer, Integer> getNodeLabelMap() {
        return nodeLabelMap;
    }

    public void setNodeLabelMap(Map<Integer, Integer> nodeLabelMap) {
        this.nodeLabelMap = nodeLabelMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DFScode dfScode = (DFScode) o;
        return Objects.equals(edgeSeq, dfScode.edgeSeq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edgeSeq);
    }

    @Override
    public String toString() {
        return "DFScode{" +
                "edgeSeq=" + edgeSeq +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DFScode dfScodeCloned = (DFScode) super.clone();
        dfScodeCloned.nodeLabelMap = new HashMap<>(this.nodeLabelMap);
        dfScodeCloned.edgeSeq = new ArrayList<>(this.edgeSeq);
        return dfScodeCloned;
    }

    public static void main(String[] args) throws Exception {
/*        DFScode dfScode = new DFScode(new GSpanEdge(1, 2, 1, 1, 1, 1));
        //1

        dfScode.addEdge(new GSpanEdge(2, 3, 1, 2, 1, 1));
        //2

        dfScode.addEdge(new GSpanEdge(3, 1, 2, 1, 1, 1));
        //3

        dfScode.addEdge(new GSpanEdge(2, 4, 1, 3, 1, 1));
        //4

        dfScode.addEdge(new GSpanEdge(4, 1, 3, 1, 1, 1));
        //5

        dfScode.addEdge(new GSpanEdge(1, 5, 1, 3, 1, 1));
        //6

        dfScode.addEdge(new GSpanEdge(5, 6, 3, 4, 1, 1));
        //7

        //dfScode.addEdge(new GSpanEdge(6, 1, 4, 1, 1, 1));
        //8

        //dfScode.addEdge(new GSpanEdge(5, 7, 3, 1, 1, 1));
        //9

        //dfScode.addEdge(new GSpanEdge(7, 8, 1, 2, 1, 1));
        //10

        DFScode dfScode1 = new DFScode(new GSpanEdge(1, 2, 1, 1, 1, 1));
        //1

        dfScode1.addEdge(new GSpanEdge(2, 3, 1, 2, 1, 1));
        //2

        dfScode1.addEdge(new GSpanEdge(3, 1, 2, 1, 1, 1));
        //3

        dfScode1.addEdge(new GSpanEdge(2, 4, 1, 3, 1, 1));
        //4

        dfScode1.addEdge(new GSpanEdge(4, 1, 3, 1, 1, 1));
        //5

        dfScode1.addEdge(new GSpanEdge(1, 5, 1, 3, 1, 1));
        //6

        dfScode1.addEdge(new GSpanEdge(5, 6, 3, 4, 1, 1));
        //7

        dfScode1.addEdge(new GSpanEdge(6, 1, 4, 1, 1, 1));
        //8
        DFScode dfScode2 = new DFScode(new GSpanEdge(1, 2, 1, 1, 1, 1));
        //1

        dfScode2.addEdge(new GSpanEdge(2, 3, 1, 2, 1, 1));
        //2

        dfScode2.addEdge(new GSpanEdge(3, 1, 2, 1, 1, 1));
        //3

        dfScode2.addEdge(new GSpanEdge(2, 4, 1, 3, 1, 1));
        //4

        dfScode2.addEdge(new GSpanEdge(4, 1, 3, 1, 1, 1));
        //5

        dfScode2.addEdge(new GSpanEdge(3, 5, 1, 3, 1, 1));
        //6

        dfScode2.addEdge(new GSpanEdge(5, 6, 3, 4, 1, 1));
        //7

        dfScode2.addEdge(new GSpanEdge(6, 1, 4, 1, 1, 1));
        //8


        System.out.println(dfScode.isParentOf(dfScode1));
        System.out.println(dfScode.isParentOf(dfScode));
        System.out.println(dfScode.isParentOf(dfScode2));
        GSpanEdge gSpanEdge1 = new GSpanEdge(6, 5, 3, 1, 1, 1);
        GSpanEdge gSpanEdge2 = new GSpanEdge(6, 1, 4, 1, 1, 1);
        System.out.println(gSpanEdge1.equals(gSpanEdge2));
        *//*        dfScode.saveToFile("test.json", true);
        DFScode dfScode1 = DFScode.readFromFile("test.json");*/

        //String dirPath = "D:\\April9\\R_0.8\\P_all";
        //DFScode.removeDupDumpReadable(dirPath,"C:\\bioportal.sqlite",true);
        String dirPath = "D:\\result_April14\\R_1T_0.04\\";
        DFScode.removeDupDumpReadable(dirPath,"C:\\bioportal_full.sqlite",false);
    }
}
