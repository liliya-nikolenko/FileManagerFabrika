package filemanager;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final Model model = new Model();

        final Model model2 = new Model();
        //Thread m2Thread = new Thread(model2);
        //m2Thread.start();
        final View view = new View(model, model2);
        Controller controller = new Controller(model, model2, view);
        model.addObserver(view);
        model2.addObserver(view);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                view.setVisible(true);
                System.out.println("view run");
            }
        });

    }

}
