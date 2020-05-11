package RacerGame;

import RacerGame.road.RoadManager;
import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;
import com.javarush.engine.cell.Key;

public class RacerGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH/2;
    public static final int ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 5;
    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private boolean isGameStopped;
    private FinishLine finishLine;
    private ProgressBar progressBar;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        showGrid(false);
        createGame();
    }

    private void createGame() {
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        isGameStopped = false;
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        score = 3500;
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene() {
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for(int i = 0; i < HEIGHT; i++) {
            for(int j = 0; j < ROADSIDE_WIDTH; j++) {
                setCellColor(j, i, Color.GREEN);
            }
            for(int j = WIDTH-ROADSIDE_WIDTH; j < WIDTH; j++) {
                setCellColor(j, i, Color.GREEN);
            }
            for(int j = ROADSIDE_WIDTH; j < WIDTH-ROADSIDE_WIDTH; j++) {
                setCellColor(j, i, Color.DARKGRAY);
            }
            setCellColor(CENTER_X, i, Color.WHITE);
        }
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        if(x >= 0 && x <=(WIDTH-1) && y >= 0 && y <= (HEIGHT-1)){
            super.setCellColor(x, y, color);
        }
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());
    }

    @Override
    public void onTurn(int step) {
        if(roadManager.checkCrush(player)) {
            gameOver();
            drawScene();
        } else {
            if(roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT) {
                finishLine.show();
            }
            if(finishLine.isCrossed(player)) {
                win();
                drawScene();
            } else {
                score -= 5;
                setScore(score);
                moveAll();
                roadManager.generateNewRoadObjects(this);
                drawScene();
            }
        }
    }

    @Override
    public void onKeyPress(Key key) {
        if(key == Key.SPACE && isGameStopped) {
            createGame();
        }
        if(key == Key.RIGHT) {
            player.setDirection(Direction.RIGHT);
        } else if(key == Key.LEFT) {
            player.setDirection(Direction.LEFT);
        }
        if(key == Key.UP) {
            player.speed = 2;
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if(key == Key.RIGHT && player.getDirection() == Direction.RIGHT) {
            player.setDirection(Direction.NONE);
        } else if(key == Key.LEFT && player.getDirection() == Direction.LEFT) {
            player.setDirection(Direction.NONE);
        }
        if(key == Key.UP) {
            player.speed = 1;
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "GAME OVER", Color.RED, 28);
        stopTurnTimer();
        player.stop();
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GOLD, "FINISHED!", Color.SILVER, 28);
        stopTurnTimer();
    }
}
