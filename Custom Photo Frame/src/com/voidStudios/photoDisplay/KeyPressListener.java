package com.voidStudios.photoDisplay;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class KeyPressListener implements KeyListener {
	
	public KeyPressListener(View view) {
		view.addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}
	
	@SuppressWarnings("unused")
	public void keyTyped(KeyEvent e) {}

	@SuppressWarnings("unused")
	public void keyReleased(KeyEvent e) {}
}
