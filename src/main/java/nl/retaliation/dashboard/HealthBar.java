package nl.retaliation.dashboard;

import processing.core.PGraphics;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.GameObject;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.SpriteObject;
import nl.han.ica.OOPDProcessingEngineHAN.View.Viewport;
import nl.retaliation.IRTSObject;

public class HealthBar extends GameObject {
	IRTSObject object;
	int TILESIZE;
	
	public HealthBar(IRTSObject object, int TILESIZE){
		super(object.getX(), object.getY(), TILESIZE, TILESIZE);
		this.object = object;
		this.TILESIZE = TILESIZE;
	}
	
	@Override
	public void drawWithViewport(PGraphics g, Viewport viewport){
		g.fill(128);
		g.rect(object.getX(), object.getY(), TILESIZE, (TILESIZE/10));
		g.fill(255);
		g.rect(object.getX(), object.getY(), TILESIZE/2, TILESIZE/10);
	}

	@Override
	public void update() {
		
	}

	@Override
	public void draw(PGraphics g) {
		
	}
}
