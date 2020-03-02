package inf112.skeleton.app.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Timer;
import inf112.skeleton.app.model.board.Direction;
import inf112.skeleton.app.model.board.Location;
import inf112.skeleton.app.model.board.MapHandler;
import inf112.skeleton.app.model.cards.IProgramCard;
import inf112.skeleton.app.model.cards.MoveForwardCard;
//import org.graalvm.compiler.lir.sparc.SPARCMove;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class GameModel {

    private Robot robot;
    private MapHandler tiledMapHandler;
    private Player player;
    private int phaseNumber;
    private Timer timer;

    private Timer.Task doCardTimed;
    private Timer.Task doTilesTimed;
    private Timer.Task updatePhaseNumber;


    private ArrayList<Deque<Location>> cardSteps = new ArrayList<>();
    private ArrayList<Deque<Location>> tileSteps = new ArrayList<>();



    public GameModel() {
        robot = new Robot();
        player = new Player();
        player.generateCardHand();
        timer = new Timer();
        phaseNumber = 0;
        tiledMapHandler = new MapHandler("map-1.tmx");
    }

    public Robot getRobot() {
        return this.robot;
    }

    public TiledMap getBoard() {
        return this.tiledMapHandler.getMap();
    }

    public Player getPlayer() {
        return this.player;
    }

    public void endTurn() {
        Location loc = robot.getLocation();

        cardSteps.add(new LinkedList<>());
        cardSteps.set(0, doCard(0, cardSteps.get(0), loc));
        loc = updateLastLoc(loc, cardSteps.get(0));

        tileSteps.add(new LinkedList<>());
        tileSteps.set(0, doTiles(0, tileSteps.get(0), loc));
        loc = updateLastLoc(loc, tileSteps.get(0));

        for (int i = 1; i < 5; i++) {
            cardSteps.add(new LinkedList<>());
            cardSteps.set(i,doCard(i, cardSteps.get(i), loc));
            loc = updateLastLoc(loc, cardSteps.get(i));

            tileSteps.add(new LinkedList<>());
            tileSteps.set(i, doTiles(i, tileSteps.get(i), loc));
            loc = updateLastLoc(loc, tileSteps.get(i));

        }
        int delay = 0;
        for (int i = 0; i < 5; i++) {
            scheduleDoCardTimed(delay, i);
            delay += cardSteps.get(i).size();
            scheduleDoTilesTimed(delay, i);
            delay += tileSteps.get(i).size();
            scheduleUpdatePhaseNumber(delay);
            delay += 1;
        }
        player.generateCardHand();
    }

    private Deque<Location> doTiles(int phaseNumber, Deque<Location> cardSteps, Location initialLoc) {
        String currentTileType =  tiledMapHandler.getTileType(initialLoc.getPosition(), "Tile");
        Direction currentTileDirection = tiledMapHandler.getDirection(initialLoc.getPosition(), "Tile");

        switch(currentTileType != null ? currentTileType: "none"){
            case("conveyor_normal"):
                cardSteps.add(initialLoc.moveDirection(currentTileDirection));
                break;
            case("conveyor_express"):
                cardSteps.add(initialLoc.moveDirection(currentTileDirection));
                initialLoc = cardSteps.getLast();
                String nextTileType = tiledMapHandler.getTileType(initialLoc.getPosition(), "Tile");
                Direction nextTileDirection = tiledMapHandler.getDirection(initialLoc.getPosition(), "Tile");
                if ("conveyor_express".equals(nextTileType)) {
                    cardSteps.add(initialLoc.moveDirection(nextTileDirection));
                }
                break;
            case("gear_clockwise"):
                cardSteps.add(new Location(initialLoc.getPosition(), initialLoc.getDirection().right()));
                break;
            case("gear_counterclockwise"):
                cardSteps.add(new Location(initialLoc.getPosition(), initialLoc.getDirection().left()));
                break;
            default:
        }
        return cardSteps;
    }

    private Deque<Location> doCard (int phaseNumber, Deque<Location> cardSteps, Location initialLoc) {
        IProgramCard card = player.getCardInProgrammingSlot(phaseNumber);
        player.setCardinProgrammingSlot(phaseNumber, null);
        if (card != null) {
            if (!(card instanceof MoveForwardCard && tiledMapHandler.wallInPath(initialLoc))){
                cardSteps.add(card.instruction(initialLoc.copy()));
            }
        }
        return cardSteps;

    }

    public Timer.Task scheduleDoCardTimed(int delay, int phase) {
        if (cardSteps.get(phase).size() == 0) {return null;}
        doCardTimed = new Timer.Task() {
            @Override
            public void run() {
                robot.setLocation(cardSteps.get(phase).remove());
            }
        };
        Timer.instance().scheduleTask(doCardTimed, delay, 1, cardSteps.get(phase).size() - 1);
        return doCardTimed;
    }

    public  Timer.Task scheduleDoTilesTimed(int delay, int phase) {
        if (tileSteps.get(phase).size() == 0) {return null;}
        doTilesTimed = new Timer.Task() {
            @Override
            public void run() {
                robot.setLocation(tileSteps.get(phase).remove());
            }
        };
        Timer.instance().scheduleTask(doTilesTimed, delay, 1, tileSteps.get(phase).size()-1);
        return doTilesTimed;
    }

    public Timer.Task scheduleUpdatePhaseNumber(int delay) {
        updatePhaseNumber = new Timer.Task() {
            @Override
            public void run() {
                phaseNumber++;
            }
        };
        Timer.instance().scheduleTask(updatePhaseNumber, delay, 1, 0);
        return updatePhaseNumber;
    }

    private  Location updateLastLoc (Location loc, Deque<Location> locations) {
        //System.out.println(locations.pollLast().toString());
        if (locations.peekLast() != null) { System.out.println("asdasd"); return locations.pollLast();}
        return loc;
    }

    public boolean inTestMode() {
        return true;
    }
}
