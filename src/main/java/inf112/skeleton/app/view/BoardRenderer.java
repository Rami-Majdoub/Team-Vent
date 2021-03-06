package inf112.skeleton.app.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.skeleton.app.model.GameModel;
import inf112.skeleton.app.model.Robot;
import inf112.skeleton.app.model.board.Direction;

import java.util.IdentityHashMap;

public class BoardRenderer extends OrthogonalTiledMapRenderer {
    private final GameModel gameModel;
    private IdentityHashMap<Robot, TiledMapTileLayer.Cell> robotsToCellsHashMap;

    public BoardRenderer(GameModel gameModel) {
        super(gameModel.getMapHandler().getMap(), 1 / gameModel.getMapHandler().getTileLayer().
                getTileWidth());
        this.gameModel = gameModel;
        loadTextures();
    }

    @Override
    public void render() {
        super.render();
        clearRobotsFromMap();
        renderRobots();
    }

    //TODO update for several robots instead of static 0
    private void loadTextures() {
        TextureRegion robotFacingNorth = new TextureRegion(new Texture("Player/Mechs/Mech1A_north.png"));
        TextureRegion robot2FacingNorth = new TextureRegion(new Texture("Player/Mechs/Mech2.png"));
        TextureRegion robot3FacingNorth = new TextureRegion(new Texture("Player/Mechs/Mech3.png"));
        TextureRegion robot4FacingNorth = new TextureRegion(new Texture("Player/Mechs/Mech4.png"));
        TiledMapTileLayer.Cell robotCell;
        robotsToCellsHashMap = new IdentityHashMap<>();
        int i = 0;
        // associate robots with cells for the robot layer of the map
        for (Robot robot : gameModel.getRobots()) {
            robotCell = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(robotFacingNorth));

            i++;
            if (i ==2){
                robotCell = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(robot2FacingNorth));
            }
            if (i == 3){
                robotCell = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(robot3FacingNorth));
            }
            if (i==4){
                robotCell = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(robot4FacingNorth));
            }
            robotsToCellsHashMap.put(robot, robotCell);
        }
    }

    public void renderRobots() {
        clearRobotsFromMap();
        for (Robot robot : gameModel.getRobots()) {
            if (!robot.alive()) continue;
            TiledMapTileLayer.Cell cell = robotsToCellsHashMap.get(robot);
            rotateCellToMatchRobot(robot, cell);
            gameModel.getMapHandler().getRobotLayer().setCell(robot.getX(), robot.getY(), cell);
        }
    }

    private void clearRobotsFromMap() {
        for (int i = 0; i < gameModel.getMapHandler().getWidth(); i++) {
            for (int j = 0; j < gameModel.getMapHandler().getWidth(); j++) {
                gameModel.getMapHandler().getRobotLayer().setCell(i, j, null);
            }
        }
    }

    private void rotateCellToMatchRobot(Robot robot, TiledMapTileLayer.Cell cell) {
        // assuming the texture in the cell is facing north when rotation is 0
        Direction direction = robot.getDirection();
        if (direction == Direction.NORTH) {
            cell.setRotation(0);
        } else if (direction == Direction.WEST) {
            cell.setRotation(1);
        } else if (direction == Direction.SOUTH) {
            cell.setRotation(2);
        } else if (direction == Direction.EAST) {
            cell.setRotation(3);
        } else {
            throw new IllegalArgumentException("Unexpected fifth direction");
        }
    }

}
