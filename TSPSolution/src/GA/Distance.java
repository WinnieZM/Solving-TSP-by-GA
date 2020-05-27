package GA;

import java.io.BufferedReader;
import java.io.File;
import java.util.Random;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 城市距离类，保存城市间的距离，初始化后不再改变
 *
 */
public class Distance {

    private static double distance[][];
    private int num;
    Random random = new Random();
    private int min = 2;
    private int max = 100;

    public Distance(int num, boolean ifDuichen) throws IOException {
        this.num = num;
        initDistance(ifDuichen);
    }

    private void initDistance(boolean ifDuichen) throws IOException {
    	String a;
    	int[] x=new int[num];//存放x坐标的数组
		int[] y=new int[num];//存放y坐标的数组
        distance = new double[num][num];
        File file = new File("/Users/huichen/Desktop/algorithm/att48.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        for(int i=0;i<num;i++) {
			a=in.readLine();//读入一行数据
			String[] b=a.split(" ");//分隔出每个值
			x[i]=Integer.valueOf(b[1]);//x坐标数组
			y[i]=Integer.valueOf(b[2]);//y坐标数组
		}
		in.close();
        //distance是一个对称阵，且对角元素设为无穷大；对角线元素不会被用到，如果算法正确
        for (int i = 0; i < num; i++) {
            if (ifDuichen) {
                for (int j = i; j < num; j++) {
                    if (i == j) {
                        distance[i][j] = 0;
                    } else {
                    	distance[i][j] = distance[j][i] = Math.sqrt((x[i]-x[j])*(x[i]-x[j])+(y[i]-y[j])*(y[i]-y[j]));//计算欧式距离
                        //distance[i][j] = distance[j][i] = min + ((max - min) * random.nextDouble()); //产生2-100之间的随机浮点数
                    }
                }
            } else {  //不对称情况的矩阵产生
                for (int j = 0; j < num; j++) {
                    if (i == j) {
                        distance[i][j] = Double.MAX_VALUE;
                    } else {
                        distance[i][j] = min + ((max - min) * random.nextDouble()); //产生2-100之间的随机浮点数
                    }
                }

            }
        }
        printlndistance();
    }

    //返回该位置的距离值
    public static double getDistance(int i, int j) { //注意i,j和城市之间的对应关系
        return distance[i][j];
    }

    //打印距离矩阵
    private void printlndistance() {
        System.out.printf("%5s","");
        for (int i = 0; i < num; i++) {
            System.out.printf("%5s",i);
        }
        System.out.println();
        for (int i = 0; i < num; i++) {
            System.out.printf("%5s",i);
            for (int j = 0; j < num; j++) {
                System.out.printf("%5s",(int)distance[i][j]);
            }
            System.out.println();
        }
    }

}
