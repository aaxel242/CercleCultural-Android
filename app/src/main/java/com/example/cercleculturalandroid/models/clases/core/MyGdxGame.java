package com.example.cercleculturalandroid.models.clases.core;

import static android.graphics.Color.WHITE;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor {

	private OrthographicCamera camera;
	private static final float VIRTUAL_WIDTH = 1280;
	private static final float VIRTUAL_HEIGHT = 720;
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
		Gdx.app.log("GDX", "Inicializando juego");
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		texAvailable = new Texture(Gdx.files.internal("imgbutaca.png"));
		texSelected = new Texture("imgbutacaselected.png");

		float totalGridWidth = COLS * SEAT_WIDTH + (COLS - 1) * PADDING;
		START_X = (Gdx.graphics.getWidth() - totalGridWidth) / 2f;

		seats = new ArrayList<>(ROWS * COLS);
		int idCounter = 1;

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				float x = START_X + c * (SEAT_WIDTH + PADDING);
				float y = START_Y - r * (SEAT_HEIGHT + PADDING);
				char fila = (char) ('A' + r);
				int columna = c + 1;

				seats.add(new Seat(
						idCounter++, 1, 1,
						String.valueOf(fila), columna,
						Seat.State.AVAILABLE,
						x, y
				));
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// 1. Configurar vista de la cámara
		camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

		// 2. Recalcular posiciones relativas
		float centerX = VIRTUAL_WIDTH / 2f;
		float startY = VIRTUAL_HEIGHT * 0.7f;

		float gridWidth = COLS * SEAT_WIDTH + (COLS - 1) * PADDING;
		START_X = centerX - (gridWidth / 2f);

		// 3. Actualizar todas las butacas
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				Seat seat = seats.get(r * COLS + c);
				seat.x = START_X + c * (SEAT_WIDTH + PADDING);
				seat.y = startY - r * (SEAT_HEIGHT + PADDING);
			}
		}

		camera.update(); // <-- ¡Importante!
	}

	@Override
	public void render() {
		if (batch == null) {
			batch = new SpriteBatch();
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		for (Seat s : seats) {
			Texture t = s.state == Seat.State.SELECTED ? texSelected : texAvailable;
			batch.draw(t, s.x, s.y, SEAT_WIDTH, SEAT_HEIGHT);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		texAvailable.dispose();
		texSelected.dispose();
	}

	// ---------------- Input ----------------

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		float y = Gdx.graphics.getHeight() - screenY;

		for (Seat s : seats) {
			if (screenX >= s.x && screenX <= s.x + SEAT_WIDTH &&
					y >= s.y && y <= s.y + SEAT_HEIGHT) {

				s.state = (s.state == Seat.State.AVAILABLE)
						? Seat.State.SELECTED
						: Seat.State.AVAILABLE;

				System.out.println("ID: " + s.id +
						" | Fila: " + s.fila +
						" | Columna: " + s.columna +
						" | Estado: " + s.state);

				// Aquí podrías guardar en BD si deseas

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
