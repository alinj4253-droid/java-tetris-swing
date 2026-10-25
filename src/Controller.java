import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Controller implements KeyListener {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        view.getGame_main().addKeyListener(this);
        setupListeners();
    }

    // 设置按钮监听器
    /*
    1.e是一个ActionEvent对象，表示按钮被点击
    2.ActionListener接口是一个函数式接口，只有一个抽象方法actionPerformed()，表示按钮被点击后的回调函数。
    public interface ActionListener extends EventListener {  public void actionPerformed(ActionEvent e); }
    * */
    private void setupListeners() {
        view.getCommonButton().addActionListener(e -> onCommonMode());
        view.getDifficultyButton().addActionListener(e -> onDifficultyMode());
        view.getPauseButton().addActionListener(e -> onPauseGame());
        view.getCloseMusicButton().addActionListener(e -> onToggleMusic());
        view.getExitButton().addActionListener(e -> onExitGame());
    }

    //普通模式
    private void onCommonMode() {
        if (model.getTime() < 1000) {
            model.setTime(model.getTime() + 250);
        }
        view.getLabel1().setText("游戏状态: 普通模式!");

        //使游戏主面板获得焦点，确保用户可以与游戏交互
        /*
        获得焦点：指让某个组件成为当前窗口中可以接收键盘输入的组件
         */
        view.getGame_main().requestFocusInWindow();
    }



    //困难模式
    private void onDifficultyMode() {
        if(model.getState()==0) {
            model.setState(1);
            model.setTime(model.getTime() - 300);
            Random random = new Random();
            for (int i = 0; i < 4; i++) {
                int randomRect_x = random.nextInt(15) + 15;
                int randomRect_y = random.nextInt(15);//随机生成一个[0,15)的随机整数
                //判断随机生成的坐标是否为空，若为空则设置背景色为黑色，并设置数据为2
                if (model.getDataSpecific(randomRect_x, randomRect_y) == 0) {
                    model.getText()[randomRect_x][randomRect_y].setBackground(Color.black);
                    model.setDataSpecific(randomRect_x, randomRect_y, 2);
                } else {
                    i--;
                }
            }
            view.getLabel1().setText("游戏状态: 困难模式!");
            view.getGame_main().requestFocusInWindow();
        }
    }

    //游戏暂停 or 游戏继续
    private void onPauseGame() {
        if (!Model.isrunning) {
            return;
        }
        if (model.isGame_pause()) {
            model.setGame_pause(false);
            model.openMusic();
            view.getLabel1().setText("游戏状态: 正在进行中!");
        } else {
            model.setGame_pause(true);
            model.stopMusic();
            view.getLabel1().setText("游戏状态: 暂停中!");
        }
        view.getGame_main().requestFocusInWindow();
    }

    //关闭音效  or 开启音效
    private void onToggleMusic() {
        if (model.getBgMusic().isRunning()) {
            model.stopMusic();
        } else {
            model.openMusic();
        }
        view.getGame_main().requestFocusInWindow();
    }

    //退出游戏
    private void onExitGame() {
        int confirm = JOptionPane.showConfirmDialog(null, "确定要退出游戏吗？", "确认退出", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //控制游戏开始
        //控制游戏暂停
        if (e.getKeyChar() == 'p') {
            //判断游戏是否结束
            if (!Model.isrunning) {
                return;
            }
            model.setPause_times(model.getPause_times()+1);
            //判断按下一次,暂停游戏
            if (model.getPause_times()== 1) {
                model.setGame_pause(true);
                model.stopMusic();
                View.label1.setText("游戏状态: 暂停中!");
            }
            //判断按下两次,继续游戏
            if (model.getPause_times() == 2) {
                model.setGame_pause(false);
                model.setPause_times(0);
                model.openMusic();
                View.label1.setText("游戏状态: 正在进行中!");
            }
        }
        //控制方块进行变形
        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            if (!Model.isrunning) {
                return;
            }
            if (model.isGame_pause()) {
                return;
            }
            //定义变量,存储目前方块的索引
            int old;
            for (old = 0;old < model.getAllRect().length;old++) {
                //判断是否是当前方块
                if (model.getRect() == model.getAllRect()[old]) {
                    break;
                }
            }
            //定义变量,存储变形后方块
            int next;
            //判断是方块
            if (old == 0 || old == 7 || old == 8 || old == 9) {
                return;
            }
            //清除当前方块
            model.clear(model.getX(),model.getY());
            if (old == 1 || old == 2) {
                next = model.getAllRect()[old == 1 ? 2 : 1];

                if (canTurn(next,model.getX(),model.getY())) {
                    model.setRect(next);
                }
            }
            if (old >= 3 && old <= 6) {
                next = model.getAllRect()[old + 1 > 6 ? 3 : old + 1];

                if (canTurn(next, model.getX(), model.getY())) {
                    model.setRect(next);
                }
            }

            if (old == 10 || old == 11) {
                next = model.getAllRect()[old == 10 ? 11 : 10];

                if (canTurn(next,model.getX(),model.getY())) {
                    model.setRect(next);
                }
            }

            if (old == 12 || old == 13) {
                next = model.getAllRect()[old == 12 ? 13 : 12];

                if (canTurn(next,model.getX(),model.getY())) {
                    model.setRect(next);
                }
            }

            if (old >= 14 && old <= 17) {
                next = model.getAllRect()[old + 1 > 17 ? 14 : old + 1];

                if (canTurn(next,model.getX(),model.getY())) {
                    model.setRect(next);
                }
            }

            if (old == 18 || old == 19) {
                next = model.getAllRect()[old == 18 ? 19 : 18];

                if (canTurn(next,model.getX(),model.getY())) {
                    model.setRect(next);
                }
            }

            if (old == 20 || old == 21) {
                next = model.getAllRect()[old == 20 ? 21 : 20];

                if (canTurn(next,model.getX(),model.getY())) {
                    model.setRect(next);
                }
            }

            //重新绘制变形后方块
            model.draw(model.getX(),model.getY());
        }
    }
    //判断方块此时是否可以变形的方法
    public boolean canTurn(int a,int m,int n) {
        //创建变量
        int temp = 0x8000;
        //遍历整个方块
        for (int i = 0;i < 4;i++) {
            for (int j = 0;j < 4;j++) {
                if ((a & temp) != 0) {
                    if (model.getData()[m][n] == 1) {
                        return false;
                    }
                }
                n++;
                temp >>= 1;
            }
            m++;
            n = n -4;
        }
        //可以变形
        return true;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        //方块进行左移
        if (e.getKeyCode() == 37) {
            //判断游戏是否结束
            if (!Model.isrunning) {
                return;
            }
            //判断游戏是否暂停
            if (model.isGame_pause()) {
                return;
            }
            //方块是否碰到左墙壁
            if (model.getY() <= 1) {
                return;
            }
            //定义一个变量
            int temp = 0x8000;
            //判断左边途中是否碰到别的方块
            for (int i = model.getX();i < model.getX() +4;i++) {
                for (int j = model.getY();j < model.getY() + 4;j++) {
                    if ((temp & model.getRect()) != 0) {
                        if (model.getData()[i][j-1] == 1) {
                            return;
                        }
                    }
                    temp >>= 1;
                }
            }
            //首先清除目前方块
            model.clear(model.getX(),model.getY());
            model.setY(model.getY()-1);
            model.draw(model.getX(),model.getY());
        }
        //方块进行右移
        if (e.getKeyCode() == 39) {
            //判断游戏是否结束
            if (!Model.isrunning) {
                return;
            }
            //判断游戏是否暂停
            if (model.isGame_pause()) {
                return;
            }
            //定义变量
            int temp = 0x8000;
            int m = model.getX();
            int n = model.getY();
            //存储最右边的坐标值
            int num = 1;
            for (int i = 0;i < 4;i++) {
                for (int j = 0;j < 4;j++) {
                    if ((temp & model.getRect()) != 0) {
                        if (n > num) {
                            num = n;
                        }
                    }
                    n++;
                    temp >>= 1;
                }
                m++;
                n = n - 4;
            }
            //判断是否碰到右墙壁
            if (num >= (Model.game_y-2)) {
                return;
            }
            //方块右移途中是否碰到别的方块
            temp = 0x8000;
            for (int i = model.getX();i < model.getX() + 4;i++) {
                for (int j = model.getY();j < model.getY() + 4;j++) {
                    if ((temp & model.getRect()) != 0) {
                        if (model.getData()[i][j+1] == 1) {
                            return;
                        }
                    }
                    temp >>= 1;
                }
            }
            //清除当前方块
            model.clear(model.getX(),model.getY());

            model.setY(model.getY()+1);

            model.draw(model.getX(),model.getY());
        }
        //方块进行下落
        if (e.getKeyCode() == 40) {
            //判断游戏是否结束
            if (!Model.isrunning) {
                return;
            }
            //判断游戏是否暂停
            if (model.isGame_pause()) {
                return;
            }
            //判断方块是否可以下落
            if (!model.canFall(model.getX(),model.getY())) {
                return;
            }
            model.clear(model.getX(),model.getY());
            //改变方块的坐标
            model.setX(model.getX()+1);
            model.draw(model.getX(),model.getY());
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}


}
