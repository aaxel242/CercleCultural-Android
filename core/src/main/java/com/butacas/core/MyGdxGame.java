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
	private Texture texAvailable;
	private Texture texSelected;

	private List<Seat> seats;

	// Grid config
	private static final int ROWS = 5;
	private static final int COLS = 8;
	private static final float PADDING = 10f;
	private static final float SEAT_WIDTH = 120f;
	private static final float SEAT_HEIGHT = 120f;
	private float START_X;
	private static final float START_Y = 400f;

	@Override
	public void create() {
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(this);

		texAvailable = new Texture("imgButaca.png");
		texSelected = new Texture("imgButacaSelected.png");

		float totalGridWidth = COLS * SEAT_WIDTH + (COLS - 1) * PADDING;
		START_X = (Gdx.graphics.getWidth() - totalGridWidth) / 2f;

		seats = new ArrayList<>(ROWS * COLS);
		int idCounter = 1;
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				float x = START_X + c * (SEAT_WIDTH + PADDING);
				float y = START_Y - r * (SEAT_HEIGHT + PADDING);
				seats.add(new Seat(
						idCounter++, c, r,
						Seat.State.AVAILABLE,
						x, y
				));
			}
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.9f, 0.95f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		for (Seat s : seats) {
			Texture t = s.getState() == Seat.State.SELECTED ? texSelected : texAvailable;
			batch.draw(t, s.getX(), s.getY(), SEAT_WIDTH, SEAT_HEIGHT);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		texAvailable.dispose();
		texSelected.dispose();
	}

	// ---------------- Public API ----------------

	/**
	 * Selecciona o deselecciona la butaca dada por columna y fila.
	 */
	public void selectSeat(int col, int row) {
		for (Seat seat : seats) {
			if (seat.getCol() == col && seat.getRow() == row) {
				seat.setState(
						seat.getState() == Seat.State.AVAILABLE
								? Seat.State.SELECTED
								: Seat.State.AVAILABLE
				);
				break;
			}
		}
	}

	// ---------------- InputProcessor -----------------

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float y = Gdx.graphics.getHeight() - screenY;
		// Delegar a selectSeat, calculando col y row aproximado
		for (Seat s : seats) {
			if (screenX >= s.getX() && screenX <= s.getX() + SEAT_WIDTH
					&& y >= s.getY() && y <= s.getY() + SEAT_HEIGHT) {
				selectSeat(s.getCol(), s.getRow());
				System.out.println("Butaca seleccionada: col=" + s.getCol() + " row=" + s.getRow());
				break;
			}
		}
		return true;
	}

	@Override public boolean keyDown(int keycode) {return false;}
	@Override public boolean keyUp(int keycode) {return false;}
	@Override public boolean keyTyped(char character) {return false;}
	@Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
	@Override public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
	@Override public boolean mouseMoved(int screenX, int screenY) {return false;}
	@Override public boolean scrolled(float amountX, float amountY) {return false;}
}