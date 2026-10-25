import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class View extends JFrame {

    private Model model;

    // 显示游戏分数的标签
    public static JLabel label;

    // 显示游戏状态的标签
    public static JLabel label1;

    public static String label4 = "开始游戏！";
    public static String label5 = "再来一局！";
    public static String label6 = "普通模式";
    public static String label7 = "困难模式";

    // 困难模式状态码
    int state = 0;

    public static String label8 = "游戏暂停（继续）";
    public static String label9 = "退出游戏";
    public static String label10 = "关闭（开启）音效";

    private JButton commonButton;
    private JButton difficultyButton;
    private JButton pauseButton;
    private JButton closeMusicButton;
    private JButton exitButton;

    public View(Model model) {
        this.model = model;
        new WelcomeWindow(this, label4).setVisible(true);
        label1 = new JLabel("游戏状态: 正在游戏中!");
        label = new JLabel("游戏得分为: 0");
        initGamePanel();
        initExplainPanel();
        initWindow();
        this.setVisible(true);
    }

    //游戏欢迎界面 窗口类 包括开始按钮和背景图片
    public static class WelcomeWindow extends JDialog {

        private JButton startButton;

        public WelcomeWindow(Frame owner, String label) {
            super(owner, "欢迎来到俄罗斯方块小游戏！", true);
            setSize(773, 591);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // 修改为dispose操作

            // 创建启动按钮
            startButton = new JButton(label);
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 关闭欢迎窗口
                    dispose();

                }
            });

            // 创建背景图片标签
            JLabel backgroundLabel = new JLabel();
            ImageIcon backgroundImage = new ImageIcon("background.png"); // 设置背景图片
            backgroundLabel.setIcon(backgroundImage);
            backgroundLabel.setLayout(null);
            backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());

            // 设置按钮位置
            int buttonWidth = 100;
            int buttonHeight = 30;
            int buttonX = 330;
            int buttonY = 415;
            startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
            backgroundLabel.add(startButton);

            // 创建内容面板并添加背景图片标签
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());
            contentPanel.add(backgroundLabel, BorderLayout.CENTER);
            this.add(contentPanel);

            // 确保关闭窗口时不退出程序
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose(); // 只关闭对话框
                }
            });
        }
    }

    // 游戏窗口
    public void initWindow() {
        // 设置窗口大小
        this.setSize(450, 650);
        // 设置窗口居中
        this.setLocationRelativeTo(null);
        // 设置释放窗体
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口大小不可变
        this.setResizable(false);
        // 设置标题
        this.setTitle("我的俄罗斯方块游戏");
    }

    //初始化游戏界面
    /*
    1.JPanel介绍
    JPanel 是 Java Swing 库中的一个类，用于创建面板，这是一个可以容纳其他 GUI 组件（如按钮、文本字段和表格）的容器。
    JPanel 继承自 JComponent 类，提供了一块可以绘制组件和图形的空间。
    面板默认使用流式布局管理器（FlowLayout），但可以设置为其他布局管理器以满足不同的布局需求。
    构造函数：JPanel(LayoutManager layout)：创建一个新的 JPanel，指定一个布局管理器。
     2.GridLayout介绍
     new GridLayout(rows, cols, hgap, vgap);
     rows - 行数，cols - 列数，hgap - 水平间距，vgap - 垂直间距
     */
    private JPanel game_main;
    public void initGamePanel() {
        game_main= new JPanel();
        game_main.setLayout(new GridLayout(model.game_x, model.game_y, 6, 6));
        game_main.requestFocus();
        game_main.setVisible(true);
        for (int i = 0; i < model.getText().length; i++) {
            for (int j = 0; j < model.getText()[i].length; j++) {
                model.getText()[i][j] = new JTextArea(1, 1);
                model.getText()[i][j].setBackground(Color.WHITE);

                // 设置边界
                //检查当前文本区域是否位于每一行的的第一列，最后一列或最后一行
                if (j == 0 || j ==  model.getText()[i].length - 1 || i ==  model.getText().length - 1) {
                    model.getText()[i][j].setBackground(Color.decode("#ccffff"));//满足条件则设置为浅蓝色
                    model.setDataSpecific(i, j, 1);
                }

                model.getText()[i][j].setEditable(false);
                game_main.add(model.getText()[i][j]);
            }
        }
        this.setLayout(new BorderLayout());
        this.add(game_main, BorderLayout.CENTER);
        game_main.setFocusable(true);
        game_main.requestFocusInWindow();
    }
    public JPanel getGame_main(){
        return game_main;
    }


    //初始化左说明面板
    public void initExplainPanel() {
        JPanel explain_left = new JPanel();
        explain_left.setLayout(new BoxLayout(explain_left, BoxLayout.Y_AXIS));
        explain_left.add(new JLabel("------------------------------------"));
        explain_left.add(new JLabel("游戏说明:"));
        explain_left.add(new JLabel("按空格键,方块变形"));
        explain_left.add(new JLabel("按左箭头,方块左移"));
        explain_left.add(new JLabel("按右箭头,方块右移"));
        explain_left.add(new JLabel("按下箭头,方块下落"));
        explain_left.add(new JLabel("按下键盘p键,游戏暂停！"));
        label1.setForeground(Color.BLUE);
        label.setForeground(Color.BLUE);
        explain_left.add(new JLabel("------------------------------------"));
        explain_left.add(label);
        explain_left.add(new JLabel("------------------------------------"));
        explain_left.add(label1);
        explain_left.add(new JLabel("------------------------------------"));

        // 创建普通模式按钮
        commonButton = new JButton(label6);
        commonButton.setName("commonButton");

        // 创建困难模式按钮
        difficultyButton = new JButton(label7);
        difficultyButton.setName("difficultyButton");


        // 创建游戏暂停按钮
        pauseButton = new JButton(label8);
        pauseButton.setName("pauseButton");


        // 创建关闭音乐或音乐继续的按钮
        closeMusicButton = new JButton(label10);
        closeMusicButton.setName("closeMusicButton");


        // 创建退出游戏按钮
        exitButton = new JButton(label9);
        exitButton.setName("exitButton");


        // 将五个按钮添加到左说明面板上
        explain_left.add(commonButton);
        explain_left.add(difficultyButton);
        explain_left.add(pauseButton);
        explain_left.add(closeMusicButton);
        explain_left.add(exitButton);

        this.add(explain_left, BorderLayout.WEST);
    }

    public JButton getCommonButton() {
        return commonButton;
    }

    public JButton getDifficultyButton() {
        return difficultyButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getCloseMusicButton() {
        return closeMusicButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JLabel getLabel1() {
        return label1;
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }
}