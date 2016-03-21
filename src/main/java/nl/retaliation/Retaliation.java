package nl.retaliation;


import java.util.ArrayList;
import java.util.Vector;

import nl.han.ica.OOPDProcessingEngineHAN.Engine.GameEngine;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.GameObject;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.Sprite;
import nl.han.ica.OOPDProcessingEngineHAN.Tile.TileMap;
import nl.han.ica.OOPDProcessingEngineHAN.Tile.TileType;
import nl.han.ica.OOPDProcessingEngineHAN.View.View;
import nl.han.ica.OOPDProcessingEngineHAN.View.Viewport;
import nl.retaliation.building.Building;
import nl.retaliation.dashboard.Minimap;
import nl.retaliation.dashboard.Selection;
import nl.retaliation.level.GrassTile;
import nl.retaliation.level.WaterTile;
import nl.retaliation.logic.LevelGenerator;
import nl.retaliation.logic.Vector2;
import nl.retaliation.networking.Client;
import nl.retaliation.networking.Server;
import nl.retaliation.unit.*;
import processing.core.PApplet;

/**
 * Main in-game class
 * 
 * @author Luke Nooteboom
 * @author Jonathan Vos
 *
 */
@SuppressWarnings("serial")
public class Retaliation extends GameEngine { /* OOPG = Object oriented piece of garbage */	
	private final int TILESIZE = 32;
	private View view;
	private Viewport viewport;
	
	//private Player currentPlayer;
	private Server currentServer;
	private Client currentClient;
	private boolean isServer = false;
	
	private Minimap minimap;
	
	private ArrayList<IRTSObject> allObjects = new ArrayList<IRTSObject>();
	
	private ArrayList<IRTSObject> selectedUnits = null;
	private ArrayList<Selection> selections = null;
	private Vector2 corMousePressed = null, corMouseReleased = null;

	public static void main(String[] args) {
		PApplet.main(new String[]{"nl.retaliation.Retaliation"});
	}

	@Override
	public void setupGame() {
		if (isServer) {
			currentServer = new Server(63530);
		} else {
			currentClient = new Client("localhost", 63530);
		}
		
		
		ArrayList<Unit> units = new ArrayList<Unit>();
		ArrayList<Building> buildings = new ArrayList<Building>();
		
		
		
		units.add(new SovIFV(6 * TILESIZE, 6 * TILESIZE, TILESIZE));
		//units.add(new SovIFV(11 * TILESIZE, 6 * TILESIZE, TILESIZE));
		//units.add(new SovIFV(6 * TILESIZE, 6 * TILESIZE, TILESIZE));
		units.add(new SovIFV(10 * TILESIZE, 10 * TILESIZE, TILESIZE));
		units.add(new SovMiG(13 * TILESIZE, 13 * TILESIZE, TILESIZE));
		units.add(new SovMiG(16 * TILESIZE, 16 * TILESIZE, TILESIZE));
//		buildings.add(new HQRed(12, 12, TILESIZE));
//		buildings.add(new HQRed(3, 3, TILESIZE));
//		buildings.add(new HQRed(2, 2, TILESIZE));
//		buildings.add(new HQRed(1, 1, TILESIZE));
		
		for(Unit unit : units){
			addGameObject(unit);
			
		}
		for(Building building : buildings){
			addGameObject(building);
		}
		
		initTileMap();
		viewPort(800, 600);
		minimap = new Minimap(0, 0, this.getTileMap());
		addDashboard(minimap);
		setFPSCounter(true);
	}

	@Override
	public void update() {
		deleteDeadGameObjects();
		allObjects = vectorToArrayList(getGameObjectItems());
		
		if (currentServer != null) {
			currentServer.sendData(allObjects, tileMap);
		}
		if (currentClient != null) {
			ArrayList<IRTSObject> newObjects = currentClient.transceiveData(tileMap);
			if (newObjects != null) {
				deleteAllGameOBjects();
				for (IRTSObject newObject : newObjects) {
					newObject.addToEngine(this);
				}
			}
		}
		
	}
	
	@Override
	public void keyPressed() {
		int moveSpeed = 32;
		switch (key) {
		case 'w':
			viewport.setY(viewport.getY() - moveSpeed);
			break;
		case 's':
			viewport.setY(viewport.getY() + moveSpeed);
			break;
		case 'a':
			viewport.setX(viewport.getX() - moveSpeed);
			break;
		case 'd':
			viewport.setX(viewport.getX() + moveSpeed);
			break;
		}
	}
	
	@Override
	public void mousePressed(){
		if(mouseButton == LEFT){
			int xTile = (int) ((viewport.getX() + mouseX) / TILESIZE);
			int yTile = (int) ((viewport.getY() + mouseY) / TILESIZE);
		
			corMousePressed = new Vector2(xTile, yTile);
		}
	}
	
	@Override
	public void mouseReleased(){
		if(mouseButton == LEFT){
			int xTile = (int) ((viewport.getX() + mouseX) / TILESIZE);
			int yTile = (int) ((viewport.getY() + mouseY) / TILESIZE);
			corMouseReleased = new Vector2(xTile, yTile);
			
			if(corMouseReleased.equal(corMousePressed)){
				//selectedUnits = vectorToIRTSObject(corMouseReleased);
				selectedUnits = new ArrayList<IRTSObject>();
				selectedUnits.add(vectorToIRTSObject(corMouseReleased));
			} else {
				selectedUnits = vectorsToIRTSObjects(corMousePressed, corMouseReleased);
			}
			
			removeSelection();
			selections = new ArrayList<Selection>(selectedUnits.size());
			for(IRTSObject object : selectedUnits){
				if (object != null) {
					selections.add(new Selection(this, new Sprite("nl/retaliation/media/sprites/selected.png"), TILESIZE, object));
				}
			}
			updateSelection();
		}
	}
	
	@Override
	public void mouseClicked() {
		int xTile = (int) Math.floor((viewport.getX() + mouseX) / TILESIZE);
		int yTile = (int) Math.floor((viewport.getY() + mouseY) / TILESIZE);
		Vector2 tileCor = new Vector2(xTile, yTile);
		//Tile clickedTile = tileMap.getTileOnIndex(xTile, yTile);
		
		if(mouseButton == RIGHT && selectedUnits.size() > 0){
			if(selectedUnits.get(0) instanceof Unit){
				for(IRTSObject unit : selectedUnits){
					
					
					IRTSObject target = vectorToIRTSObject(new Vector2(yTile, xTile));
					if (target != null) {
						System.out.println("target");
						selectedUnits.get(0).target(target, tileMap, allObjects);
					} else if (unit instanceof Unit){
						System.out.println("mov");
						((Unit) unit).setPath(tileCor, tileMap, allObjects, 0.1f);
					}
				}
			}
		}
		
		//u.setPath(new Vector2((int) ((viewport.getX() + mouseX) / TILESIZE), (int) ((viewport.getY() + mouseY) / TILESIZE)), tileMap, allObjects);
	}
	
	private void removeSelection(){
		if(selections != null){
			for(Selection selection : selections){
				selection.removeSelf(this);
			}
		}
	}
	
	private void updateSelection(){
		if(selections != null){
			for(Selection selection : selections){
				this.addGameObject(selection);
			}
		}
	}
	
	private ArrayList<IRTSObject> vectorsToIRTSObjects(Vector2 cor1, Vector2 cor2){
		ArrayList<IRTSObject> selectedUnits = new ArrayList<IRTSObject>(30);
		
		for(IRTSObject object: allObjects){
			if(object.getPos().between(cor1, cor2)){
				selectedUnits.add(object);
			}
		}
		
		return selectedUnits;
	}
	
	private IRTSObject vectorToIRTSObject(Vector2 cor){
		//IRTSObject selectedObject;
		
		for(IRTSObject object: allObjects){
			if(object.getPos().equal(cor)){
//				if(object instanceof AirUnit){
//					selectedObject.set(0, object);
//					return selectedObject;
//				}
//				else{
//					selectedObject.set(0, object);
//				}
				return object;
			}
		}
		
		return null;
	}
	
	private void initTileMap() {
		//temp
		Sprite grassSprite = new Sprite("src/main/java/nl/retaliation/media/sprites/grass.png");
		TileType<GrassTile> grassType = new TileType<>(GrassTile.class, grassSprite);
		Sprite waterSprite = new Sprite("src/main/java/nl/retaliation/media/sprites/water.png");
		TileType<WaterTile> waterType = new TileType<>(WaterTile.class, waterSprite);
		
		TileType<?>[] tileTypes = {grassType, waterType};
		
		LevelGenerator noise = new LevelGenerator(0000f, 128, 128);
		tileMap = new TileMap(TILESIZE, tileTypes, noise.generateNoise(0.0f));
		tileMap.setTile(3, 3, 1);
	}
	
	private void viewPort(int screenWidth, int screenHeight) {
		viewport = new Viewport(0, 0, screenWidth, screenHeight);
		view = new View(viewport, screenWidth, screenHeight);

		setView(view);
		size(screenWidth, screenHeight);
	}
	
	private ArrayList<IRTSObject> vectorToArrayList(Vector<GameObject> gameObjects) {
		ArrayList<IRTSObject> newGameObjects = new ArrayList<IRTSObject>();
		for (GameObject gameObject : gameObjects) {
			if (gameObject instanceof IRTSObject) {
				newGameObjects.add((IRTSObject) gameObject);
			}
		}
		return newGameObjects;
	}
	private void deleteDeadGameObjects() {
		GameObject currentObject;
		for (int i = 0; i < getGameObjectItems().size(); i++) {
			currentObject = getGameObjectItems().get(i);
			if (currentObject.isVisible() == false) {
				deleteGameObject(currentObject);
			}
		}
	}

}
