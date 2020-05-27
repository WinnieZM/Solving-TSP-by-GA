package GA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author weangdan
 */
/**
 * 遗传算法个体，采取基于路径的编码方式
 */
public class GAEntity {

//    private static Integer[] initRoad = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
//        10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
//        20, 21, 22, 23, 24, 25, 26, 27, 28, 29};
	 private static Integer[] initRoad = new Integer[]{0, 1, 2, 3, 4};
//		        10, 11, 12, 13, 14, 15, 16, 17, 18, 19};

    private int num;//城市个数
    private List<Integer> roadlist;
    private Integer[] road;
    private double adaptability = 0.0;//个体适应度
    private double p_lucky = 0.0;//幸存概率

    //创建一个num大小的road一维矩阵，来存储路径
    GAEntity(int n,String s) {    
        num = n;
        roadlist = new ArrayList<Integer>();
        road = new Integer[num];
        InitRoad();
    }

    GAEntity(int n) {
        num = n;
        roadlist = new ArrayList<Integer>();
        road = new Integer[num];
    }

    //road中存储的是城市的编号
    private void InitRoad() {//随机解
        roadlist = Arrays.asList(initRoad);  //asList(返回指定数组支持的固定大小列表)
        Collections.shuffle(roadlist);    //shuffle(list )将List中的内容随机打乱顺序
        road = (Integer[]) roadlist.toArray();  //将列表转换成数组
    }

    public void setRoad(int i, int j) {
        roadlist.set(i, j);
        road[i] = j;
    }

    public int getRoad(int i) {
        return road[i];
    }

    public double getAdaptability() {
        return adaptability;
    }

    public void setAdaptability(double adaptability) {
        this.adaptability = adaptability;
    }

    public double getP_lucky() {
        return p_lucky;
    }

    public void setP_lucky(double p_lucky) {
        this.p_lucky = p_lucky;
    }

    public String printRoad() {
        String p = "";
        for (int i = 0; i < num; i++) {
        	System.out.print("  " + (road[i]+1 )+ ";");
//            p += "  " + road[i] + ";";
        }
//        System.out.println();
//        System.out.print("  幸存概率："+p_lucky);
//        p+="幸存概率："+p_lucky;
        return p;
    }

    //计算本次路径的适应度
    public double cal_Adaptability() {
        adaptability = 0.0;
        for (int i = 0; i < num - 1; i++) {
            adaptability += Distance.getDistance(road[i], road[i + 1]);
        }
        adaptability +=Distance.getDistance(road[num-1], road[0]);
        return adaptability;
    }

    //计算每条路径的幸存程度
    public double cal_preLucky(double all_ability) {
        p_lucky = 1 - adaptability / all_ability;
        return p_lucky;
    }

    //对幸存度的值进行归一化
    public void cal_Lucky(double all_lucky) {
        p_lucky = p_lucky / all_lucky;
    }

    
    //相当于将position1、2位置之间的基因全部填入子代
    public void setRoad(GAEntity parent, int position1, int position2) {
        roadlist.clear();
        for (; position1 <= position2; position1++) {
        	//parent.getRoad(position1)相当于road[position1]
            road[position1] = parent.getRoad(position1);
            roadlist.add(road[position1]);//当前已有路径统计
        }
    }

    //子代染色体其余基因位置的匹配
    public void modifyRoad(GAEntity parent, int position1, int position2, MatchTable matchTable, boolean ifChild1) {
        int roadnum;
        boolean ifModify = false;
        if (ifChild1) {//子代1的查询表应该从父代2开始,最终值落在父代1中
//            System.out.println("开始插入首尾值：子代1");
            for (int i = 0; i < num; i++) {
            	/*如果i的位置位于position1和position2位置，则直接将i
            	 * 的值定位到position2位置，并跳出本次if判断
            	 */
                if (i >= position1 && i<=position2) {
                    i = position2;
                    continue;
                }
                roadnum = parent.getRoad(i);
                ifModify = checkRoad(roadnum);

                while (ifModify) {
//                   System.out.println("开始查找匹配表:"+roadnum );
                    roadnum = matchTable.getRoadNum(false, roadnum);
//                     System.out.println(""+roadnum );
                    ifModify = checkRoad(roadnum);
                }
                road[i] = roadnum;
                roadlist.add(roadnum);
            }
//             System.out.println("子代1处理结束");
        } else {//子代2的查询表应该从父代1开始
//            System.out.println("开始插入首尾值：子代2");
            for (int i = 0; i < num; i++) {
                if (i >= position1&& i<=position2) {
                    i = position2;
                    continue;
                }
                roadnum = parent.getRoad(i);
                ifModify = checkRoad(roadnum);

                while (ifModify) {
//                    System.out.println("开始查找匹配表:"+roadnum );
                    roadnum = matchTable.getRoadNum(true, roadnum);
//                     System.out.println(""+roadnum );
                    ifModify = checkRoad(roadnum);
                }

                road[i] = roadnum;
                roadlist.add(roadnum);

            }
        }
//         System.out.println("子代2处理结束");
    }

    //当前的得到的子代基因中是否存在配对
    private boolean checkRoad(int roadnum) {
    	
        if (roadlist.contains(roadnum)) {
            return true;
        }
        return false;
    }

    //变异——选择交换变异
    public void exchange(int p1, int p2) {
        int t = road[p1];
        road[p1] = road[p2];
        road[p2] = t;
    }
    
    //
    public boolean checkdifference(GAEntity g){
    	for(int i=0;i<num;i++){
    		if(road[i]==g.getRoad(i)){
    			continue;
    			} else {
    				return true;
    			}
    	}
        return false;
    }
    
}
