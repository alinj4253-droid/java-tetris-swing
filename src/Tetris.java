public class Tetris {
    private Model model;
    private View view;
    private Controller controller;

    public static void main(String[] args) {
        while (Model.isrunning){
            Tetris tertris = new Tetris();
            tertris.model.game_begin();
        }
    }

    public Tetris() {
        model = new Model();
        view = new View(model);
        controller = new Controller(model, view);
    }
}
