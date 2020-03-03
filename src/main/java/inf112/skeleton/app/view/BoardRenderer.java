package inf112.skeleton.app.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.skeleton.app.model.GameModel;
import inf112.skeleton.app.model.Robot;
import inf112.skeleton.app.model.board.Direction;

import java.util.IdentityHashMap;

public class BoardRenderer extends OrthogonalTiledMapRenderer {
    private final GameModel gameModel;
    private TextureRegion robotFacingUp;
    private IdentityHashMap<Robot, TiledMapTileLayer.Cell> robotsToCellsHashMap;

    public BoardRenderer(TiledMap map, float unitScale, GameModel gameModel) {
        super(map, unitScale);
        this.gameModel = gameModel;
        loadTextures();
    }

    @Override
    public void render() {
        super.render();
        clearRobotsFromMap();
        renderRobots();
    }

    private void loadTextures() {
        robotFacingUp = new TextureRegion(new Texture("Player/Mechs/Mech1A_north.png"));
        TiledMapTileLayer.Cell robotCell = new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(robotFacingUp));
        robotsToCellsHashMap = new IdentityHashMap<>();
        // associate robots with cells for the robot layer of the map
        robotsToCellsHashMap.put(gameModel.getRobot(), robotCell);
    }

    public void renderRobots() {
        clearRobotsFromMap();
        for (Robot robot : gameModel.getRobots()) {
            // get the cell for that robot or use the default robot texture to create a new cell
            TiledMapTileLayer.Cell cell = robotsToCellsHashMap.getOrDefault(robot, new TiledMapTileLayer.Cell().
                    setTile(new StaticTiledMapTile(robotFacingUp)));
            rotateCellToMatchRobot(robot, cell);
            gameModel.getTiledMapHandler().getRobotLayer().setCell(robot.getX(), robot.getY(), cell);
        }
    }

    private void clearRobotsFromMap() {
        for (int i = 0; i < gameModel.getTiledMapHandler().getWidth(); i++) {
            for (int j = 0; j < gameModel.getTiledMapHandler().getWidth(); j++) {
                gameModel.getTiledMapHandler().getRobotLayer().setCell(i, j, null);
            }
        }
    }

    private void rotateCellToMatchRobot(Robot robot, TiledMapTileLayer.Cell cell) {
        // assuming the texture in the cell is facing north when rotation is 0
        Direction direction = robot.getDirection();
        new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(robotFacingUp));
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