package nl.retaliation.unit;

import java.util.ArrayList;

import nl.han.ica.OOPDProcessingEngineHAN.Objects.GameObject;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.Sprite;
import nl.han.ica.OOPDProcessingEngineHAN.Tile.TileMap;
import nl.retaliation.logic.Vector2;

/**
 * 
 * @author Jonathan Vos
 *
 */

public class AirUnit extends Unit{
	public AirUnit(float x, float y, Sprite sprite, int tileSize, float maxSpeed, int health){
		super(x, y, sprite, tileSize, maxSpeed, health);
	}
	
	public void setPath(Vector2 desiredTilePos, TileMap terrain, GameObject[] gameobjects){
		isMoving = true;
		this.desiredTilePos = desiredTilePos;
		this.currentPath = setPath(desiredTilePos, this.tilePosition);
	}
	
	private ArrayList<Vector2> setPath(Vector2 desiredTilePos, Vector2 pos){
		Vector2 currentPos = pos;
		ArrayList<Vector2> path = new ArrayList<Vector2>(0);
		int deltaX, deltaY;
		
		while(!currentPos.equal(desiredTilePos)){
			if(currentPos.getX() < desiredTilePos.getX()){
				deltaX = 1;
			} else if(currentPos.getX() == desiredTilePos.getX()){
				deltaX = 0;
			} else{ // currentPos.getX() > desiredTilePos.getX()
				deltaX = -1;
			}
			
			if(currentPos.getY() < desiredTilePos.getY()){
				deltaY = 1;
			} else if(currentPos.getY() == desiredTilePos.getY()){
				deltaY = 0;
			} else{ // currentPos.getY() > desiredTilePos.getY()
				deltaY = -1;
			}
			
			currentPos = new Vector2(currentPos.getX() + deltaX, currentPos.getY() + deltaY);
			
			path.add(currentPos);
		}
		
		return path;
	}

	@Override
	public void destroy() {
		// Boom!
	}
}
