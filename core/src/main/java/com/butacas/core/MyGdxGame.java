package com.butacas.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {
	private SpriteBatch batch;
	private Texture texAvailable, texSelected;
	private List<Seat> seats;

	// Configuración de la cuadrícula
	private static final int ROWS = 5, COLS = 8;
	private static final float PADDING = 10f, SEAT_SIZE = 120f;
	private float startX, startY;

	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(this);

		// Carga y asigna las texturas CORRECTAMENTE
		texAvailable = new Texture(Gdx.files.internal("imgbutaca.png"));
		texSelected  = new Texture(Gdx.files.internal("imgbutacaselected.png"));

		seats = new ArrayList<>(ROWS * COLS);
		float totalWidth = COLS * SEAT_SIZE + (COLS - 1) * PADDING;
		startX = (Gdx.graphics.getWidth() - totalWidth) * 0.5f;
		startY = Gdx.graphics.getHeight() * 0.7f;

		int id = 1;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				float x = startX + c * (SEAT_SIZE + PADDING);
				float y = startY - r * (SEAT_SIZE + PADDING);
				seats.add(new Seat(id++, c, r, Seat.State.AVAILABLE, x, y));
			}
		}
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		for (Seat s : seats)
		{
			Texture tex = s.getState() == Seat.State.SELECTED ? texSelected : texAvailable;
			batch.draw(tex, s.getX(), s.getY(), SEAT_SIZE, SEAT_SIZE);
		}
		batch.end();
	}

	@Override
	public void dispose()
	{
		batch.dispose();
		texAvailable.dispose();
		texSelected.dispose();
	}

	public void toggleSeat(int col, int row)
	{
		for (Seat s : seats)
		{
			if (s.getCol() == col && s.getRow() == row)
			{
				s.setState(s.getState() == Seat.State.AVAILABLE
						? Seat.State.SELECTED
						: Seat.State.AVAILABLE);
				break;
			}
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		float y = Gdx.graphics.getHeight() - screenY;
		for (Seat s : seats)
		{
			if (screenX >= s.getX() && screenX <= s.getX() + SEAT_SIZE
					&& y >= s.getY() && y <= s.getY() + SEAT_SIZE)
			{
				toggleSeat(s.getCol(), s.getRow());
				break;
			}
		}
		return true;
	}

	@Override
	public boolean keyDown(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int p, int b)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int p)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y)
	{
		return false;
	}

	@Override
	public boolean scrolled(float dx, float dy)
	{
		return false;
	}
	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}
}
