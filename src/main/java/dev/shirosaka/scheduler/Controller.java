package dev.shirosaka.scheduler;

public class Controller {
    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        initView();
    }

    public void initView() {
    }

    public void initController() {
        log("Initializing controller...");
    }

    public void log(String message) {
        view.getTaDebugLog().append(message + "\n");
    }
}
