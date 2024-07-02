package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;

public class ISOTile extends ApplicationAdapter {
	SpriteBatch batch;
	Viewport view;
	OrthographicCamera cam;

	HashMap<Character, Texture> tileset;
	String map;

	final int TILE_WIDTH = 32;
	final int TILE_HEIGHT = 32;

	float offset;
	float rate = 4f;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		cam = new OrthographicCamera();
		view = new FitViewport(600, 600, cam);


		tileset = new HashMap<>();

		// load tiles into tileset
		FileHandle dir = Gdx.files.internal("tiles/");
		if(dir.isDirectory()) {

			for(FileHandle file : dir.list(".png")) {
				String fname = file.toString();
				fname = fname.substring(fname.lastIndexOf('/')+1, fname.lastIndexOf('.'));

				Texture text = new Texture(file);
				tileset.put(fname.charAt(0), text);
			}
		}

		map = Gdx.files.internal("maps/map.txt").readString();
	}

	public Vector2 tileToScreen(Vector2 tilePosition) {
		Vector2 out = new Vector2();

		out.x = tilePosition.x * TILE_WIDTH/2 + tilePosition.y * -TILE_WIDTH/2;
		out.y = tilePosition.x * -0.25f*TILE_HEIGHT + tilePosition.y * -0.25f*TILE_HEIGHT;

		return out;
	}

	@Override
	public void render () {
		ScreenUtils.clear(0.1f, 0, 0.25f, 1);

		// cool little animation
		if(offset >= 10 || offset <= -10) {
			rate = - rate;
		}

		offset += rate * Gdx.graphics.getDeltaTime();

		batch.setProjectionMatrix(cam.combined);
		batch.begin();

		int x = 0;
		int y = 0;
		for(int i = 0; i<map.length(); i++) {
			char ch = map.charAt(i);

			// handle CRLF or just LF
			switch(ch) {
				case '\r':
					i++; // skip LF

				case '\n':
					x = 0;
					y++;
					continue;
			}

			Texture tile = tileset.get(ch);

			Vector2 screenPos = tileToScreen(new Vector2(x, y));

			// cool little animation
			if(x == y) screenPos.y += offset;

			batch.draw(tile, screenPos.x, screenPos.y);

			x++;
		}
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		view.update(width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();

		for(Texture text : tileset.values()) {
			text.dispose();
		}
	}
}
