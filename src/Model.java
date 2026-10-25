import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Random;

public class Model extends JFrame {
    // 游戏的行数30,列数15
    public static final int game_x = 30;
    public static final int game_y = 15;
    //存放背景音乐
    private Clip bgMusic;
    // 二维数组，记录方格的属性，0:没有方格 1：有方格 2：特殊方格
    private int[][] data;
    // 用于存储所有的方块的数组
    private int[] allRect;
    // 用于存储当前方块的变量
    private int rect;
    // 线程的休眠时间
    private int time = 900;
    // 方块当前的位置
    private int x, y;
    // 计算得分
    private int score = 0;
    // 用于判断游戏是否结束
    public static boolean isrunning = true;
    // 困难模式状态码  当state为1时，说明游戏模式此时为困难模式
    private int state = 0;
    // 定义一个标志变量,用于判断游戏是否暂停
    private boolean game_pause = false;
    //文本域数组,背景方格
    private JTextArea[][] text;
    // 定义一个变量用于记录按下暂停键的次数
    private int pause_times = 0;
    private boolean bgMusicPlaying = false;

    public Model() {
        isrunning = true;
        text = new JTextArea[game_x][game_y];
        data = new int[game_x][game_y];
        allRect = new int[]{0x00cc, 0x8888, 0x000f, 0x888f, 0xf888, 0xf111, 0x111f, 0x0eee, 0xffff, 0x0008, 0x0888, 0x000e,
                0x0088, 0x000c, 0x08c8, 0x00e4, 0x04c4, 0x004e, 0x08c4, 0x006c, 0x04c8, 0x00c6};
        openMusic();
    }

    //播放音乐
    //加载并播放名为“music.wav"的音频文件作为背景音乐，并使其连续循环播放
    public void openMusic() {
        try {
            //获取音频输入流
            File bgMusicFile = new File("music.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bgMusicFile);
            //获取Clip对象
            bgMusic = AudioSystem.getClip();
            //打开Clip并读取音频数据
            bgMusic.open(audioStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);//Clip.LOOP_CONTINUOUSLY常量表示音频流是循环播放的
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //关闭音乐
    public void stopMusic(){
        if(bgMusic!=null&&bgMusic.isRunning()){
            bgMusic.stop();
            bgMusic.close();
        }
    }

    public void game_begin() {
        while (true) {
            //判断游戏是否结束
            if (!isrunning) {
                System.out.println("！！！游戏结束！！！");
                break;
            }
            System.out.println("！！！游戏开始！！！");
            game_run();
        }
        //在标签位置显示"游戏结束"
        View.label1.setText("游戏状态: 游戏结束!");
        isrunning=true;
        new View.WelcomeWindow(this, View.label5).setVisible(true);
        this.setVisible(false);
    }

    //随机生成下落方块形状的方法
    public void ranRect() {
        Random random = new Random();
        rect = allRect[random.nextInt(22)];
    }
    //游戏运行的方法
    public void game_run() {
        ranRect();
        x = 0;y = 5;
        for (int i = 0;i < game_x;i++) {
            try {
                Thread.sleep(time);
                if (game_pause) {
                    i--;
                } else {
                    //判断方块是否可以下落
                    /*
                    方块不能下落：
                    方块可以下落：
                     */
                    if (!canFall(x,y)) {
                        changData(x,y);
                        //循环遍历4层,看是否有行可以消除
                        for (int j = x;j < x + 4;j++) {
                            int sum = 0;
                            for (int k = 1;k <= (game_y-2);k++) {
                                if (data[j][k] == 1) {
                                    sum++;
                                }
                            }
                            //判断是否有一行可以被消除
                            if (sum == (game_y-2)) {
                                //消除j这一行
                                removeRow(j);
                            }
                        }
                        //判断游戏是否失败
                        for (int j = 1;j <= (game_y-2);j++) {
                            if (data[3][j] == 1) {
                                isrunning = false;
                                stopMusic();
                                break;
                            }
                        }
                        break;
                    } else {
                        x++;//层数+1
                        fall(x,y);//方块下落一层
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //判断方块是否可以继续下落的方法
    public boolean canFall(int m,int n) {
        int temp = 0x8000;
        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                if ((temp & rect) != 0) {
                    //判断该位置的下一行是否有方块
                    if (data[m+1][n] == 1||data[m+1][n] == 2) {  return false; }
                }
                n++;//n 表示的是当前列的索引，每次n++是为了移动到下一列的位置，检查下一个方块单元
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
        return true;
    }
    //改变不可下降的方块对应的区域的值的方法
    public void changData(int m,int n) {
        int temp = 0x8000;
        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                if ((temp & rect) != 0) {
                    data[m][n] = 1;
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
    }
    //移除某一行的所有方块,令上方方块掉落的方法
    public void removeRow(int row) {
        int temp = 100;
        for (int i = row;i >= 1;i--) {
            for (int j = 1;j <= (game_y-2);j++) {
                data[i][j] = data[i-1][j];
            }
        }
        reflesh(row);
        //方块加速
        if (time > temp) {
            time -= (temp/4);
        }
        score += temp;
        //显示变化后的分数
        View.label.setText("游戏得分为: " + score);
    }
    //刷新移除某一行后的游戏界面的方法
    public void reflesh(int row) {
        //遍历row行以上的游戏区域
        for (int i = row;i >= 1;i--) {
            for (int j = 1;j <= (game_y-2);j++) {
                if (data[i][j] == 1) {
                    text[i][j].setBackground(Color.ORANGE);
                }else {
                    text[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }
    //方块向下掉落一层的方法
    public void fall(int m, int n) {
        if (m > 0) {
            // 清除上一层方块
            clear(m - 1, n);
        }
        // 重新绘制方块
        draw(m, n);
    }
    //方块掉落后,清除上一层有颜色的地方的方法
    public void clear(int m, int n) {
        int temp = 0x8000;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {
                    text[m][n].setBackground(Color.WHITE);
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
    }
    //重新绘制掉落后方块的方法
    public void draw(int m, int n) {
        int temp = 0x8000;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((temp & rect)!= 0) {
                    text[m][n].setBackground(Color.ORANGE);
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n - 4;
        }
    }

    public Clip getBgMusic() {
        return bgMusic;
    }

    public void setBgMusic(Clip bgMusic) {
        this.bgMusic = bgMusic;
    }

    public int[][] getData() {
        return data;
    }

    //获取Data数组特定位置的值
    public int getDataSpecific(int x, int y) {
        return data[x][y];
    }

    public void setData(int[][] data) {
        this.data = data;
    }

    //设置Data数组特定位置的值
    public void setDataSpecific(int x, int y, int value) {
        this.data[x][y] = value;
    }

    public int[] getAllRect() {
        return allRect;
    }

    public void setAllRect(int[] allRect) {
        this.allRect = allRect;
    }

    public int getRect() {
        return rect;
    }

    public void setRect(int rect) {
        this.rect = rect;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static boolean isIsrunning() {
        return isrunning;
    }

    public static void setIsrunning(boolean isrunning) {
        Model.isrunning = isrunning;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    public boolean isGame_pause() {
        return game_pause;
    }

    public void setGame_pause(boolean game_pause) {
        this.game_pause = game_pause;
    }

    public JTextArea[][] getText() {
        return text;
    }

    public void setText(JTextArea[][] text) {
        this.text = text;
    }

    public int getPause_times() {
        return pause_times;
    }

    public void setPause_times(int pause_times) {
        this.pause_times = pause_times;
    }

    public boolean isBgMusicPlaying() {
        return bgMusicPlaying;
    }

    public void setBgMusicPlaying(boolean bgMusicPlaying) {
        this.bgMusicPlaying = bgMusicPlaying;
    }
}