package com.samdroid.game;

import com.samdroid.audio.Audio;
import com.samdroid.file.FileIO;
import com.samdroid.game.view.Graphics;
import com.samdroid.game.view.Screen;
import com.samdroid.input.Input;

public interface Game {

    public Audio getAudio();

    public Input getInput();

    public FileIO getFileIO();

    public Graphics getGraphics();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getInitScreen();
}
