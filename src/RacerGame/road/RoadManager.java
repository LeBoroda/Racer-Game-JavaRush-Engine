package RacerGame.road;


import RacerGame.PlayerCar;
import RacerGame.RacerGame;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private static final int PLAYER_CAR_DISTANCE = 12;
    private List<RoadObject> items = new ArrayList<>();

    private int passedCarsCount = 0;

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if(type == RoadObjectType.THORN) {
            return new Thorn(x, y);
        } else if(type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        } else {
            return new Car(type, x, y);
        }
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);
        RoadObject roadObject = createRoadObject(type, x, y);
        if (roadObject != null && isRoadSpaceFree(roadObject)) {
            items.add(roadObject);
        }
    }

    public void draw(Game game) {
        for(RoadObject object : items) {
            object.draw(game);
        }
    }

    public void move(int boost) {
        for(RoadObject object : items) {
            object.move(boost+object.speed, items);
        }
        deletePassedItems();
    }

    private boolean isThornExists() {
        for(RoadObject object : items) {
            if(object.type == RoadObjectType.THORN) {
                return true;
            }
        }
        return false;
    }

    private void generateThorn(Game game) {
        if(game.getRandomNumber(100) < 10 && !isThornExists()) {
            addRoadObject(RoadObjectType.THORN, game);
        }
    }

    public void generateNewRoadObjects(Game game) {
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {
        Iterator iterator = items.iterator();
        while(iterator.hasNext()) {
            RoadObject object = (RoadObject) iterator.next();
            if(object.y >= RacerGame.HEIGHT) {
                if(object.type == RoadObjectType.THORN) {
                    iterator.remove();
                } else {
                    iterator.remove();
                    passedCarsCount++;
                }
            }
        }
    }

    public boolean checkCrush(PlayerCar player) {
        for(RoadObject object : items) {
            if(object.isCollision(player))
                return true;
        }
        return false;
    }

    private void generateRegularCar(Game game) {
        int carTypeNumber = game.getRandomNumber(4);
        if(game.getRandomNumber(100) < 30) {
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        for(RoadObject roadObject : items) {
            if(roadObject.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)){
                return false;
            }
        }
        return true;
    }

    private boolean isMovingCarExists() {
        for(RoadObject object : items) {
            if(object.type == RoadObjectType.DRUNK_CAR) {
                return true;
            }
        }
        return false;
    }

    private void generateMovingCar(Game game) {
        if(game.getRandomNumber(100) < 10 && !isMovingCarExists()) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    public int getPassedCarsCount() {
        return passedCarsCount;
    }


}
