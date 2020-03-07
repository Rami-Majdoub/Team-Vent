package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.model.board.MapHandler;

public class UIDemo implements ApplicationListener {

    private TiledMap map;
    private TiledMapRenderer renderer;
    private OrthographicCamera camera;

    Skin skin;
    Stage stage;
    Table rootTable;
    int bottomTableHeight;

    @Override
    public void create () {
        skin = new Skin(Gdx.files.internal("Skin/shade/skin/uiskin.json"));
        stage = new Stage(new ScreenViewport());

        // Important: the height of the bottom table determines the width and height of the map. The map scales itself
        // to fit within the space that remains
        this.bottomTableHeight = 150;

        int calc = (Gdx.graphics.getHeight() - bottomTableHeight);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, ((float) Gdx.graphics.getWidth() / (Gdx.graphics.getHeight() -
                bottomTableHeight)) * 10, 10);
        camera.update();

        MapHandler mapHandler = new MapHandler("map-1.tmx");
        map = mapHandler.getMap();
        renderer = new OrthogonalTiledMapRenderer(map, 1/100f);

        // The main table that takes up the entire screen
        rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.add().expand();
        rootTable.debug();

        // The sidebar
        Table sidebar = new Table(skin);
        sidebar.add("STATS");
        rootTable.add(sidebar).width(376); // todo: calculate this number as a function of the map width
        rootTable.row();

        // The bottom table
        Table bottomTable = new Table(skin);
        bottomTable.add("CARDS");
        rootTable.add(bottomTable).height(bottomTableHeight);

        stage.addActor(rootTable);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.act();
        stage.draw();

        // Prepare for embedded map drawing by applying the desired viewport for the map
        Gdx.gl.glViewport(0, bottomTableHeight, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight() - bottomTableHeight);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize (int width, int height) {
        rootTable.setFillParent(true);
    }

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "RoboRally";
        cfg.width = 1366;
        cfg.height = 768;
        cfg.resizable = true;
        new LwjglApplication(new UIDemo(), cfg);
    }

}