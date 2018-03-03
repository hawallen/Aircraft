package com.hawallen.aircraft.util;

import java.util.Random;

public class Calculator {

	/**
	 * ����һ�������
	 * 
	 * @param botton
	 * @param top
	 * @return
	 */
	public static int utilRandom(int botton, int top) {
		return ((Math.abs(new Random().nextInt()) % (top - botton)) + botton);
	}
}
