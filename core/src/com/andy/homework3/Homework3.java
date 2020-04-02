package com.andy.homework3;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Segment;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class Homework3 extends ApplicationAdapter
{
	SpriteBatch batch;
	OrthographicCamera camera;
	Texture playerTexture,guardTexture,buildingTexture;
	Vector2 leftView, rightView;
	Vector2 home,guardNose;
	Sprite player,guard,building;
	ShapeRenderer sr;
	Ray r;
	
	
	@Override
	public void create()
	{
		sr = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.setToOrtho(false,2304,1296);
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		guardTexture = new Texture(Gdx.files.internal("guard.png"));
		buildingTexture = new Texture(Gdx.files.internal("building.png"));
		batch = new SpriteBatch();
		
		building = new Sprite(buildingTexture,1500,750);
		building.setPosition(300,300);
		player = new Sprite(playerTexture);
		guard = new Sprite(guardTexture);
		home = new Vector2((300+building.getWidth()), 300-guard.getHeight());
		player.setPosition(0,0);
		guard.setPosition(home.x,home.y);
		leftView = new Vector2();
		rightView=new Vector2();
		r = new Ray();
	}
	
	@Override
	public void render()
	{
		guard.setRotation(90);
		//TODO Check if player is in view
		//TODO make building block view
		leftView.x =(float) Math.cos(angleToRad(guard.getRotation()+60+90));
		leftView.y = (float) Math.sin(angleToRad(guard.getRotation()+60+90));
		
		rightView.x = (float) Math.cos(angleToRad(guard.getRotation()-60+90));
		rightView.y = (float) Math.sin(angleToRad(guard.getRotation()-60+90));
		
		
		if(inView())
			System.out.println("In view");
		
		draw();
	}
	public float angleToRad(float angle)
	{
		return (float) (angle*(Math.PI/180));
	}
	public void draw()
	{
		batch.setProjectionMatrix(camera.combined);
		Gdx.gl.glClearColor(0,.45f,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		player.draw(batch);
		guard.draw(batch);
		building.draw(batch);
		batch.end();
		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.rectLine(guard.getX()+guard.getWidth()/2,guard.getY()+guard.getHeight()/2,guard.getX()+10,guard.getY()+10,3); //TODO fix line render
		sr.end();
		camera.update();
	}
	
	public boolean inView()  //Checks if the angles between the player and the view vectors is less than 120
	{
		Vector2 a = new Vector2(leftView.x,leftView.y);
		Vector2 b = new Vector2(rightView.x,leftView.y);
		Vector2 playerVec = new Vector2(player.getX()-guard.getX(),player.getY()-guard.getY());
		
		double angle1 = Math.toDegrees(Math.acos((a.dot(playerVec))/(a.len() * playerVec.len())));
		double angle2 = Math.toDegrees(Math.acos((b.dot(playerVec))/(b.len() * playerVec.len())));
		
		if(angle1+angle2 <= 120) //check if blocked by building
		{
			Vector2 xy1 = new Vector2((guard.getX()+guard.getWidth())/2,(guard.getY()+guard.getHeight())/2);
			Vector2 xy2 = new Vector2((player.getX()+player.getWidth())/2,(player.getY()+player.getHeight())/2);
			if(!Intersector.intersectLinePolygon(xy1,xy2,new Polygon(building.getVertices())))
				return true;
			else
			{
				float[] vertices = building.getVertices();
				Array<Vector2> visible = new Array();
				for(int i=0;i<vertices.length;i+=2)
				{
					if(!Intersector.intersectLinePolygon(xy1,new Vector2(vertices[i],vertices[i+1]),new Polygon(building.getVertices())))
						visible.add(new Vector2(vertices[i],vertices[i+1]));
				}
				
			}
			
			
		return true;
		}
		else
			return false;
	}
	
	@Override
	public void dispose()
	{
		batch.dispose();
		playerTexture.dispose();
		buildingTexture.dispose();
		guardTexture.dispose();
	}
}
