package ag.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 一致性哈希算法
 *
 * @author : huangxianguo@weconex.com
 * @since : 2019-07-02 15:27
 */
public class ConsistentHash {


    /** 均衡因子数 */
    private int virtualFactor;

    /** 用来记录节点分布的信息 */
    private TreeMap<Integer,Node> ring;


    public ConsistentHash(int virtualFactor) {
        this.virtualFactor = virtualFactor<1?1:virtualFactor;
        this.ring = new TreeMap<>();
    }


    /**
     *
     * TODO :   模拟系统在初始化的时候，注册现有节点
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 15:31
     */
    public void initNode(List<Node> nodes){

        // 通过节点的ip来做hash取值
        for (Node node : nodes) {

            addFakeNode(node);

            int hashCode = hash(node.getIp());
            ring.put(hashCode, node);
        }

    }


    /**
     *
     * TODO : 模拟添加虚拟节点
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 16:04
     */
    private  void addFakeNode(Node node){
        for (int i = 1; i <= virtualFactor; i++) {

            Node fake = new Node(node.getIp()+i,node.getName()+"#"+i);
            fake.setRef(node);
            int hashCode = hash(fake.getIp());
            ring.put(hashCode, fake);
        }
    }

    /**
     *
     * TODO :   模拟添加节点
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 16:02
     */
    public void addNode(Node node){

        addFakeNode(node);

        int hashCode = hash(node.getIp());
        ring.put(hashCode, node);

        info();
    }


    /**
     *
     * TODO :  模拟移除节点，这里移除的节点一定是物理节点
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 22:39
     */
    public void removeNode(Node node){

        int hashCode = hash(node.getIp());
        Node removeNode = ring.remove(hashCode);
        if(removeNode!=null){
            // 移除物理节点后，同时需要将虚拟节点也一并移除
            List<Integer> removeCodes = ring.values().stream().filter(n -> {
                Node ref = n.getRef();
                return ref != null && ref.equals(node);
            }).map(Node::hashCode)
                    .collect(Collectors.toList());
            for (Integer code : removeCodes) {
                ring.remove(code);
            }
        }
        info();

    }

    /**
     *
     * TODO : 模拟输出当前节点环的信息
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 16:27
     */
    public void info(){

        StringBuilder builder = new StringBuilder();
        builder.append("当前节点数为: ").append(ring.size()).append("\n");
        builder.append("分别为: ").append("\n");
        for (Map.Entry<Integer, Node> entry : ring.entrySet()) {
            Node node = entry.getValue();
            builder.append("Node: ")
                    .append(entry.getKey())
                    .append("  ip: ")
                    .append(node.getIp())
                    .append("  name: ")
                    .append(node.getName())
                    .append(" ")
                    .append(node.getRef()!=null?"虚拟节点":"物理节点")
                    .append(node.getRef()!=null?"\n":"  cache size: "+node.size()+"\n");
        }

        System.out.println(builder);
    }


    /**
     *
     * TODO : 模拟查找节点
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 16:22
     */
    public Node findNode(String key){

        int hashCode = hash(key);

        System.out.println("key "+key+" 对应的hashCode " + hashCode);
        // 这里找出比当前hash值大的map
        SortedMap<Integer, Node> sortedMap = ring.tailMap(hashCode);
        if(!sortedMap.isEmpty()){
            return ring.get(sortedMap.firstKey());
        }
        // 如果找不到的话，就默认取第一个节点，这个目的是为了实现闭环
        return ring.firstEntry().getValue();
    }


    /**
     *
     * TODO : 模拟对外提供缓存服务
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 16:24
     */
    public void cache(String key,Object value){

        Node node = findNode(key);
        Node ref = node.getRef();
        if(ref!=null){
            ref.cache(key, value);
            System.out.println("当前数据将存放在节点 " + ref.toString());
        }else{
            node.cache(key,value);
            System.out.println("当前数据将存放在节点 " + node.toString());
        }
    }


    /**
     *
     * TODO : 模拟对外提供查找缓存服务
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 16:24
     */
    public Object get(String key){

        Node node = findNode(key);

        System.out.println("\n当前数据可能存放在节点      " + node.toString());

        return node.get(key);
    }


    /**
     *
     * TODO : 缓存节点
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 15:29
     */
    private static class Node{

        private Node ref;

        private String ip;

        private String name;

        private Map<String,Object> cache;

        public Node(String ip, String name) {
            this.ip = ip;
            this.name = name;
            this.cache = new HashMap<>();
        }

        public Node(String ip) {
            this.ip = ip;
        }

        public void setRef(Node ref) {
            this.ref = ref;
        }

        public Node getRef() {
            return ref;
        }

        public String getIp() {
            return ip;
        }

        public String getName() {
            return name;
        }


        /**
         *
         * TODO : 模拟在每个节点上进行缓存数据
         * @author : huangxianguo@weconex.com
         * @since : 2019-07-02 22:35
         */
        public void cache(String key,Object value){
            cache.put(key, value);
        }

        public int size(){
            return cache.size();
        }
        /**
         *
         * TODO : 模拟在指定节点上根据key来获取数据
         * @author : huangxianguo@weconex.com
         * @since : 2019-07-02 22:36
         */
        public Object get(String key){
            return cache.get(key);
        }

        @Override
        public String toString() {
            return "Node{" +
                    "ip='" + ip + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
           if(o==null || o.getClass() != Node.class){
               return false;
           }
           return this.hashCode() == o.hashCode();
        }

        @Override
        public int hashCode() {
            return hash(ip);
        }
    }





    /**
     *
     * TODO :  通过 FNVHash1 算法来实现计算hash值
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 22:33
     */
    public static int hash(String data) {
        // 这里再次计算md5的值，目的是使下面计算的字符串长度都是一致的
        data = md5(data);
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < data.length(); i++) {
            hash = (hash ^ data.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return Math.abs(hash);
    }


    /**
     *
     * TODO : 计算md5值
     * @author : huangxianguo@weconex.com
     * @since : 2019-07-02 22:35
     */
    public static String md5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



    public static void main(String[] args) {

        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node("127.0.0.1","node1"));
        nodes.add(new Node("127.0.0.2","node2"));
        nodes.add(new Node("127.0.0.3","node3"));
        nodes.add(new Node("127.0.0.4","node4"));

        ConsistentHash ag = new ConsistentHash(2);
        ag.initNode(nodes);

        ag.info();

        ag.addNode(new Node("127.0.0.5", "node5"));

        ag.cache("abc", "abc");
        ag.cache("123", 123);
        ag.cache("sfasf", "sfasf");
        ag.cache("abcdefg", "sfasf");

        System.out.println("从缓存中取出的值是 "+ ag.get("123"));


        ag.addNode(new Node("127.0.0.6", "node6"));
        ag.cache("123", 123);
        System.out.println("从缓存中取出的值是 "+ ag.get("123"));

        ag.removeNode(new Node("127.0.0.5"));

    }

}



