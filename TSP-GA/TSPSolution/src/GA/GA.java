package GA;

import java.io.IOException;
import java.util.Random;

/**
 * 遗传算法
 *
 */
public class GA {

    //确定种群规模、迭代次数、个体选择方式、交叉概率、变异概率等
    private int entitysize = 1000;//种群规模
    private int p = 10000;//迭代次数
    private double p_bianyi = 0.002;//变异概率
    private double p_jiaopei = 0.9;//交配概率
    private int citynum = 5;//城市数量
    private GAEntity[] gaEntity;
    private GAEntity[] tempEntity;
    private double all_ability;
    private MatchTable matchTable;
    private GAEntity bestEntity;
    private static double shortestRoad;

    //初始化城市（随机产生城市坐标）
    private void Init_Distance() throws IOException {
        new Distance(citynum,true);
    }

    //初始化种群（随机产生100种的路径），暂不检测路径间的重复问题,因为重复的可能太小
    private void Init_GAEntity() {
        gaEntity = new GAEntity[entitysize];
        tempEntity = new GAEntity[entitysize];
        for (int i = 0; i < entitysize; i++) {
            gaEntity[i] = new GAEntity(citynum, "");
            System.out.println("---------初始种群" + i + "---------");
            gaEntity[i].printRoad();
            System.out.println();
        }
    }

    /**
     * 当P<迭代次数时
     */
    //计算染色体适应度值(每个染色体的路径总和）和幸存程度
    private void Cal_AdaptabilityAndLucky() {
        all_ability = 0.0;
        double all_lucky = 0.0;
        //计算路径总长
        for (int i = 0; i < entitysize; i++) {
            all_ability += gaEntity[i].cal_Adaptability();
        }
        //计算总的幸存程度，为归一化做准备
        for (int i = 0; i < entitysize; i++) {
            all_lucky += gaEntity[i].cal_preLucky(all_ability);     
        }
        //计算每条路径的经过归一化的幸存程度，为选择做准备
        for (int i = 0; i < entitysize; i++) {
            gaEntity[i].cal_Lucky(all_lucky);
        }
    }
    
 //-------------------------------------------------------  
    
    private  void  chooseSample( ) {
    	double[] cus = new double[entitysize+1];
    	
    	cus[0]=0.0;
    	for(int i=0 ; i < entitysize ;i++)	//轮盘赌累加器
    		cus[i+1]=cus[i]+gaEntity[i].getP_lucky();    	
    	
    	tempEntity[0]=bestEntity;
    	for(int p=1;p<entitysize;p++) {
    		double rand =Math.random();
    		for(int r=0;r<entitysize;r++) {
    			if(rand>cus[r] && rand <=cus[r+1]) {
    				tempEntity[p]=gaEntity[r];
//    				tempEntity[p]=bestEntity;
    			}
    		}
    	}
    	for (int i = 0; i < entitysize; i++) 
            gaEntity[i] = tempEntity[i];
    }

//------------------------------------------------------------
//    
//    //按某个选择概率选择样本,使用轮盘赌选择法，根据幸存程度选择,本质是重构解空间，解空间样本可重复
//    private void chooseSample() {
//        double p = 0.0;
//        double all_prelucky = 0.0;
//        for (int i = 0; i < entitysize; i++) {
//            p = Math.random();//产生0到1之间的随机数
//            all_prelucky = 0.0;
//            tempEntity[i] = gaEntity[entitysize - 1];//提高精确度
//            for (int j = 0; j < entitysize; j++) {
//                all_prelucky += gaEntity[j].getP_lucky();
////                  System.out.println("正在更新："+"j:"+  all_prelucky);
//                if (p <= all_prelucky) {
////                     System.out.println("找到位置时："+  all_prelucky);
//                    tempEntity[i] = gaEntity[j];
//                    break;
//                }
//            }
//        }
//        //更新解空间
//        for (int i = 0; i < entitysize; i++) {
//            gaEntity[i] = null;
//            gaEntity[i] = tempEntity[i];
////            System.out.println("样本选择后" + i + ":" + gaEntity[i].printRoad());
//        }
//    }

    //个体交叉,采用部分匹配法PMX
    private void Mating() {
//        System.out.println("进入交叉：");
        double mating[] = new double[entitysize];//染色体的交配概率
        boolean matingFlag[] = new boolean[100000];//染色体的可交配情况
        boolean findMating1 = false;
        Random random = new Random();
        matchTable = new MatchTable(citynum);
        int mating1 = 0;
        int mating2 = -1;//指示当前交配的两个对象
        int position1, position2;//指示交换位置
        int matingnum = 0;	//参与交配的染色体数
        
        //随机产生交配概率,确定可交配的染色体
        for (int i = 0; i < entitysize; i++) {
            mating[i] = Math.random();
            if (mating[i] < p_jiaopei) {
                matingFlag[i] = true;
                matingnum++;
            } else {
                matingFlag[i] = false;
            }
        }
//        System.out.println("交叉准备工作已完毕");
        matingnum = matingnum / 2 * 2;//参与交配的染色体数应该是偶数
        for (int i = 0; i < matingnum / 2; i++) {
            findMating1 = false;
            position1 = random.nextInt(citynum);	//生成0-citynum之间的随机数
            position2 = random.nextInt(citynum);
            //在position1和position2之间进行交叉
            if (position1 <= position2) {

            } else {	
                int t = position1;
                position1 = position2;
                position2 = t;
            }
            //寻找两个可交配的染色体
            for (mating2++; mating2 < entitysize; mating2++) {
//                System.out.println("开始寻找两个可交配的染色体");
                if (matingFlag[mating2]) {
                    if (findMating1) {
                        break;//已经找到mating1和mating2
                    } else {
                    	//将找到第一个可以交配的染色体的位置放在mating1上
                    	//而mating2要通过 if (matingFlag[mating2])来寻找（for循环有+1操作）
                        mating1 = mating2;
                        findMating1 = true;
                    }
                }
            }
            //这两个染色体进行交配（部分匹配法）
            //gaEntity[mating1]和gaEntity[mating2]在position1和position2上进行交叉
            //先构建匹配表
            matchTable.setTable(gaEntity[mating1], gaEntity[mating2], position1, position2);
            //进行交叉操作
            GAEntity tempGaEntity1 = new GAEntity(citynum);//子代1
            GAEntity tempGaEntity2 = new GAEntity(citynum);//子代2
            //首先插入交叉部分的值
            //然后插入首尾值
            if (!gaEntity[mating1].checkdifference(gaEntity[mating2])) {
                tempGaEntity1 = gaEntity[mating1];
                tempGaEntity2 = gaEntity[mating2];
            } else {
                //相当于将position1、2位置之间的基因按照映射原则填入
                tempGaEntity1.setRoad(gaEntity[mating2], position1, position2);
                tempGaEntity2.setRoad(gaEntity[mating1], position1, position2);
                //子代染色体其他基因的位置开始进行匹配
                tempGaEntity1.modifyRoad(gaEntity[mating1], position1, position2, matchTable, true);
                tempGaEntity2.modifyRoad(gaEntity[mating2], position1, position2, matchTable, false);
            }

            gaEntity[mating1] = tempGaEntity1;
            gaEntity[mating2] = tempGaEntity2;
            }
    }

    //个体变异,采用简单的交换变异
    private void Variating() {
//        System.out.println("进入变异");
        double rating[] = new double[entitysize];//染色体的变异概率
        boolean ratingFlag[] = new boolean[entitysize];//染色体的可变异情况
        Random random = new Random();
        int position1, position2;//指示交换位置
        //随机产生变异概率,确定可变异的染色体
        for (int i = 0; i < entitysize; i++) {
            rating[i] = Math.random();
            if (rating[i] < p_bianyi) {
                ratingFlag[i] = true;
            } else {
                ratingFlag[i] = false;
            }
        }
        //开始变异
        for (int i = 0; i < entitysize; i++) {
            if (ratingFlag[i]) {
                position1 = 0;
                position2 = 0;
                while (position1 == position2) {
                    position1 = random.nextInt(citynum);
                    position2 = random.nextInt(citynum);
                }
                gaEntity[i].exchange(position1, position2);
            }
        }
    }

    /**
     * 迭代结束
     */
    private void ChooseBestSolution(Boolean initBest) {
//        System.out.println("进入路径选择");
        Double roadLength = Double.MAX_VALUE;
        int bestRoad = 0;
        for (int i = 0; i < entitysize; i++) {
            if ( gaEntity[i].getAdaptability() < roadLength ) {
                roadLength = gaEntity[i].getAdaptability();
                bestRoad = i;
            }
        }
       
        if (initBest) {
        	System.out.println("    初始迭代");
            shortestRoad = roadLength;
            bestEntity = gaEntity[bestRoad];
            System.out.println("该次迭代最好的路径：");
            gaEntity[bestRoad].printRoad();
            System.out.println( );
            System.out.println("  该次迭代最低消耗：" + roadLength);
        } else if (shortestRoad > roadLength) {
        	System.out.println("    有效迭代");
            shortestRoad = roadLength;
            bestEntity = gaEntity[bestRoad];
            System.out.println("该次迭代最好的路径：");
            gaEntity[bestRoad].printRoad();
            System.out.println( );
            System.out.println("  该次迭代最低消耗：" + roadLength);
        } else{
        	 System.out.println("   本次迭代失败 " );
        }
    }

    private void Iterator() throws IOException {
        Init_Distance();
        Init_GAEntity();
        boolean initBest = true;
        bestEntity = gaEntity[0];
        for (int i = 0; i < p; i++) {
            System.out.println("--------第" + i + "次迭代--------");
            Cal_AdaptabilityAndLucky();
            ChooseBestSolution(initBest);
            initBest = false;
            chooseSample();
            Mating();
            Variating();
        }
        System.out.println();
        System.out.println("---------end---------");
        System.out.println("最好的路径：" );
        bestEntity.printRoad();
        System.out.println();
        System.out.println("  最低消耗：" + shortestRoad);
    }

    //打印适应度最高的解
    public static void main(String[] args) throws IOException {
//    	int[] a=new int[20];
//    	.length;i++) {
    	long startTime = System.currentTimeMillis();
        GA ga = new GA();
        ga.Iterator();
        long endTime = System.currentTimeMillis(); 
        long time = endTime-startTime;
		System.out.println("程序运行时间："+ time +"");
//        a[i]=(int)shortestRoad;
//    	}
//    	for(int i=0;i<a.length;i++) {
//    		System.out.print(a[i]+" ");
//    	}
    }
}
